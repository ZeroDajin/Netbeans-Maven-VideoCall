/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TCPServer;

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
public class LobbyHandler implements Runnable{
    private Socket clientSocket;
    private List<User> userList;
    
    public LobbyHandler(Socket clientSocket, List<User> userList){
        this.clientSocket = clientSocket;
        this.userList = userList;
    }
    @Override
    public void run(){
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            while (true) {
                Object receivedObject = objectInputStream.readObject();
                if (receivedObject instanceof User) {
                    // Process the received user data
                    User receivedUser = (User) receivedObject;
                    userList.add(receivedUser);
                    // Send back the updated list of user names
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    objectOutputStream.writeObject(userList);
                } else if (receivedObject instanceof String && ((String) receivedObject).equals("disconnect")) {
                    // Handle the disconnect message
                    // Close the socket and remove the user from the user list
                    clientSocket.close();
                    userList.removeIf(user -> user.UserIP.equals(clientSocket.getInetAddress()));
                    break; // Exit the loop and stop listening to the client
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private List<String> getUserNames() {
        List<String> userNames = new ArrayList<>();
        for (User user : userList) {
            userNames.add(user.Name);
        }
        return userNames;
    }
}
