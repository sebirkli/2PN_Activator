$(function() {
    $("#down_button").click(function(ev) {
        window.location.replace("/game/s");
    });
    $(document).keypress(function(event) {
        window.location.replace("/game/" + String.fromCharCode(event.which));
    });
    
    $.ajax({url: "/game/" + String.fromCharCode(event.which), success: function(result){
        $("#div1").html(result);
    }});
});