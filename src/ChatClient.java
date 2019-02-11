import java.io.*;
import java.net.*;

class ChatClient extends Thread{

//    private final static ChatClient client = new ChatClient();
    private final String HOSTNAME = "localhost";
    private final int PORT = 1234;
    private boolean running = true;
    private User currentUser = new User();

    public ChatClient() {
        start();
        try {
            Socket socket = new Socket(HOSTNAME, PORT);
            //TODO: add setSoTimeout()
            ObjectOutputStream dataOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream dataIn = new ObjectInputStream(socket.getInputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connected");
            while (running) {
            String userInput = input.readLine();
            Message msg = new Message(socket, userInput, currentUser);
            dataOut.writeObject(msg);
//            dataOut.flush();
                if (userInput.equals("quit")) {
                    socket.close();
                }

                Message incoming = null;
                try {
                    incoming = (Message)dataIn.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println(incoming.getTimestamp() + " | " + incoming.getSender().substring(1) + " " + incoming.getUser().getUsername() + ": " + incoming.getMsg());
            }
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


}

