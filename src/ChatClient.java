import java.io.*;
import java.net.*;

class ChatClient extends Thread{

//    private final static ChatClient client = new ChatClient();
    private final String HOSTNAME = "10.155.90.36";
    private final int PORT = 1234;
    private boolean running = true;

    ObjectInputStream dataIn;
    ObjectOutputStream dataOut;
    Socket socket;

    private User currentUser = new User();
 

    public ChatClient() {
        start();
        try {
            socket = new Socket(HOSTNAME, PORT);
            //TODO: add setSoTimeout()
            dataOut = new ObjectOutputStream(socket.getOutputStream());
            dataIn = new ObjectInputStream(socket.getInputStream());

            System.out.println("Connected");

            Thread monitorIncoming = new Thread(this::monitorIncomingMessages);
            monitorIncoming.start();
            Thread monitorInput = new Thread(this::monitorInput);
            monitorInput.start();
//            monitorIncomingMessages(dataIn);

//            while (running) {
//            String userInput = input.readLine();
//            if (userInput.equals("quit")) {
//                socket.close();
//            }
//            Message msg = new Message(socket, userInput);
//            dataOut.writeObject(msg);

//            dataOut.flush();


        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + HOSTNAME);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    HOSTNAME);
            System.exit(1);
        }
    }

    public ChatClient get(){
        return this;
    }

    void monitorIncomingMessages() {
        while (running) {
            try {
                Message incoming = (Message) dataIn.readObject();
                System.out.println(incoming.getTimestamp() + " | " + incoming.getSender().substring(1) + ": " + incoming.getMsg());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException ioe) {
                System.out.println("Socket is closed");
                running = false;
//                ioe.printStackTrace();

            }
        }
    }
    void monitorInput(){

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (running) {
                String userInput = input.readLine();
                if (userInput.equals("quit")) {
                    socket.close();
                    running = false;
                } else {
                    Message msg = new Message(socket, userInput);
                    dataOut.writeObject(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

