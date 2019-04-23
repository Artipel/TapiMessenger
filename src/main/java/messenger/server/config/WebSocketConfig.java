package messenger.server.config;

import messenger.controller.DisconnectionHandler;
import messenger.controller.MainController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private DisconnectionHandler disconHandler;

    /**
     * setter for action invoked on client disconnection.
     * @param disconHandler
     */
    public void setDisconnectionHandler(DisconnectionHandler disconHandler) {
        this.disconHandler = disconHandler;
    }

    /**
     * Sets /tapi-messenger as endpoint
     * Sets all origins as allowed
     * Uses SockJS
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/tapi-messenger")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    /**
     * Creates brokers with prefixes: /topic and /user/queue
     * Application prefix: /tapi
     * User prefix /user
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/user/queue");
        registry.setApplicationDestinationPrefixes("/tapi");
        registry.setUserDestinationPrefix("/user");
    }

    @EventListener
    public void onSocketDisconnected(SessionDisconnectEvent event) {
        disconHandler.handleDisconnection(event);
    }

}
