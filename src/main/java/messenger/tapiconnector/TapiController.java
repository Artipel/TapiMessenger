package messenger.tapiconnector;

import messenger.controller.MainController;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

@Component
public class TapiController {

    private Socket socket;
    private OutputStream outputStream;
    private PrintWriter printWriter;
    private InputStream inputStream;
    private BufferedReader bufferedReader;

    private final MainController mainController;

    public TapiController(MainController mainController) {
        this.mainController = mainController;
        try {
            socket = new Socket("localhost", 44444);
            outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream);
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            Thread listeningThread = new Thread(() -> {
                while(!Thread.interrupted()) {
                    try {
                        String line = bufferedReader.readLine();
                        System.out.println("Read from TAPI: " + line);
                        String command = line.split(",")[0];
                        switch (command) {
                            case "NEWCALL":
                                String[] arguments = line.split(",");
                                if (arguments.length > 2)
                                    incomingPhonecall(arguments[1], arguments[2]);
                                break;
                            case "ERROR":
                                System.out.println("Error reported from TAPI");
                                break;
                            case "OK":
                                System.out.println("Tapi accepts message");
                                break;
                            default:
                                System.out.println("Unknown message from TAPI: " + line);
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            listeningThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenFor(String number){
        try {
            System.out.println("Trying to send bytes over socket");
            String command = "LISTEN," + number;
            sendMessage(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopListenFor(String number){
        try {
            String command = "STOPLISTEN," + number;
            sendMessage(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void callTo(String from, String to){
        try {
            String command = "CALL," + from + "," + to;
            sendMessage(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void incomingPhonecall(String from, String to){
        mainController.handleIncomingCall(from, to);
    }

    private byte[] getMessageLength(String msg) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(msg.length());
        return buffer.array();
    }

    private void sendMessage(String msg) throws IOException {
        if(outputStream != null) {
            outputStream.write(getMessageLength(msg));
            outputStream.flush();
        }
        if(printWriter != null) {
            printWriter.print(msg);
            printWriter.flush();
        }
    }

}
