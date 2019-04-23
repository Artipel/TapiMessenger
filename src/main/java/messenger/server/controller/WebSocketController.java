package messenger.server.controller;

import messenger.controller.MainController;
import messenger.controller.WebSocketCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import messenger.server.model.Caller;
import messenger.server.model.messages.CallMessage;
import messenger.server.model.messages.ListenMessage;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;

@Controller
public class WebSocketController {

    // private final MainController mainController;

    private WebSocketCallback callback;

    public WebSocketController() {
    }

    @Autowired
    SimpMessagingTemplate template;

    public void setCallback(WebSocketCallback callback) {
        this.callback = callback;
    }

    /**
     * send caller data to given SimpSessionId
     * @param listenerSessionId SimpSessionId to which data is sent
     * @param caller object transferred over WebSocket.
     */
    public void notifyIncomingCall(String listenerSessionId, Caller caller) {
        template.convertAndSend("/user/queue/incoming-call-user" + listenerSessionId, caller);
    }

    /**
     * Calls main controller to register new listener
     * @param sessionId
     * @param apexSession
     * @return
     */
    @MessageMapping("/listen")
    @SendTo("/topic/is-listen-init")
    public String startListen(@Header("simpSessionId") String sessionId, @Header("apex_session") String apexSession) { //getNumberFromSessionFromDB(sessionId)
        System.out.println("Received request to listen for session: " + sessionId + " apex session: " + apexSession);
        try {
            String number = callback.listenerSubscribed(sessionId, apexSession);
            return "LISTENING STARTED for number: " + number;
        } catch (Exception e) {
            return "FAILED TO FIND NUMBER FOR THIS SESSION";
        }
    }

    @MessageMapping("/stop")
    public void stopListen(@Header("simpSessionId") String sessionId) {
        System.out.println("Received request to STOP listen for session: " + sessionId);
        // mainController.stopListen(apexSession);
        callback.listenerUnsubscribed(sessionId);
    }

    @MessageMapping("/call")
    @SendTo("/topic/is-call-init")
    public String initiateCall(CallMessage message, @Header("simpSessionId") String sessionId) {
        System.out.println("Received request to call to " + message.getToNumber() + " from sessionID: " + sessionId);
        // mainController.initNewCall(sessionId, message.getToNumber());
        callback.askForNewCall(sessionId, message.getToNumber());
        return "CALLING NUMBER " + message.getToNumber();
    }
}
