/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HubHandler;

import java.net.InetAddress;
import java.io.Serializable;

/**
 *
 * @author unkno
 */
public class User implements Serializable {
    public String Name;
    public InetAddress UserIP;
    public int UserPORT;
    
    //initializing User
    public User(String Name, InetAddress UserIP, int UserPORT){
        this.Name = Name;
        this.UserPORT = UserPORT;
        this.UserIP = UserIP;
    }
}
