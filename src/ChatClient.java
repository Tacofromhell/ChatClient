import java.io.*;
import java.net.*;

class ChatClient {

    private final static ChatClient client = new ChatClient();
    private final String HOSTNAME = "localhost";
    private final int PORT = 1234;

    private ChatClient() {
        try (
            Socket socket = new Socket(HOSTNAME, PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))

        ) {
            System.out.println("Connected");
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                if (userInput.equals("quit")) {
                    socket.close();
                }
                System.out.println(socket.getLocalSocketAddress().toString().substring(1) + " " + in.readLine());
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

    static ChatClient get(){
        return client;
    }
}

