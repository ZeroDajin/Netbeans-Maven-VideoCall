/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HubHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;

/**
 *
 * @author unkno
 */
public class UserListUpdater implements Runnable {
    private Socket clientSocket;
    private List<User> userList;
    private DefaultListModel<String> listModel;
    private DefaultComboBoxModel<String> comboBoxModel;
    private List<User> lastUserList; // Keep track of the last received UserList

    public UserListUpdater(Socket clientSocket, List<User> userList, DefaultListModel<String> listModel, DefaultComboBoxModel<String> comboBoxModel) {
        this.clientSocket = clientSocket;
        this.userList = userList;
        this.listModel = listModel;
        this.comboBoxModel = comboBoxModel;
        this.lastUserList = new ArrayList<>(userList); // Initialize lastUserList with the initial UserList
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Receive the updated UserList from the server
                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                List<User> updatedUserList = (List<User>) objectInputStream.readObject();

                // Check for changes in the UserList
                if (!updatedUserList.equals(lastUserList)) {
                    // Update the local UserList with the received updated UserList
                    userList.clear();
                    userList.addAll(updatedUserList);

                    // Update the JList with the updated user names
                    listModel.clear();
                    for (User user : userList) {
                        listModel.addElement(user.Name);
                    }

                    // Update the ComboBox with the updated user names
                    comboBoxModel.removeAllElements();
                    for (User user : userList) {
                        comboBoxModel.addElement(user.Name);
                    }

                    // Update lastUserList with the current UserList
                    lastUserList.clear();
                    lastUserList.addAll(userList);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // Handle any exceptions or implement error handling as needed
        }
    }
}

