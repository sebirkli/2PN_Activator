package controllers;

import play.*;
import play.mvc.*;
import de.htwg.se.tpn.TwoPN;
import de.htwg.se.tpn.controller.TpnController;
import de.htwg.se.tpn.controller.TpnControllerInterface;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import java.util.LinkedList;
import controllers.WebsocketObserver;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

import views.html.*;

public class Application extends Controller {

    static HashMap<String, TpnControllerInterface> uuidToController = new HashMap<>();
    static TpnControllerInterface controller = TwoPN.getInstance().getController();

    public Result index() {
        return ok(index.render());
    }
    
    public Result startGUI() {
        TwoPN.getInstance().startGUI();
        return showGame();
    }
    
    public Result ajaxGame() {
        return ok(ajax.render(controller));
    }
    
    public Result publicGame() {
        controller = TwoPN.getInstance().getController();
        session("private", "false");
        return showGame();
    }
    
    public Result privateGame() {
        session("private", "true");
        return showGame();
    }
    
    private Result showGame() {
        TpnControllerInterface c = curController();
        
        return ok(tpn.render(c));
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
        return new WebSocket<JsonNode>() {
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
                new WebsocketObserver(c, out, in);
            }
        };
    }
}
