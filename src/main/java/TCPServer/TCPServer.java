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
public class TCPServer implements Runnable{
    private List<User> userList;
    private ServerSocket serverSocket;
    
    public TCPServer(List<User> userList){
        this.userList = userList;
    }
    @Override
    public void run(){
        try {
            serverSocket = new ServerSocket(5000); 
            System.out.println("Server is listening....");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread lobbyHandler = new Thread(new LobbyHandler(clientSocket, userList));
                lobbyHandler.start();
                System.out.println("Getting new User....");
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        List<User> userList = new ArrayList<>();  // Create a list to hold users
        TCPServer tcpServer = new TCPServer(userList);  // Create an instance of TCPServer
        Thread serverThread = new Thread(tcpServer);  // Create a new thread for the server
        serverThread.start();
    }
}
