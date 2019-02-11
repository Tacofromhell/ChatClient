import java.io.*;
import java.net.*;

class ChatClient extends Thread{

//    private final static ChatClient client = new ChatClient();
    private final String HOSTNAME = "localhost";
    private final int PORT = 1234;
    private boolean running = true;

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
            if (userInput.equals("quit")) {
                socket.close();
            }
            Message msg = new Message(socket, userInput);
            dataOut.writeObject(msg);
//            dataOut.flush();

//                Message incoming = null;
                try {
                    Message incoming = (Message)dataIn.readObject();
                    System.out.println(incoming.getTimestamp() + " | " + incoming.getSender().substring(1) + ": " + incoming.getMsg());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
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

