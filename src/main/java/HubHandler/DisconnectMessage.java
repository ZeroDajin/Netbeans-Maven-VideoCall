/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package HubHandler;

import java.io.Serializable;
import java.net.InetAddress;

/**
 *
 * @author unkno
 */
public class DisconnectMessage implements Serializable{
    public InetAddress UserIP;
    public DisconnectMessage(InetAddress UserIP){
       this.UserIP = UserIP;
    }
}
