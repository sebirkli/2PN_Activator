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
                direction = String.fromCharCode(event.which);
        }
        
        $.ajax({url: "/json/" + direction, success: function(result){
            resultObj = JSON.parse(result);
            console.log(resultObj);
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
        }});
    });
});