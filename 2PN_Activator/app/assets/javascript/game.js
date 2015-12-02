$(function() {
    $("#down_button").click(function(ev) {
        window.location.replace("/game/s");
    });
    $(document).keypress(function(event) {
        $.getJSON({url: "/json/" + String.fromCharCode(event.which), success: function(result){
            console.log("recieved result");
            var fieldSize = result.filedSize;
            for (i = 0; i < fieldSize; ++i) {
                for (j = 0; j < fieldSize; ++j) {
                    var tileID = "#" + i.toString() + j.toString();
                    $(tileID)
                        .removeClass($(tileID).className.split(' ').pop())
                        .addClass("tile" + result[i][j].value);
                    $(tileID)
                        .html(result[i][j].value);
                }
            }
        }});
    });
});