function drawGameField(resultObj) {
    var fieldSize = resultObj.fieldSize;
    
    clearGameContainer();
    for (i = 0; i < fieldSize; ++i) {
        for (j = 0; j < fieldSize; ++j) {
/*            var tile = document.createElement('div');
            tile.className = "tile square tile" + resultObj.grid[i][j].value.toString();
            setTileSize(tile, fieldSize);
            
            var number = document.createElement('span');
            number.className = "tile-centerd-text";
            number.innerHTML = resultObj.grid[i][j].value.toString();
            tile.appendChild(number);
            document.getElementById('gameContainer').appendChild(tile);
            
            <div class='square-box'>
                <div class='square-content'><div><span>Aspect ratio 1:1</span></div></div>
            </div>*/
            
            var box = document.createElement('div');
            box.className = "tile square square-box tile" + resultObj.grid[i][j].value.toString();
            setTileSize(box, fieldSize);
            var cont = document.createElement('div');
            cont.className = "square-content";
            var ediv = document.createElement('div');
            var espan = document.createElement('span');
            espan.innerHTML = resultObj.grid[i][j].value.toString();
            
            box.appendChild(cont);
            cont.appendChild(ediv);
            ediv.appendChild(espan);
            document.getElementById('gameContainer').appendChild(box);
        }
    }
}

$($.ajax({url: "/json", success: function(result){
    resultObj = JSON.parse(result);
    drawGameField(resultObj);
}}));

function clearGameContainer() {
    var game = document.getElementById("gameContainer");
    while (game.firstChild) {
        game.removeChild(game.firstChild);
    }
}

function setTileSize(tile, fieldSize) {
    var size = 80 / fieldSize;
    var margin = 20 / (fieldSize * 2);
    tile.style.height = size.toString() + "%";
    tile.style.width = size.toString() + "%";
    tile.style.margin = margin.toString() + "%";
}