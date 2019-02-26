package network;

import data.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

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

    private ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

    private ChatClient() {

        try {
            socket = new Socket(HOSTNAME, PORT);
            //TODO: add setSoTimeout()
            System.out.println("Connected");
            initObjectStreams();
            SocketStreamHelper.sendData(new NetworkMessage.ClientConnect("connecting client test"), dataOut);
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
            dataHandler.addToQueue(SocketStreamHelper.receiveData(dataIn));
        }
    }

    public void sendMessageToServer(String userInput, String activeRoom) {
        Message newMessage = new Message(userInput, currentUser, activeRoom);
        SocketStreamHelper.sendData(newMessage, dataOut);
    }

    public static ChatClient get() {
        return singleton;
    }

    public void startAutoUpdatingActiveRoom() {
        // starts auto updating active room
        UpdateActiveRoom updateActiveRoom = new UpdateActiveRoom();
        Thread updateActiveRoomThread = new Thread(updateActiveRoom);
        updateActiveRoomThread.setDaemon(true);
        updateActiveRoomThread.start();
    }

    public Socket getSocket() {
        return this.socket;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public ConcurrentHashMap<String, Room> getRooms() {
        return this.rooms;
    }

    public ObjectOutputStream getDataOut() {
        return dataOut;
    }

    public void closeThreads() {
        running = false;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void addRoom(Room room) {
        this.rooms.putIfAbsent(room.getRoomName(), room);
    }
}