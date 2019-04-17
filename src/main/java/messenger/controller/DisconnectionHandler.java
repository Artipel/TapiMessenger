package messenger.controller;

import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public interface DisconnectionHandler {
    public void handleDisconnection(SessionDisconnectEvent event);
}
