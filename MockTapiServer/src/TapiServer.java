import com.sun.security.ntlm.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TapiServer {

    static Socket socket;
    static ServerSocket server;
    static InputStream in;
    static OutputStream out;
    static BufferedReader reader;

    public static void main(String[] args) {

        try {
            initSocketAndStream(44444);
//
//            server = new ServerSocket(44444);
//            System.out.println("Mock server: running on port 44444");
//            socket = server.accept();
//            in = socket.getInputStream();
//            out = socket.getOutputStream();
//            reader = new BufferedReader(new InputStreamReader(in));

            while(true) {
                char[] buffer = new char[4];
                reader.read(buffer, 0, 4);
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
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(10000);
                    switch (i % 3) {
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
        t0.start();
    }

    private static void incomingPhonecall(String from, String to) {
        System.out.println("New incoming phonecall from: " + from + " to: " + to);
        PrintWriter writer = new PrintWriter(out);
        System.out.println("Command: " + "NEWCALL,"+from+","+to);
        writer.println("NEWCALL,"+from+","+to);
        writer.flush();
    }

    private static void initSocketAndStream(int port) throws IOException {
        server = new ServerSocket(port);
        System.out.println("Mock server: running on port 44444");
        socket = server.accept();
        in = socket.getInputStream();
        out = socket.getOutputStream();
        reader = new BufferedReader(new InputStreamReader(in));
    }

}
