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
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SwingUtilities;

/**
 *
 * @author unkno
 */
public class UserListUpdater implements Runnable {

    private Socket clientSocket;
    private List<User> userList;
    private JList<String> listUser;
    private JComboBox<String> comboBox;
    private boolean running;
    private List<User> lastUserList; // Keep track of the last received UserList

    public UserListUpdater(Socket clientSocket, List<User> userList, JList<String> listUser, JComboBox<String> comboBox) {
        this.clientSocket = clientSocket;
        this.userList = userList;
        this.listUser = listUser;
        this.comboBox = comboBox;
        this.running = true;
        this.lastUserList = new ArrayList<>(userList); // Initialize lastUserList with the initial UserList
    }
        public void stop() {
        this.running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                // Receive the updated UserList from the server
                ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
                List<User> updatedUserList = (List<User>) objectInputStream.readObject();

                // Check for changes in the UserList
                if (!updatedUserList.equals(lastUserList)) {
                    // Update the local UserList with the received updated UserList
                    userList.clear();
                    userList.addAll(updatedUserList);

                    // Update the JList with the updated user names
                    SwingUtilities.invokeLater(() -> {
                        DefaultListModel<String> model = new DefaultListModel<>();
                        for (User user : userList) {
                            model.addElement(user.Name);
                        }
                        listUser.setModel(model);
                    });

                    // Update the ComboBox with the updated user names
                    SwingUtilities.invokeLater(() -> {
                        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
                        for (User user : userList) {
                            comboBoxModel.addElement(user.Name);
                        }
                        comboBox.setModel(comboBoxModel);
                    });

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
    public List<User> getUserList(){
        return userList;
    }
}
