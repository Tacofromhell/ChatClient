package network;

import java.io.*;
import java.net.*;

public class ChatClient {

    //    private final static network.ChatClient client = new network.ChatClient();
    private final String HOSTNAME = "localhost";
    private final int PORT = 1234;
    private volatile boolean running = true;
    private static ChatClient _singleton = new ChatClient();
    private Socket socket;
    private ObjectOutputStream dataOut;
    private ObjectInputStream dataIn;
    private Thread startT;


    public ChatClient() {
        try {
            socket = new Socket(HOSTNAME, PORT);
            //TODO: add setSoTimeout()
            System.out.println("Connected");

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + HOSTNAME);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    HOSTNAME);
            System.exit(1);
        }

        startT = new Thread(() -> {
            try {
                loop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        startT.start();
    }

    private void loop() throws Exception {
        System.out.println("Starting client thread");

        dataOut = new ObjectOutputStream(socket.getOutputStream());
        dataIn = new ObjectInputStream(socket.getInputStream());
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (running) {
            String userInput = input.readLine();
            Message msg = new Message(socket, userInput);
            dataOut.writeObject(msg);
//            dataOut.flush();
            if (userInput.equals("quit")) {
                socket.close();
            }

            Message incoming = null;
            try {
                incoming = (Message) dataIn.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println(incoming.getTimestamp() + " | " + incoming.getSender().substring(1) + ": " + incoming.getMsg());
        }
    }

    public static ChatClient get() {
        return _singleton;
    }

    public void closeThreads() {
        running = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startT.interrupt();
    }
}

