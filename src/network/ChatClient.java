
package network;

import gui.Main;
import javafx.application.Platform;

import java.io.*;
import java.net.*;

public class ChatClient {
    private final String HOSTNAME = "localhost";
    private final int PORT = 1234;
    private volatile boolean running = true;
    private final static ChatClient singleton = new ChatClient();
    private Socket socket;
    private ObjectOutputStream dataOut;
    private ObjectInputStream dataIn;
    private User currentUser = new User();

    private ChatClient() {

        try {
            socket = new Socket(HOSTNAME, PORT);
            //TODO: add setSoTimeout()
            System.out.println("Connected");

            initObjectStreams();

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + HOSTNAME);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    HOSTNAME);
            System.exit(1);
        }

    }

    private void initObjectStreams() {
        System.out.println("Starting client thread");
        try {
            dataOut = new ObjectOutputStream(socket.getOutputStream());
            dataIn = new ObjectInputStream(socket.getInputStream());

            Thread monitorIncoming = new Thread(this::monitorIncomingMessages);
            monitorIncoming.setDaemon(true);
            monitorIncoming.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void monitorIncomingMessages() {
        while (running) {
            try {
                Message incoming = (Message) dataIn.readObject();

                //Just for print in terminal
                String msg = incoming.getTimestamp() + " | " + incoming.getUser().getUsername() + ":  " + incoming.getMsg();
                System.out.println(msg);

                //Send incoming message and currentUser to javaFX
//                Main.UIcontrol.printMessageFromServer(incoming, currentUser);
                Platform.runLater(() -> Main.UIcontrol.printMessageFromServer(incoming));

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException ioe) {
                System.out.println("Socket is closed");
                running = false;
//                ioe.printStackTrace();

            }
        }
    }

//    public void sendDataToServer(String userName, String userInput) {
//
//        if (!currentUser.getUsername().equals(userName))
//            currentUser.setUsername(userName);
//
//        try {
//            Message msg = new Message(socket, userInput, currentUser);
//            dataOut.writeObject(msg);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void sendDataToServer() {
//
//        try {
//            dataOut.writeObject(currentUser);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void sendMessageToServer(User user, String userInput) {

        if (!currentUser.getUsername().equals(user.getUsername()))
            currentUser.setUsername(user.getUsername());

        try {
            Message newMessage = new Message(userInput, currentUser);
            dataOut.writeObject(newMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendUserToServer() {
        try {
            dataOut.reset();
            dataOut.writeObject(currentUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ChatClient get() {
        return singleton;
    }

    public Socket getSocket(){
        return this.socket;
    }

    public User getCurrentUser(){
        return currentUser;
    }

    public void closeThreads() {
        running = false;
    }
}