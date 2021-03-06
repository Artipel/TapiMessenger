var stompClient = null;

function setCallerBadge(caller) {
    $("#dialog").show();
    $("#name-and-surname").html(caller.name);
    $("#phone-number").html(caller.phone);
}

function resetCallerBadge() {
    $("#dialog").hide();
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/tapi-messenger');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/incoming-call', function (caller) {
            setCallerBadge(JSON.parse(caller.body));
        });
        sendName("+48606413737");
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    //stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
    stompClient.send("/tapi/listen", {}, JSON.stringify({'number': $("#name").val()}));
}

function sendName(number) {
    //stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
    stompClient.send("/tapi/listen", {}, JSON.stringify({'number': number.toString()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
    $( "#close" ).click(function () { resetCallerBadge(); });
});