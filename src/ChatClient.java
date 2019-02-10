import java.io.*;
import java.net.*;

class ChatClient {

//    private final static ChatClient client = new ChatClient();
    private final String HOSTNAME = "localhost";
    private final int PORT = 1234;
    private boolean running = true;

    public ChatClient() {
        try {
            Socket socket = new Socket(HOSTNAME, PORT);
            //TODO: add setSoTimeout()
            ObjectOutputStream dataOut = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream dataIn = new ObjectInputStream(socket.getInputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Connected");
            while (running) {
            String userInput = input.readLine();
//            dout.writeUTF(userInput);
//            String msg = din.readUTF();
            Message msg = new Message(socket, userInput);
            dataOut.writeObject(msg);
            dataOut.flush();
                if (userInput.equals("quit")) {
                    socket.close();
                }

                Message incoming = null;
                try {
                    incoming = (Message)dataIn.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println(incoming.getTimestamp() + " | " + incoming.getSender().substring(1) + ": " + incoming.getMsg());
//                System.out.println(socket.getLocalSocketAddress().toString().substring(1) + " " + msg);
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

