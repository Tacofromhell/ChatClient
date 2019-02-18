
package network;

import gui.Main;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class ChatClient {
    private final String HOSTNAME = "localhost";
    private final int PORT = 1234;
    private volatile boolean running = true;
    private final static ChatClient singleton = new ChatClient();
    private Socket socket;
    private ObjectOutputStream dataOut;
    private ObjectInputStream dataIn;
    private LinkedBlockingDeque<Object> dataQueue = new LinkedBlockingDeque<>();
    private User currentUser;

    private ArrayList<Room> rooms = new ArrayList<>();



    private ChatClient() {

        try {
            socket = new Socket(HOSTNAME, PORT);
            //TODO: add setSoTimeout()
            System.out.println("Connected");

            initObjectStreams();
            updateServer();
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

            Thread monitorIncoming = new Thread(this::monitorIncomingData);
            monitorIncoming.setDaemon(true);
            monitorIncoming.start();
            Thread handleData = new Thread(this::handleDataQueue);
            handleData.setDaemon(true);
            handleData.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void monitorIncomingData() {
        while (running) {
            try {
                dataQueue.addLast(dataIn.readObject());
            } catch (ClassNotFoundException e) {
                System.err.println("Object not found");
            } catch (IOException ioe) {
                System.out.println("Socket is closed");
                running = false;
            }
        }
    }

    private void handleDataQueue() {
        while (true) {
            if (dataQueue.size() > 0) {

                Object data = dataQueue.poll();

                if (data instanceof Message) {
                    System.out.println("Received a: " + data);
                    Message incoming = (Message) data;
                    //Just for print in terminal
                    String msg = incoming.getRoom() + ": " + incoming.getTimestamp() + " | " + incoming.getUser().getUsername() + ":  " + incoming.getMsg();
                    System.out.println(msg);

                    //Send incoming message and currentUser to javaFX
                    Platform.runLater(() -> Main.UIcontrol.printMessageFromServer(incoming));

                } else if (data instanceof Room) {

                    Room room = (Room) data;
                    rooms.add(room);
                    // print rooms messages on connection
                    room.getMessages()
                            .forEach(msg ->
                                    Platform.runLater(() -> Main.UIcontrol.printMessageFromServer(msg)));
                    System.out.println("Received a: " + data);

                    if(!rooms.contains(data)) {
                        rooms.add((Room) data);
                        // print rooms messages on connection
                        rooms.forEach(room -> room.getMessages()
                                .forEach(msg ->
                                        Platform.runLater(() -> Main.UIcontrol.printMessageFromServer(msg))));
                    }
                } else if(data instanceof ArrayList){
                    System.out.println("ARRAYLIST a " + data);
                    this.rooms = (ArrayList<Room>) data;


                    Platform.runLater(() -> Main.UIcontrol.updateUserList());

                } else if(data instanceof User){
                    System.out.println("Received a: " + data);

                    this.currentUser = (User) data;
                    System.out.println(currentUser.getID());
                }
            } else {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessageToServer(User user, String userInput, String activeRoom) {

        if (!currentUser.getUsername().equals(user.getUsername()))
            currentUser.setUsername(user.getUsername());

        try {
            Message newMessage = new Message(userInput, currentUser, activeRoom);
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

    public Socket getSocket() {
        return this.socket;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public ArrayList<Room> getRooms(){
        return this.rooms;
    }

    public void updateServer(){
        try {
            dataOut.writeObject("update");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void closeThreads() {
        running = false;
    }
}