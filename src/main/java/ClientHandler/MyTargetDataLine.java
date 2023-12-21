package ClientHandler;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.sound.sampled.*;

public class MyTargetDataLine {

    private DatagramSocket socket;
    private InetAddress destinationIP;
    private TargetDataLine targetDataLine;
    private SourceDataLine sourceDataLine;
    private int destinationPort;
    public MyTargetDataLine(String destinationIP, int destinationPort) {
        this.destinationPort = destinationPort;
        
        try {
            try{
            this.destinationIP = InetAddress.getByName(destinationIP);
            this.socket = new DatagramSocket(6870);
        }
        catch(Exception e){
             e.printStackTrace();
        }
            // Specify audio format
            AudioFormat audioFormat = getAudioFormat();

            // Get an appropriate target data line
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            DataLine.Info sourceDataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceDataLineInfo);
            sourceDataLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 44100.0F;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public TargetDataLine getTargetDataLine() {
        return targetDataLine;
    }

    public void startCaptureAndPlayback() {
    // Start capturing audio
    targetDataLine.start();
    sourceDataLine.start();

    new Thread(() -> {
        byte[] buffer = new byte[2048];
        int bytesRead;

        while (true) {
            bytesRead = targetDataLine.read(buffer, 0, buffer.length);
            sendAudioData(buffer, bytesRead);
        }
    }).start();

    new Thread(() -> {
        byte[] receiveBuffer = new byte[2048];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        while (true) {
            try {
                socket.receive(receivePacket);
                int bytesRead = receivePacket.getLength();
                if (bytesRead > 0) {
                    sourceDataLine.write(receiveBuffer, 0, bytesRead);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }).start();
}


    public void stopCaptureAndPlayback() {
        // Stop capturing audio
        targetDataLine.stop();
        targetDataLine.close();

        // Stop playback
        sourceDataLine.stop();
        sourceDataLine.close();
    }
    private void sendAudioData(byte[] data, int length) {
        try {
            DatagramPacket packet = new DatagramPacket(data, length, destinationIP, destinationPort);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}