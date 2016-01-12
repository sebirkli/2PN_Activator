var socket = new WebSocket("ws://localhost:9000/socket");
socket.onopen = function() {
    console.log("onopen");
}
socket.onmessage = function(message) {
    socket.send("data")
    console.log("onmessage");
}
socket.onerror = function() {
    console.log("onerror");
    socket.close();
}
socket.onclose = function() {
    console.log("onclose");
}