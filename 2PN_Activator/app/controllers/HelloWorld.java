package controllers;

import de.htwg.se.tpn.controller.TpnController;
import play.Play;
import play.mvc.*;
import views.html.*;
import de.htwg.se.tpn.controller.*;

public class HelloWorld extends Controller
{
    TpnController helloController = new TpnController();

    public Result index()
    {

        return ok(views.html.hello.render());
    }
}