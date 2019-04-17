package messenger.config;

import messenger.controller.MainController;
import messenger.server.config.WebSocketConfig;
import messenger.server.controller.WebSocketController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import messenger.tapiconnector.TapiController;
import sun.applet.Main;

@Configuration
public class BeanConfiguration {

    @Bean
    MainController mainController() {
        return new MainController();
    }

    @Bean
    TapiController tapiController(MainController mainController) {
        return new TapiController(mainController);
    }

    @Bean
    WebSocketController webSocketController(MainController mainController) {
        return new WebSocketController(mainController);
    }

}
