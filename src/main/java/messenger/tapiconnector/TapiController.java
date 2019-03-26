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

    enum OPCODE {
        CALL,
        LISTEN,
        STOP_LISTEN,
        NEW_CALL,
        ERROR,
        OK
    }

    private Socket socket;

    private OutputStream outputStream;
    private PrintWriter printWriter;
    private InputStream inputStream;
    private BufferedReader bufferedReader;
    private DataOutputStream dataOutputStream;

    private final MainController mainController;

    public TapiController(MainController mainController) {
        this.mainController = mainController;
        try {
            socket = new Socket("localhost", 44444);
            outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream);
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            dataOutputStream = new DataOutputStream(outputStream);
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
            System.out.println("command: " + command);
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt(command.length() + 1);
            if(outputStream != null) {
                outputStream.write(buffer.array());
                outputStream.flush();
//                dataOutputStream.writeInt(command.length() + 1);
//                dataOutputStream.flush();
            }
            if(printWriter != null) {
                printWriter.println(command);
                printWriter.flush();
            }
            System.out.println("Sending bytes to C++ program");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            System.out.println("Trying to send bytes over socket");
            String command = "STOPLISTEN," + number;
            System.out.println("command: " + command);
            dataOutputStream.writeInt(command.length() + 1);
            dataOutputStream.flush();
            printWriter.println(command);
            System.out.println("Sending bytes to C++ program");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void callTo(String from, String to){
        try {
            System.out.println("Trying to send bytes over socket");
            String command = "CALL," + from + "," + to;
            System.out.print("command: " + command);
            dataOutputStream.writeInt(command.length() + 1);
            dataOutputStream.flush();
            printWriter.print(command);
            System.out.println("Sending bytes to C++ program");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void incomingPhonecall(String from, String to){
        mainController.handleIncomingCall(from, to);
    }


}
