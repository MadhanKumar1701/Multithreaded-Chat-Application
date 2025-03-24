import java.io.*;
import java.net.*;
import java.util.*;

// ChatServer class to handle multiple clients
public class ChatServer {
    private static final int PORT = 12345;  // Port number where the server listens
    private static Set<ClientHandler> clientHandlers = new HashSet<>();  // Stores all connected clients

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {  // Create a server socket
            System.out.println("Server started. Waiting for clients...");

            // Infinite loop to accept multiple client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();  // Accept client connection
                System.out.println("New client connected: " + clientSocket);

                // Create a new handler for the client and start a thread
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();  // Start the client thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcasts a message to all connected clients except the sender
    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {  // Prevent sender from receiving their own message
                client.sendMessage(message);
            }
        }
    }

    // Removes a client from the set when they disconnect
    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
    }
}

// Handles communication with a single client
class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // Set up input and output streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Ask client for their name
            out.println("Welcome to the chat! Enter your name:");
            String clientName = in.readLine();
            System.out.println(clientName + " has joined the chat.");

            // Notify other clients
            ChatServer.broadcastMessage(clientName + " has joined the chat!", this);

            String message;
            // Continuously listen for messages from the client
            while ((message = in.readLine()) != null) {
                System.out.println(clientName + ": " + message);
                ChatServer.broadcastMessage(clientName + ": " + message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }

    // Sends a message to this client
    public void sendMessage(String message) {
        out.println(message);
    }

    // Closes resources and removes the client
    private void closeConnections() {
        try {
            in.close();
            out.close();
            socket.close();
            ChatServer.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
