/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TCPServer;

import HubHandler.DisconnectMessage;
import HubHandler.User;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author unkno
 */
public class LobbyHandler implements Runnable {

    private Socket clientSocket;
    private List<User> userList;
    private List<Socket> clientSockets;

    public LobbyHandler(Socket clientSocket, List<User> userList, List<Socket> clientSockets) {
        this.clientSocket = clientSocket;
        this.userList = userList;
        this.clientSockets = clientSockets;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            while (true) {
                Object receivedObject = objectInputStream.readObject();
                if (receivedObject instanceof User) {
                    // Process the received user data
                    User receivedUser = (User) receivedObject;
                    synchronized (userList) {
                        userList.add(receivedUser);
                    }
                    // Send back the updated list of user names
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    objectOutputStream.writeObject(getUserNames());
                    // Send the updated user list to all connected clients
                    sendUserListToAllClients();
                } else if (receivedObject instanceof DisconnectMessage) {
                    // Handle the disconnect message
                    // Close the socket and remove the user from the user list
                    clientSocket.close();
                    synchronized (userList) {
                        userList.removeIf(user -> user.UserIP.equals(((DisconnectMessage) receivedObject).UserIP));
                    }
                    clientSockets.remove(clientSocket);
                    // Send the updated user list to all connected clients
                    sendUserListToAllClients();
                    break; // Exit the loop and stop listening to the client
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // Handle any exceptions or implement error handling as needed
        }
    }

    private List<String> getUserNames() {
        List<String> userNames = new ArrayList<>();
        synchronized (userList) {
            for (User user : userList) {
                userNames.add(user.Name);
            }
        }
        return userNames;
    }

    private void sendUserListToAllClients() {
        for (Socket socket : clientSockets) {
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(new ArrayList<>(userList)); // Send a copy of the updated user list
            } catch (IOException e) {
                e.printStackTrace();
                // Handle any exceptions or display an error message to the server log
            }
        }
    }

}
