function drawGameField(resultObj) {
    var fieldSize = resultObj.fieldSize;
    
    clearGameContainer();
    for (i = 0; i < fieldSize; ++i) {
        for (j = 0; j < fieldSize; ++j) {
            var tile = document.createElement('div');
            tile.className = "tile square tile" + resultObj.grid[i][j].value.toString();
            setTileSize(tile, fieldSize);
            
            var number = document.createElement('span');
            number.className = "tile-centerd-text";
            number.innerHTML = resultObj.grid[i][j].value.toString();
            tile.appendChild(number);
            document.getElementById('gameContainer').appendChild(tile);
        }
    }
}

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