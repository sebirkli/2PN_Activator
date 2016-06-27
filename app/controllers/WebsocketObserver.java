package controllers;

import akka.actor.UntypedActor;
import akka.japi.Creator;
import de.htwg.se.tpn.controller.AddUIRequest;
import de.htwg.se.tpn.controller.Command;
import de.htwg.se.tpn.model.GameFieldInterface;
import de.htwg.se.tpn.util.observer.IObserver;
import de.htwg.se.tpn.controller.TpnController;
import de.htwg.se.tpn.controller.TpnControllerInterface;
import de.htwg.se.tpn.util.observer.Event;
import de.htwg.se.tpn.view.UIActor;
import de.htwg.se.tpn.view.UIEvent;
import play.libs.F;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import akka.actor.ActorRef;
import akka.actor.Props;

import play.libs.Json;
import play.mvc.*;
import play.libs.*;

import static javafx.scene.input.KeyCode.F;

class WebsocketObserver extends UntypedActor {

    WebSocket.Out<JsonNode> out;
    WebSocket.In<JsonNode> in;
    ActorRef controller;
    String name;
    String email;
    public WebsocketObserver(ActorRef c, WebSocket.Out<JsonNode> o,
                             WebSocket.In<JsonNode> i, String nickname, String email) {
        out = o;
        in = i;
        controller = c;
        name = nickname;
        this.email = email;
        controller.tell(new AddUIRequest(getSelf()), getSelf());
        
        in.onMessage(new F.Callback<JsonNode>() {
            public void invoke(JsonNode event) {
                JsonNode eventTypeNode = event.get("eventType");
                if (eventTypeNode == null) {
                    return;
                }
                String eventType = eventTypeNode.asText();
                
                if (eventType.equals("newGame")) {
                    int size = Integer.parseInt(event.get("size").asText());
                    int nrnew = Integer.parseInt(event.get("new").asText());
                    controller.tell(new Command("new " + size + " " + nrnew), getSelf());
                } if (eventType.equals("save")) {
                    controller.tell(new Command("save " + email), getSelf());
                    
                } if (eventType.equals("load")) {
                    controller.tell(new Command("load " + email), getSelf());
                    
                } else if (eventType.equals("command")) {
                    controller.tell(new Command(event.get("d").asText()), getSelf());
                }
            }
        });
    }

    public void onReceive(Object message) throws Exception {
        if(message instanceof UIEvent) {
            UIEvent event = (UIEvent)message;
            if(event.type == UIEvent.Type.NewField) {
                this.handleNewFieldEvent(event.gameField);
            } else if(event.type == UIEvent.Type.GameOver) {
                this.handleGameOverEvent(event.gameField);
            } else if(event.type == UIEvent.Type.NewGame) {
                this.handleNewGameEvent(event.gameField);
            } else if(event.type == UIEvent.Type.GameLoaded) {
                this.handleLoadedGameEvent(event.gameField);
            } else if(event.type == UIEvent.Type.Error) {
                this.handleErrorEvent(event.message);
            }

        }
    }

    public void handleNewFieldEvent(GameFieldInterface gameField) {
        updateValues(gameField);
    }

    boolean end = false;
    public void handleGameOverEvent(GameFieldInterface gameField) {
        if (!end) {
            end = true;
            gameOver(gameField);
        }
    }

    public void handleNewGameEvent(GameFieldInterface gameField) {
        end = false;
        updateValues(gameField);
    }

    public void handleLoadedGameEvent(GameFieldInterface gameField) {
    }

    public void handleErrorEvent(String gameField) {
    }

    public static Props props(ActorRef c, WebSocket.Out<JsonNode> o,
                              WebSocket.In<JsonNode> i, String nickname, String email) {
        return Props.create(new Creator<WebsocketObserver>() {
            @Override
            public WebsocketObserver create() throws Exception {
                return new WebsocketObserver(c, o, i, nickname, email);
            }
        });
    }
    
    private void updateValues(GameFieldInterface gameField) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance ;
        ObjectNode node = nodeFactory.objectNode();
        node.put("eventType", "UpdateView");
        
        int fieldSize = gameField.getHeight();
        
        ArrayNode grid = new ArrayNode(nodeFactory);
        for (int i = 0; i < fieldSize; ++i) {
            ArrayNode row = new ArrayNode(nodeFactory);
            for (int j = 0; j < fieldSize; ++j) {
                ObjectNode valNode = nodeFactory.objectNode();
                valNode.put("value", gameField.getValue(i, j));
                row.add(valNode);
            }
            grid.add(row);
        }

        node.put("fieldSize", fieldSize);
        node.put("grid", grid);
        
        out.write(node);
    }
    
    private void gameOver(GameFieldInterface gameField) {
        JsonNodeFactory nodeFactory = JsonNodeFactory.instance ;
        ObjectNode node = nodeFactory.objectNode();
        node.put("eventType", "GameOver");
        
        int points = 0;
        int fieldSize = gameField.getHeight();
        for (int i = 0; i < fieldSize; ++i) {
            for (int j = 0; j < fieldSize; ++j) {
                points += gameField.getValue(i, j);
            }
        }
        node.put("name", name);
        node.put("points", points);
        
        out.write(node);
    }
}