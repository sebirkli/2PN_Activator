package controllers;

import play.*;
import play.mvc.*;
import de.htwg.se.tpn.TwoPN;
import de.htwg.se.tpn.controller.TpnController;
import de.htwg.se.tpn.controller.TpnControllerInterface;

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

        controller.processInput(command);

        return playGame();
    }
    
    public Result playGame() {
        return ok(tpn.render(controller));
    }
}
