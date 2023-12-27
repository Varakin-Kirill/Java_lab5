import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                PrintWriter outputStream = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Connected to Server.");

            // Reading messages from the server
            Thread readThread = new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = inputStream.readLine()) != null) {
                        System.out.println("Server: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            readThread.start();

            // Sending messages to the server
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                outputStream.println(userInput);
            }

            // Closing resources
            readThread.join();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
