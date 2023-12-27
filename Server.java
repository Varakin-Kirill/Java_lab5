import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 8888;
    private Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                Thread clientThread = new Thread(() -> {
                    String clientName = null;
                    try (
                            PrintWriter outputStream = new PrintWriter(clientSocket.getOutputStream(), true);
                            BufferedReader inputStream = new BufferedReader(
                                    new InputStreamReader(clientSocket.getInputStream()))) {
                        outputStream.println("Please enter your name:");
                        clientName = inputStream.readLine();
                        clients.put(clientName, outputStream);

                        outputStream.println("Enter your message:");

                        String clientMessage;
                        while ((clientMessage = inputStream.readLine()) != null) {
                            System.out.println(clientName + ": " + clientMessage);
                            broadcastMessage(clientName + ": " + clientMessage, clientName);

                            outputStream.println("Enter your message:");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (clientName != null) {
                            removeClient(clientName);
                        }
                    }
                });
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message, String senderName) {
        for (Map.Entry<String, PrintWriter> entry : clients.entrySet()) {
            if (!entry.getKey().equals(senderName)) {
                entry.getValue().println(message);
            }
        }
    }

    public void removeClient(String name) {
        PrintWriter client = clients.remove(name);
        if (client != null) {
            client.close();
        }
    }
}