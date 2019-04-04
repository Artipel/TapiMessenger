package messenger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import messenger.server.controller.WebSocketController;
import messenger.server.model.Caller;
import messenger.tapiconnector.TapiController;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

@Controller
public class MainController {

    HashMap<String, String> sessionToPhone = new HashMap<>();
    HashMap<String, String> phoneToSession = new HashMap<>();

    private WebSocketController webSocketController;

    private TapiController tapiController; // = new TapiController(this);

    @Autowired
    public void setWebSocketController(WebSocketController webSocketController) {
        this.webSocketController = webSocketController;
    }

    @Autowired
    public void setTapiController(TapiController tapiController) {
        this.tapiController = tapiController;
    }

    public MainController() {
    }

    public void initNewCall(String sessionId, String to) {
        tapiController.callTo(sessionToPhone.get(sessionId), to);
    }

    public void registerNewListener(String sessionId) {
        String number = getNumberFromSessionFromDB(sessionId);
        sessionToPhone.put(sessionId, number);
        phoneToSession.put(number, sessionId);
        tapiController.listenFor(number);
    }

    public void handleIncomingCall(String fromNumber, String toNumber) {
        webSocketController.notifyIncomingCall(phoneToSession.get(toNumber), getCallerData(fromNumber));
    }

    int debugIterator = 0;

    private String getNumberFromSessionFromDB(String sessionId) {
        return debugIterator++ % 2 == 0 ? "734" : "791" ;
    }

    private Caller getCallerData(String number) {
        switch(number) {
            case "601501401":
                return new Caller("Pawel Krol", "601501401");
            case "602502402":
                return new Caller("Rafal Smolarek", "602502402");
            case "603503403":
                return new Caller("Artur Margielewski", "603503403");
            default:
                return new Caller("Unknown", number);
        }
    }
}
