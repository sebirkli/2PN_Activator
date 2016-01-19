package controllers;

import de.htwg.se.tpn.util.observer.IObserver;
import de.htwg.se.tpn.controller.TpnController;
import de.htwg.se.tpn.controller.TpnControllerInterface;
import de.htwg.se.tpn.util.observer.Event;
import play.libs.F;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import play.libs.Json;
import play.mvc.*;
import play.libs.*;

class WebsocketObserver implements IObserver {

    WebSocket.Out<JsonNode> out;
    WebSocket.In<JsonNode> in;
    TpnControllerInterface controller;
    String name;
    public WebsocketObserver(TpnControllerInterface c, WebSocket.Out<JsonNode> o,
                                WebSocket.In<JsonNode> i, String nickname) {
        out = o;
        in = i;
        controller = c;
        name = nickname;
        controller.addObserver(this);
        
        in.onMessage(new F.Callback<JsonNode>() {
            public void invoke(JsonNode event) {
                JsonNode eventType = event.get("eventType");
                if (eventType == null) {
                    return;
                }
                if (eventType.asText().equals("newGame")) {
                    try {
                        int size = Integer.parseInt(event.get("size").asText());
                        int nrnew = Integer.parseInt(event.get("new").asText());
                        controller.gameInit(size, nrnew);
                    } catch (Exception e) {
                    }
                } else if (eventType.asText().equals("command")) {
                    controller.processInput(event.get("d").asText());
                }
            }
        });
    }
 
    boolean end = false;
 
    @Override
    public void update(Event e) {
        if (e instanceof TpnControllerInterface.NewFieldEvent) {
			updateValues();
		} else if (e instanceof TpnControllerInterface.GameOverEvent && !end) {
			end = true;
		    gameOver();
		} else if (e instanceof TpnControllerInterface.NewGameEvent) {
			end = false;
			updateValues();
		}
    } 
    
    private void updateValues() {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance ;
        ObjectNode node = nodeFactory.objectNode();
        node.put("eventType", "UpdateView");
        
        int fieldSize = controller.getSize();
        
        ArrayNode grid = new ArrayNode(nodeFactory);
        for (int i = 0; i < fieldSize; ++i) {
            ArrayNode row = new ArrayNode(nodeFactory);
            for (int j = 0; j < fieldSize; ++j) {
                ObjectNode valNode = nodeFactory.objectNode();
                valNode.put("value", controller.getValue(i, j));
                row.add(valNode);
            }
            grid.add(row);
        }

        node.put("fieldSize", fieldSize);
        node.put("grid", grid);
        
        out.write(node);
    }
    
    private void gameOver() {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance ;
        ObjectNode node = nodeFactory.objectNode();
        node.put("eventType", "GameOver");
        
        int points = 0;
        int fieldSize = controller.getSize();
        for (int i = 0; i < fieldSize; ++i) {
            for (int j = 0; j < fieldSize; ++j) {
                points += controller.getValue(i, j);
            }
        }
        node.put("name", name);
        node.put("points", points);
        
        out.write(node);
    }
}