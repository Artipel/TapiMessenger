var stompClient;

function setCallerId() {
    $s("P1_KONTRAHENT_ID", 108396);
}

function setCallerBadge(caller) {
    if($("#PHONE").attr('class').includes('is-collapsed')){
       $("#PHONE .t-Region-header .t-Region-headerItems .t-Button").click()
   }
    $s("P1_PHONE", caller.phone);
    $("#audiotag1")[0].play();
}

function resetCallerBadge() {
    $("#dialog").hide();
}

function setConnected(connected) {
    if (connected) {
        $("#PHONE").removeClass('t-Region--accent0');
        $("#PHONE").addClass('t-Region--accent1');
    }
    else {
        $("#PHONE").removeClass('t-Region--accent1');
        $("#PHONE").addClass('t-Region--accent0');
    }
}

function reportError() {
    $("#PHONE").removeClass('t-Region--accent0');
    $("#PHONE").addClass('t-Region--accent9');
}
function setConnecting() {
    $("#PHONE").removeClass('t-Region--accent0');
    $("#PHONE").removeClass('t-Region--accent1');
    $("#PHONE").addClass('t-Region--accent7');
}

function connect_only() {
    var socket = new SockJS('172.16.35.51:8081/tapi-messenger'); //172.16.35.51:8081
    stompClient = Stomp.over(socket);
    stompClient.connect('mylogin', 'mypasswd', function (frame) {
        stompClient.subscribe('/topic/is-listen-init', function (resp) { console.log(resp); });
        introduce(apexSession);
    }, function (frame) {
        console.log('Error occured');
        reportError();});
}

function connect() {
    var socket = new SockJS('172.16.35.51:8081/tapi-messenger'); //172.16.35.51:8081
    stompClient = Stomp.over(socket);
    stompClient.connect('mylogin', 'mypasswd', function (frame) {

        var apexSession = $v("pInstance");

        var url = stompClient.ws._transport.url;
        url = url.replace("http","").replace("ws","");
        url = url.replace(
            "://172.16.35.51:8081/tapi-messenger/",  ""); //172.16.35.51:8081
        url = url.replace("/websocket", "");
        url = url.replace(/^[0-9]+\//, "");
        console.log("Your current session is: " + url);

        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/queue/incoming-call-user' + url, function (caller) {
            setCallerBadge(JSON.parse(caller.body));
        });
        stompClient.subscribe('/topic/is-listen-init', function (resp) { console.log(resp); });
        stompClient.subscribe('/user/queue/incoming-call-user' + apexSession, function (caller) {
            setCallerBadge(JSON.parse(caller.body));
        });
        introduce(apexSession);
    }, function (frame) {
	console.log('Error occured');
	reportError();});
}


function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function introduce(session) {
    stompClient.send("/tapi/listen", {'apex_session': session}, 'Hello!');
}

function callTo(number) {
    if(!stompClient) {
	stompClient = parent.stompClient;
    }
    stompClient.send("/tapi/call", {}, JSON.stringify({'toNumber': number}))
}