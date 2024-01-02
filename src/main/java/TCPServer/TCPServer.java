/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package TCPServer;

import HubHandler.User;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author unkno
 */
public class TCPServer implements Runnable {
    private List<User> userList;
    private List<Socket> clientSockets;
    private ServerSocket serverSocket;

    public TCPServer(List<User> userList) {
        this.userList = userList;
        this.clientSockets = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(5000);
            System.out.println("Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);
                LobbyHandler lobbyHandler = new LobbyHandler(clientSocket, userList, clientSockets);
                Thread handlerThread = new Thread(lobbyHandler);
                handlerThread.start();
                System.out.println("New user connected.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        List<User> userList = new ArrayList<>();  // Create a list to store users
        TCPServer server = new TCPServer(userList);  // Create an instance of the server
        Thread serverThread = new Thread(server);  // Create a thread for the server
        serverThread.start();  // Start the server
        System.out.println("Server started.");
    }
}

