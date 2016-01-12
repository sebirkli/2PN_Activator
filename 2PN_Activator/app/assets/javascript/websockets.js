var socket = new WebSocket("ws://" + location.host + "/socket");

socket.onopen = function() {
    console.log("onopen");
};
socket.onmessage = function(message) {
    resultObj = JSON.parse(message.data);
    console.log(resultObj.index);
    var fieldSize = resultObj.fieldSize;
    for (i = 0; i < fieldSize; ++i) {
        for (j = 0; j < fieldSize; ++j) {
            var tileID = "#" + i.toString() + j.toString();
            $(tileID)
                .removeClass($(tileID)["0"].className.split(' ').pop())
                .addClass("tile" + resultObj.grid[i][j].value);
            $(tileID)
                .html(resultObj.grid[i][j].value);
        }
    }
};
socket.onerror = function() {
    console.log("onerror");
    socket.close();
};
socket.onclose = function() {
    console.log("onclose");
    //location.reload();
};

$(function() {
    $("#down_button").click(function(ev) {
        window.location.replace("/game/s");
    });
    $(document).bind('keydown',function(event) {
        var direction;
        switch(event.which) {
            case 37: // left
                direction = 'a';
                break;

            case 38: // up
                direction = 'w';
                break;

            case 39: // right
                direction = 'd';
                break;

            case 40: // down
                direction = 's';
                break;

            default:
                return;
        }
        
        socket.send(JSON.stringify({
            d: direction
        }));
    });
});