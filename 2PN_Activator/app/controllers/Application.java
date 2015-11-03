package controllers;

import play.*;
import play.mvc.*;
import de.htwg.se.tpn.controller.TpnController;
import views.html.*;

public class Application extends Controller {

    TpnController controller = new TpnController(5, 2);


    public Result index() {
        return ok(index.render());
    }

    public Result playGame() {
        return ok(tpn.render(controller));
    }

}
