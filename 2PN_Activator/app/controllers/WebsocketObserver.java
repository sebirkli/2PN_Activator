package controllers;

import de.htwg.se.tpn.util.observer.IObserver;
import de.htwg.se.tpn.controller.TpnController;
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

    WebSocket.Out<String> out;
    WebSocket.In<String> in;
    TpnController controller;
    public WebsocketObserver(TpnController c, WebSocket.Out<String> o,
                                WebSocket.In<String> i) {
        out = o;
        in = i;
        controller = c;
        c.addObserver(this);
        
        in.onMessage(new F.Callback<String>() {
            public void invoke(String event) {
                controller.processInput(event.get("d").asText());
            }
        });
    }
 
    @Override
    public void update(Event event) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance ;
        ObjectNode node = nodeFactory.objectNode();
        
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
}