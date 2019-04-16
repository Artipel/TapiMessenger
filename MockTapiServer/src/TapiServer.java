import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TapiServer {

    static Socket socket;
    static ServerSocket server;
    static InputStream in;
    static OutputStream out;
    static BufferedReader reader;

    public static void main(String[] args) {

        try {
            initSocketAndStream(44444);

//            server = new ServerSocket(44444);
//            System.out.println("Mock server: running on port 44444");
//            socket = server.accept();
//            in = socket.getInputStream();
//            out = socket.getOutputStream();
//            reader = new BufferedReader(new InputStreamReader(in));

            while(true) {
                char[] buffer = new char[4];
                ByteBuffer byteBuffer = ByteBuffer.allocate(4);
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                int readBytes;
                readBytes = reader.read(buffer, 0, 4);
                byteBuffer.getInt(0);
                int size = (int)buffer[0];
                buffer = new char[size];
                reader.read(buffer, 0, size);
                String line = new String(buffer);
                System.out.println(line);
                handleCommand(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleCommand(String line) {
        String[] elements = line.split(",");
        String command = elements[0];
        String[] arguments = new String[elements.length-1];
        for (int i = 1; i < elements.length; i++) {
            arguments[i-1] = elements[i];
        }
        switch(command) {
            case "LISTEN":
                startListening(arguments[0]);
                break;
        }
    }

    private static void startListening(String number) {
        Thread t0 = new Thread(() -> {
            try {
                for (int i = 0; i < 1000; i++) {
                    Thread.sleep(10000);
                    switch (i % 3) {
                        case 0:
                            incomingPhonecall("0606413737", number);
                            break;
                        case 1:
                            incomingPhonecall("444555666", number);
                            break;
                        case 2:
                            incomingPhonecall("123654789", number);
                            break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t0.start();
    }

    private static void incomingPhonecall(String from, String to) {
        System.out.println("New incoming phonecall from: " + from + " to: " + to);
        System.out.println("Command: " + "NEWCALL,"+from+","+to);
        try {
            sendMessage("NEWCALL,"+from+","+to);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initSocketAndStream(int port) throws IOException {
        server = new ServerSocket(port);
        System.out.println("Mock server: running on port 44444");
        socket = server.accept();
        in = socket.getInputStream();
        out = socket.getOutputStream();
        reader = new BufferedReader(new InputStreamReader(in));
    }

    private static void sendMessage(String msg) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(msg.length());
        buffer.put(msg.getBytes());
        if(out != null) {
            out.write(buffer.array(), 0, buffer.position());
            out.flush();
        }
    }

}
