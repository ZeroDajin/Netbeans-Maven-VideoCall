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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.imageio.ImageIO;
/**
 *
 * @author unkno
 */
public class WebcamDisplayWithUDP {
    private DatagramSocket socket;
    private InetAddress destinationIP;
    private int destinationPort;
    private byte[][] circularBuffer = new byte[10][65537];
    private int nextIndex = 0;
    public WebcamDisplayWithUDP(String destinationIP, int destinationPort) {
        this.destinationPort = destinationPort;
        try {
            this.destinationIP = InetAddress.getByName(destinationIP);
            this.socket = new DatagramSocket(6869);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendCompressedBufferedImageOverUDP(BufferedImage img) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", baos);
            baos.flush();
            byte[] imageData = baos.toByteArray();
            baos.close();

            // Compress the image data
            ByteArrayOutputStream compressedStream = new ByteArrayOutputStream();
            try (GZIPOutputStream gzipStream = new GZIPOutputStream(compressedStream)) {
                gzipStream.write(imageData);
            }
            byte[] compressedImageData = compressedStream.toByteArray();

            DatagramPacket packet = new DatagramPacket(compressedImageData, compressedImageData.length, destinationIP, destinationPort);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BufferedImage receiveCompressedBufferedImageOverUDP() {
        byte[] receiveData = new byte[65537];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {
            socket.receive(receivePacket); // Receive the UDP packet

            // Store the received data in the circular buffer
            circularBuffer[nextIndex] = receivePacket.getData();
            nextIndex = (nextIndex + 1) % 10; // Update the index for the next packet

            // Decompress the received data
            ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (GZIPInputStream gzipStream = new GZIPInputStream(bais)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = gzipStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
            }
            byte[] decompressedData = baos.toByteArray();

            // Convert the decompressed data to a BufferedImage
            ByteArrayInputStream imageStream = new ByteArrayInputStream(decompressedData);
            BufferedImage receivedImage = ImageIO.read(imageStream);
            return receivedImage;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
