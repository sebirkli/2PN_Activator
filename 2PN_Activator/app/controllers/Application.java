package controllers;

import play.*;
import play.mvc.*;
import de.htwg.se.tpn.TwoPN;
import de.htwg.se.tpn.controller.TpnController;
import de.htwg.se.tpn.controller.TpnControllerInterface;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.HashMap;
import java.util.Map;

import views.html.*;

public class Application extends Controller {

    static TpnControllerInterface controller = TwoPN.getInstance().getController();

    public Result index() {
        return ok(index.render());
    }
    
    public Result startGUI() {
        TwoPN.getInstance().startGUI();
        return playGame();
    }
    
    public Result sendCommand(String command) {
        System.out.println(command);
        controller.processInput(command);
        return playGame();
    }
    
    public Result playGame() {
        return ok(tpn.render(controller));
    }

    public Result jsonCommand(String command) {
        controller.processInput(command);
        return json();
    }

    public Result json() {
        int fieldSize = controller.getSize();
        
        Map<String, Object> grid[][] = new HashMap[fieldSize][fieldSize];

        for (int i = 0; i < fieldSize; ++i) {
            for (int j = 0; j < fieldSize; ++j) {
                grid[i][j] = new HashMap<String, Object>();
                grid[i][j].put("value", controller.getValue(i, j));
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("filedSize", fieldSize);
        map.put("grid", grid);

        return ok(Json.stringify(Json.toJson(map)));
    }
}
