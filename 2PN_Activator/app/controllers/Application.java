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
    
    public Result sendCommand(String command) {
        System.out.println(command);
        controller.processInput(command);
        return showGame();
    }
    
    public Result publicGame() {
        controller = TwoPN.getInstance().getController();
        
        session("script", "ws");
        return showGame();
    }
    
    public Result privateGame() {
        session("script", "ajax");
        session("private", "true");
        return showGame();
    }
    
    private Result showGame() {
        String script = session("script");
        if (script == null) {
            script = "ws";
        }
        
        TpnControllerInterface c = controller;
        String isPrivate = session("private");
        if (isPrivate != null && isPrivate.equals("true")) {

        }
        
        return ok(tpn.render(c, script));
    }

    public Result jsonCommand(String command) {
        controller.processInput(command);
        return json();
    }

    public Result json() {
        TpnControllerInterface c = controller;
        String uuid = session("uuid");
        
        System.out.printf("json request, uuid: %s\n", uuid);
        
        if(uuid == null) {
            uuid = java.util.UUID.randomUUID().toString();
            session("uuid", uuid);
        }
        if (!uuidToController.containsKey(uuid)) {
            System.out.printf("newController, mapsize: %d\n", uuidToController.size());
            uuidToController.put(uuid, new TpnController(4, 2));
        }
    
        c = uuidToController.get(uuid);
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
        return new WebSocket<JsonNode>() {
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
                new WebsocketObserver(controller, out, in);
            }
        };
    }
}
