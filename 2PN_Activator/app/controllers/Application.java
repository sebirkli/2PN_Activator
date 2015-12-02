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
        System.out.println(command);
        controller.processInput(command);
        return playGame();
    }
    
    public Result playGame() {
        return ok(tpn.render(controller));
    }

    public static Result jsonCommand(String command) {
        Sudoku.getInstance().getTUI().processInputLine(command);
        return json();
    }

    public static Result json() {
        IGrid grid = controller.getGrid();
        int x = grid.getCellsPerEdge();
        Map<String, Object> obj[][] = new HashMap[x][x];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < x; j++) {
                obj[i][j] = new HashMap<String, Object>();
                obj[i][j].put("cell", grid.getICell(i,j));
                boolean[] candidates = new boolean[x];
                for (int ii = 0; ii < x; ii++) {
                    candidates[ii] = controller.isCandidate(i, j, ii + 1);
                }
                obj[i][j].put("candidates", candidates);
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("meta", controller.getGrid());
        map.put("grid", obj);

        return ok(Json.stringify(Json.toJson(map)));
    }
}
