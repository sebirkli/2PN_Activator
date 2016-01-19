package controllers;

// Game
import de.htwg.se.tpn.TwoPN;
import de.htwg.se.tpn.controller.TpnController;
import de.htwg.se.tpn.controller.TpnControllerInterface;
import views.html.*;

// Fasterxml
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

// Play
import play.mvc.*;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.Http.Context;
import play.data.Form;
import play.data.DynamicForm;

// Java
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

public class Application extends Controller {

    static HashMap<String, TpnControllerInterface> uuidToController = new HashMap<>();
    static TpnControllerInterface controller = TwoPN.getInstance().getController();
    static Map<String, String> registeredUsers = new HashMap<String, String>();

    @play.mvc.Security.Authenticated(Secured.class)
    public Result index() {
        return ok(index.render(this));
    }

    public Result startGUI() {
        //TwoPN.getInstance().startGUI();
        return showGame();
    }

    @play.mvc.Security.Authenticated(Secured.class)
    public Result ajaxGame() {
        return ok(ajax.render(controller, this));
    }

    @play.mvc.Security.Authenticated(Secured.class)
    public Result publicGame() {
        controller = TwoPN.getInstance().getController();
        session("private", "false");
        return showGame();
    }

    @play.mvc.Security.Authenticated(Secured.class)
    public Result privateGame() {
        session("private", "true");
        return showGame();
    }

    private Result showGame() {
        TpnControllerInterface c = curController();
        return ok(tpn.render(c, this));
    }

    public Result jsonCommand(String command) {
        session("private", "true");
        curController().processInput(command);
        return json();
    }
    
    public TpnControllerInterface curController() {
        TpnControllerInterface c = controller;
        
        String isPrivate = session("private");
        if (isPrivate != null && isPrivate.equals("true")) {
            String uuid = curUUID();
            if (!uuidToController.containsKey(uuid)) {
                uuidToController.put(uuid, new TpnController(4, 2));
            }
            c = uuidToController.get(uuid);
        }
        return c;
    }
    
    private String curUUID() {
        String uuid = session("uuid");
        if(uuid == null) {
            uuid = java.util.UUID.randomUUID().toString();
            session("uuid", uuid);
        }
        return uuid;
    }

    public Result json() {
        TpnControllerInterface c = curController();
        
        int fieldSize = c.getSize();
        Map<String, Object> grid[][] = new HashMap[fieldSize][fieldSize];

        for (int i = 0; i < fieldSize; ++i) {
            for (int j = 0; j < fieldSize; ++j) {
                grid[i][j] = new HashMap<String, Object>();
                grid[i][j].put("value", c.getValue(i, j));
            }
        }
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("fieldSize", fieldSize);
        map.put("grid", grid);

        return ok(Json.stringify(Json.toJson(map)));
    }
    
    private LinkedList<WebSocket.Out<JsonNode>> sockets;
    
    public WebSocket<JsonNode> socket() {
        TpnControllerInterface c = curController();
        String name = session("nickname") == null ? "Player" : session("nickname");
        return new WebSocket<JsonNode>() {
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
                new WebsocketObserver(c, out, in, name);
            }
        };
    }

    // Login Authentication

    public Result login() {
        if (registeredUsers.isEmpty()) {
            registeredUsers.put("a@b.de", "1");
            registeredUsers.put("sergej@2pn.de", "sergej");
            registeredUsers.put("nico@2pn.de", "nico");
        }
        if (session("email") != null) {
            return index();
        }
        return ok(views.html.login.render(Form.form(User.class), this));
    }

    public Result signupForm() {
        return ok(views.html.signup.render(Form.form(User.class), this));
    }

    public Result logout() {
        session().clear();
        return redirect(routes.Application.index());
    }

    public Result authenticate() {
        Form<User> loginform = DynamicForm.form(User.class).bindFromRequest();
        User user = User.authenticate(loginform.get());
        if (loginform.hasErrors() || !user.loggedIn) {
            ObjectNode response = Json.newObject();
            response.put("success", false);
            response.put("errors", "");
            if (!user.loggedIn) {
                flash("errors", "Wrong username or password");
            }
            return badRequest(views.html.login.render(loginform, this));
        } else {
            session().clear();
            session("email", user.email);
            session("nickname", user.nickname());
            return redirect(routes.Application.index());
        }
    }

    public Result signup() {
        Form<User> loginform = DynamicForm.form(User.class).bindFromRequest();

        ObjectNode response = Json.newObject();
        User account = loginform.get();
        boolean exists = registeredUsers.keySet().contains(account.email);

        if (loginform.hasErrors() || exists) {
            response.put("success", false);
            response.put("errors", loginform.errorsAsJson());
            if (exists) {
                flash("errors", "Account already exists");
            }
            return badRequest(views.html.signup.render(loginform, this));
        } else {
            registeredUsers.put(account.email, loginform.get().password);
            session().clear();
            session("email", account.email);
            session("nickname", account.nickname());
            return redirect(routes.Application.index());
        }
    }

    public Result facebook(String email, String name) {
//        session().clear();
//        session("email", email);
//        session("nickname", name);
        return redirect(routes.Application.index());
    }

    public static class User {

        public String email;
        public String password;
        public boolean loggedIn;

        public static User authenticate(User user) {
            //String realPassword = registeredUsers.get(user.email);
            //boolean match = user.password.equals(realPassword);
            //return new User(user.email, user.password, match);
            return new User("email@gmx.de", "password", true);
        }

        public User() {
            this("");
        }

        public User(String email) {
            this(email, "", false);
        }

        private User(String email, String password, boolean loggedIn) {
            this.email = email;
            this.password = password;
            this.loggedIn = loggedIn;
        }

        public String nickname() {
            return email.substring(0, email.indexOf("@"));
        }
    }

    public static class Secured extends Security.Authenticator {

        @Override
        public String getUsername(Context ctx) {
            return ctx.session().get("email");
        }

        @Override
        public Result onUnauthorized(Context ctx) {
            return redirect(routes.Application.login());
        }
    }

    public String getNickName() {
        return session("nickname");
    }

    public boolean isLoggedIn() {
        return session("nickname") != null && !session("nickname").isEmpty();
    }

}
