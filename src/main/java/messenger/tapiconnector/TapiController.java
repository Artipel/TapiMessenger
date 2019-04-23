package messenger.tapiconnector;

import messenger.controller.MainController;
import messenger.controller.NewCallHandler;
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

    private NewCallHandler newCallHandler;

    private final String HOST = "localhost";
    private final int PORT = 44444;

    public TapiController() {
        while(socket == null || !socket.isConnected()) {
            try {
                initSocketAndStream();
                listeningThread = new ListeningThread();
                listeningThread.start();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                try {
                    System.out.println("Reconnecting in 3 seconds...");
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void setNewCallHandler(NewCallHandler newCallHandler) {
        this.newCallHandler = newCallHandler;
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
        newCallHandler.handleIncomingCall(from, to);
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

    private String readSocketLine() throws IOException {
        char[] buffer = new char[4];
        int bytesRead = bufferedReader.read(buffer, 0, 4);
        if(bytesRead < 4)
            throw new IOException("Wrong number of bytes read");
        buffer = new char[(int)buffer[0]];
        bytesRead = bufferedReader.read(buffer, 0, buffer.length);
        if(bytesRead < buffer.length)
            throw new IOException("Wrong number of bytes read");
        return new String(buffer);
    }

    private class ListeningThread extends Thread{
        @Override
        public void run() {
            while(!Thread.interrupted()) {
                try {
                    String line = readSocketLine();
                    // String line = bufferedReader.readLine();
                    System.out.println("Read from TAPI: " + line);
                    String[] arguments = line.split(",");
                    String command = arguments[0];
                    switch (command) {
                        case "NEWCALL":
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
