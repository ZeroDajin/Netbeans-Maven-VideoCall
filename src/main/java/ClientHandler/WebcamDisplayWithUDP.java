/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ClientHandler;


import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
/**
 *
 * @author unkno
 */
public class WebcamDisplayWithUDP {
    private DatagramSocket socket;
    private InetAddress destinationIP;
    private int destinationPort;
    public WebcamDisplayWithUDP(String destinationIP, int destinationPort) {
        this.destinationPort = destinationPort;
        try {
            this.destinationIP = InetAddress.getByName(destinationIP);
            this.socket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        public void sendBufferedImageOverUDP(BufferedImage img) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", baos);
            baos.flush();
            byte[] imageData = baos.toByteArray();
            baos.close();

            DatagramPacket packet = new DatagramPacket(imageData, imageData.length, destinationIP, destinationPort);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public BufferedImage receiveBufferedImageOverUDP() {
    byte[] receiveData = new byte[65537];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

    try {
        socket.receive(receivePacket); // Receive the UDP packet
        // Convert the received data to a BufferedImage
        ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData());
        BufferedImage receivedImage = ImageIO.read(bais);
        return receivedImage;
    } catch (Exception ex) {
        ex.printStackTrace();
        return null;
    }
    }

}
