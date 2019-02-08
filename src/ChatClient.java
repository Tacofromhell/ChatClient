import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                    "Usage: java ChatClient <host name> <port number>");
            System.exit(1);
        }

        final String HOSTNAME = args[0];
        final int PORT = Integer.parseInt(args[1]);

        try (
                Socket echoSocket = new Socket(HOSTNAME, PORT);
                PrintWriter out =
                        new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in =
                        new BufferedReader(
                                new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn =
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println(echoSocket.getLocalSocketAddress().toString().substring(1) + " " + in.readLine());
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
}
