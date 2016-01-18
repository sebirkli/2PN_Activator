function drawGameField(resultObj) {
    var fieldSize = resultObj.fieldSize;
    var gameContainer = "";
    
    for (i = 0; i < fieldSize; ++i) {
        for (j = 0; j < fieldSize; ++j) {
            gameContainer += "<div id=" + i.toString() + j.toString() +
                              " class=\"tile square tile" + resultObj.grid[i][j].value.toString() + ">\n" +
                                    "<span class=\"tile-centerd-text\">\n" +
                                        resultObj.grid[i][j].value.toString() +
                                    "</span>\n" +
                             "</div>\n";
        }
    }
    $(".game-container").html(gameContainer);
}