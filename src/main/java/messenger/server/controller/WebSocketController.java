package messenger.server.controller;

import messenger.controller.MainController;
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

    private final MainController mainController;

    public WebSocketController(MainController mainController) {
        this.mainController = mainController;
    }

    @Autowired
    SimpMessagingTemplate template;

    public void notifyIncomingCall(String listenerSessionId, Caller caller) {
        // template.convertAndSendToUser(listenerSessionId, "/user/queue/incoming-call", caller);
        template.convertAndSend("/user/queue/incoming-call-user" + listenerSessionId, caller);
        // template.convertAndSend("/topic/incoming-call", caller);
    }

    @CrossOrigin(origins = "http://172.16.35.185:7070")
    @MessageMapping("/listen")
    @SendTo("/topic/is-listen-init")
    public String startListen(@Header("simpSessionId") String sessionId, @Header("apex_session") String apexSession) { //getNumberFromSessionFromDB(sessionId)
        System.out.println("Received request to listen for session: " + apexSession);
        String number = mainController.registerNewListener(apexSession, apexSession);
        return "LISTENING STARTED for number: " + number;
    }

    @MessageMapping("/stop")
    public void stopListen(@Header("simpSessionId") String sessionId, @Header("apex_session") String apexSession) {
        System.out.println("Received request to STOP listen for session: " + apexSession);
        mainController.stopListen(apexSession);
    }

    @CrossOrigin(origins = "http://172.16.35.185:7070")
    @MessageMapping("/call")
    @SendTo("/topic/is-call-init")
    public String initiateCall(CallMessage message, @Header("apex_session") String sessionId) {
        System.out.println("Received request to call to " + message.getToNumber() + " from sessionID: " + sessionId);
        mainController.initNewCall(sessionId, message.getToNumber());
        return "CALLING NUMBER " + message.getToNumber();
    }
}
