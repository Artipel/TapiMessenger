package messenger.tapiconnector;

import messenger.controller.MainController;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Component
public class TapiController {

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private ListeningThread listeningThread;

    private final MainController mainController;

    private final String HOST = "localhost";
    private final int PORT = 44444;

    public TapiController(MainController mainController) {
        this.mainController = mainController;
        while(socket == null || !socket.isConnected()) {
            try {
                initSocketAndStream();
                listeningThread = new ListeningThread();
                listeningThread.start();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void listenFor(String number){
        try {
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
        ByteBuffer buffer = ByteBuffer.allocate(256);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(msg.length());
        buffer.put(msg.getBytes());
        if(outputStream != null) {
            outputStream.write(buffer.array(), 0, buffer.position());
            outputStream.flush();
        }
    }

    private void initSocketAndStream() throws IOException {
        socket = new Socket(HOST, PORT);
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    private class ListeningThread extends Thread{
        @Override
        public void run() {
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
        }
    }

}
