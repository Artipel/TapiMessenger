package messenger.tapiconnector;

import messenger.controller.MainController;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TapiController {

    private final MainController mainController;

    public TapiController(MainController mainController) {
        this.mainController = mainController;
    }

    public void listenFor(String number){
        Thread t1 = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(5000);
                    switch(i % 3){
                        case 0:
                            incomingPhonecall("601501401", number);
                            break;
                        case 1:
                            incomingPhonecall("602502402", number);
                            break;
                        case 2:
                            incomingPhonecall("603503403", number);
                            break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t1.start();
    }

    public void stopListenFor(String number){

    }

    public void callTo(String from, String to){

    }

    private void incomingPhonecall(String from, String to){
        mainController.handleIncomingCall(from, to);
    }


}
