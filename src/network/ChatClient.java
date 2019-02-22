package network;

import data.DataHandler;
import data.Message;
import data.Room;
import data.User;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
    private final String HOSTNAME = "localhost";
    private final int PORT = 1234;
    private volatile boolean running = true;
    private final static ChatClient singleton = new ChatClient();
    private Socket socket;
    private ObjectOutputStream dataOut;
    private ObjectInputStream dataIn;
    private User currentUser;
    DataHandler dataHandler = new DataHandler();

    private ArrayList<Room> rooms = new ArrayList<>();

    private ChatClient() {

        try {
            socket = new Socket(HOSTNAME, PORT);
            //TODO: add setSoTimeout()
            System.out.println("Connected");
            initObjectStreams();
            emitToServer("connecting");
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void monitorIncomingData() {
        while (running) {
            try {
               dataHandler.addToQueue(dataIn.readObject());
            } catch (ClassNotFoundException e) {
                System.err.println("Object not found");
            } catch (IOException ioe) {
                System.out.println("Socket is closed");
                running = false;
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

    public ArrayList<Room> getRooms() {
        return this.rooms;
    }

    public void updateServer() {
        try {
            dataOut.writeObject("update");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void emitToServer(String event){
        try {
            dataOut.writeObject(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeThreads() {
        running = false;
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    public void addRoom(Room room){
        this.rooms.add(room);
    }
}