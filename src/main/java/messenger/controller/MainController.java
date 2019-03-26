package messenger.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import messenger.server.controller.WebSocketController;
import messenger.server.model.Caller;
import messenger.tapiconnector.TapiController;
import org.springframework.stereotype.Controller;

@Controller
public class MainController {

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

    public MainController(WebSocketController webSocketController, TapiController tapiController) {
        this.webSocketController = webSocketController;
        this.tapiController = tapiController;
    }

    public void initNewCall(String from, String to) {
        tapiController.callTo(from, to);
    }

    public void registerNewListener(String sessionId, String number) {
        tapiController.listenFor(number);
    }

    public void handleIncomingCall(String from, String to) {
        webSocketController.notifyIncomingCall(getCallerData(from));
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
