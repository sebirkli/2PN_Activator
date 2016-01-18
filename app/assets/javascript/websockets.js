angular.module('myApp', [])
.controller('tpnController', function($scope){
    $scope.highscores = [];
    $scope.playerName = "";
    $scope.limit = 10;
    $scope.addHighscore = function(highscore){
        
        $scope.highscores.push(highscore);
    };
});

var socket = new WebSocket("ws://" + location.host + "/socket");
socket.onopen = function() {
    console.log("onopen");
};
socket.onmessage = function(message) {
    resultObj = JSON.parse(message.data);

    if (resultObj.eventType === "UpdateView") {
	onUpdateView(resultObj);
    } else if (resultObj.eventType === "GameOver") {
	onGameOver(resultObj);
    }
    

};

function onUpdateView(resultObj) {
    var fieldSize = resultObj.fieldSize;
    drawGameField(resultObj);
}

function onGameOver(resultObj) {
    alert("Game Over\nDeine Punkte: " + resultObj.points);
    var ngScope = angular.element(document.querySelector('#scope')).scope();
    ngScope.$apply(ngScope.addHighscore({
	name: resultObj.name,
	points: resultObj.points
    }));
}

socket.onerror = function() {
    console.log("onerror");
    socket.close();
};
socket.onclose = function() {
    console.log("onclose");
    //location.reload();
};

$(function() {
    $("#createNewGame").click(function() {
        socket.send(JSON.stringify({
            eventType: "newGame",
            size: $('#sizeNumber').val(),
            new: $('#newNumber').val()
        }));
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
            eventType: "command",
            d: direction
        }));
    });
});
