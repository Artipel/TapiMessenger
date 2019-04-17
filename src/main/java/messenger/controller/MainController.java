package messenger.controller;

import messenger.server.config.WebSocketConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import messenger.server.controller.WebSocketController;
import messenger.server.model.Caller;
import messenger.tapiconnector.TapiController;
import org.springframework.stereotype.Controller;

@Controller
public class MainController {

    SessionPhoneMap map = new SessionPhoneMap();

    private WebSocketController webSocketController;

    private TapiController tapiController; // = new TapiController(this);

    private WebSocketConfig webSocketConfig;

    @Autowired
    private DBController dbController;

    @Autowired
    public void setWebSocketController(WebSocketController webSocketController) {
        this.webSocketController = webSocketController;
    }

    @Autowired
    public void setTapiController(TapiController tapiController) {
        this.tapiController = tapiController;
    }

    @Autowired
    public void setWebSocketConfig(WebSocketConfig webSocketConfig) {
        this.webSocketConfig = webSocketConfig;

        this.webSocketConfig.setDisconnectionHandler(event -> {
            StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
            unregisterListener(sha.getSessionId());
        });
    }

    public MainController() {
    }

    public void initNewCall(String sessionId, String toNumber) {
        String fromNumber = map.getPhone(sessionId);
        if (fromNumber != null)
            tapiController.callTo(fromNumber, toNumber);
        else
            System.out.println("No number associated with sessionId: " + sessionId);
    }

    /**
     * Update session to phone map. If new phone then starts listening on Tapi Server.
     * @param sessionId
     * @param applicationSession
     * @return phone number associated with this session
     */
    public String registerNewListener(String sessionId, String applicationSession) {
        String number = getNumberFromSessionFromDB(applicationSession);
        boolean overwrite = map.addPair(number, sessionId);
        if(!overwrite)
            tapiController.listenFor(number);
        return number;
    }

    public void unregisterListener(String sessionId) {
        String number = map.getPhone(sessionId);
        if (number != null) {
            System.out.println("Unregistering listener with sessionId: " + sessionId + " from listening on number: " + number);
            if (map.deleteSession(sessionId) == 0) {
                System.out.println("Last listener on number: " + number + " disconnected. Stop listen tapi server.");
                stopListen(number); // No more listeners. Tapi Server can stop sending data.
            }
        }
    }

    public void stopListen(String number) {
        if(number != null) {
            tapiController.stopListenFor(number);
        }
    }

    public void handleIncomingCall(String fromNumber, String toNumber) {
        String[] sessions = new String[map.getSessionsCount(toNumber)];
        map.getSessions(toNumber).toArray(sessions); // This one is to avoid concurrent modification exception
        for (String session :
                sessions) {
            webSocketController.notifyIncomingCall(session, getCallerData(fromNumber));
        }
    }

    private String getNumberFromSessionFromDB(String sessionId) {
        return dbController.getNumberFromSession(sessionId);
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
