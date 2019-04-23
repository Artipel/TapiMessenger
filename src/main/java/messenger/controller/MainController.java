package messenger.controller;

import messenger.server.config.WebSocketConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import messenger.server.controller.WebSocketController;
import messenger.server.model.Caller;
import messenger.tapiconnector.TapiController;
import org.springframework.stereotype.Controller;

/**
 * Central class of TapiMessenger. Links websockets with TapiServer.
 */
@Controller
public class MainController {

    private SessionPhoneMap map = new SessionPhoneMap();

    private WebSocketController webSocketController;

    private TapiController tapiController; // = new TapiController(this);

    private WebSocketConfig webSocketConfig;

    @Autowired
    @Qualifier("apex")
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

    public MainController() { }

    /**
     * Ask TapiServer to make a call to given number from a phone associated with given SimpSessionId.
     * @param sessionId SimpSessionId of a user asking for a call
     * @param toNumber Number to call to.
     */
    public void initNewCall(String sessionId, String toNumber) {
        String fromNumber = map.getPhone(sessionId);
        if (fromNumber != null)
            tapiController.callTo(fromNumber, toNumber);
        else
            System.out.println("No number associated with sessionId: " + sessionId);
    }

    /**
     * Register session to phone map. Make a new association in a map.
     * If phone number is new then it asks TapiServer to listen for this number.
     * If it is already listened to then it does not communicate with TapiServer.
     * @param sessionId SimpSessionId to which information will be sent.
     * @param applicationSession Internal application session id. This id is used to find out phone number of a user logged in.
     * @return phone number associated with given application session id.
     */
    public String registerNewListener(String sessionId, String applicationSession) {
        String number = getNumberFromSessionFromDB(applicationSession);
        boolean overwrite = map.addPair(number, sessionId);
        if(!overwrite)
            tapiController.listenFor(number);
        return number;
    }

    /**
     * Delete phone-SimpSessionId association from map.
     * If it was the last listener for a number then TapiServer is asked to stop listening for this number.
     * @param sessionId SimpSessionId of a listener that disconnected.
     */
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

    /**
     * Ask TapiServer to stop listening for a given phone number.
     * @param number phone number to stop listen.
     */
    public void stopListen(String number) {
        if(number != null) {
            tapiController.stopListenFor(number);
        }
    }

    /**
     * send information to all sessions associated with phone that new incoming call arrived.
     * @param fromNumber caller phone number
     * @param toNumber callee phone number (ringing phone)
     */
    public void handleIncomingCall(String fromNumber, String toNumber) {
        String[] sessions = new String[map.getSessionsCount(toNumber)];
        map.getSessions(toNumber).toArray(sessions); // This one is to avoid concurrent modification exception
        for (String session :
                sessions) {
            webSocketController.notifyIncomingCall(session, getCallerData(fromNumber));
        }
    }

    /**
     * call database controller to get number connected with given session id (applicatino session id).
     * @param sessionId application session id
     * @return phone number associated with usesr associated with given session id.
     */
    private String getNumberFromSessionFromDB(String sessionId) {
        return dbController.getNumberFromSession(sessionId);
    }

    /**
     * Get specific information about owner of a given phone number
     * @param number phone number of a searched person
     * @return information about owner of a phone number
     */
    private Caller getCallerData(String number) {
        return new Caller("Unknown", number);
    }
}
