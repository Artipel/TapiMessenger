package messenger.server.controller;

import messenger.controller.MainController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import messenger.server.model.Caller;
import messenger.server.model.messages.CallMessage;
import messenger.server.model.messages.ListenMessage;

@Controller
public class WebSocketController {

    private final MainController mainController;

    public WebSocketController(MainController mainController) {
        this.mainController = mainController;
    }

    @Autowired
    SimpMessagingTemplate template;

    public void notifyIncomingCall(Caller caller) {
        template.convertAndSendToUser("", "/queue/incoming-call", caller);
        template.convertAndSend("/topic/incoming-call", caller);
    }

    @MessageMapping("/listen")
    @SendTo("/topic/is-listen-init")
    public String startListen(ListenMessage message) {
        System.out.println("Received request to listen for " + message.getNumber());
        mainController.registerNewListener("", message.getNumber());
        return "LISTENING STARTED";
    }

    @MessageMapping("/call")
    @SendTo("/topic/is-call-init")
    public String initiateCall(CallMessage message) {
        System.out.println("Received request to call to " + message.getToNumber() + " from " + message.getFromNumber());
        mainController.initNewCall(message.getFromNumber(), message.getToNumber());
        return "CALLING NUMBER " + message.getToNumber();
    }
}
