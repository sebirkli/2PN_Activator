package controllers;

import play.*;
import play.mvc.*;
import de.htwg.se.tpn.TwoPN;
import de.htwg.se.tpn.controller.TpnController;
import de.htwg.se.tpn.controller.TpnControllerInterface;

import views.html.*;

public class Application extends Controller {

    // static ISudokuController controller = Sudoku.getInstance().getController();

    static TpnControllerInterface controller = TwoPN.getInstance().getController();

    //TpnController controller = new TpnController(5, 2);


    public Result index() {
        return ok(index.render());
    }

    public Result playGame() {
        return ok(tpn.render(controller));
    }

}
