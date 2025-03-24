import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";  // Server address (localhost)
    private static final int SERVER_PORT = 12345;  // Server port

    public static void main(String[] args) {
        try (
            // Create a socket to connect to the server
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));  // Input stream (from server)
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);  // Output stream (to server)
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))  // User input (keyboard)
        ) {
            System.out.println("Connected to the chat server.");
            
            // Ask user for their name
            System.out.print(in.readLine() + " ");  // Read server's prompt
            out.println(userInput.readLine());  // Send user name to server

            // Thread to receive messages from the server
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);  // Display received messages
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            receiveThread.start();  // Start the thread

            // Main loop to send messages to the server
            String message;
            while ((message = userInput.readLine()) != null) {
                out.println(message);  // Send message to the server
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
