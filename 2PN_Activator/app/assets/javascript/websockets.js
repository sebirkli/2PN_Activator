angular.module('myApp', [])
.controller('tpnController', function($scope){
    $scope.highscores = [];
    $scope.playerName = "";
    $scope.limit = 5;
    $scope.addHighscore = function(highscore){
        
        $scope.highscores.push(highscore);
    };
});

var socket = new WebSocket("ws://" + location.host + "/socket");
var counter = 0;
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
    
    if(true){
        var ngScope = angular.element(document.querySelector('#scope')).scope();
        ngScope.$apply(ngScope.addHighscore({
            name: counter + 'name',
            points: counter*100
        }));
        counter++;
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
