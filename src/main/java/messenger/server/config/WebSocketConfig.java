package messenger.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        DefaultHandshakeHandler handler = new DefaultHandshakeHandler(){
//            @Override
//            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
//                System.out.println("Handshake");
//                Principal principal = super.determineUser(request, wsHandler, attributes);
//                return principal;
//            }
//        };
        registry.addEndpoint("/tapi-messenger")
                .setAllowedOrigins("*")
//                .setHandshakeHandler(handler)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.enableSimpleBroker("/user/queue");
        registry.setApplicationDestinationPrefixes("/tapi");
        registry.setUserDestinationPrefix("/user");
    }
}
