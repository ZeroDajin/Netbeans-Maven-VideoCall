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
        float sampleRate = 48000.0F;
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
        byte[] buffer = new byte[8192];
        int bytesRead;

        while (true) {
            bytesRead = targetDataLine.read(buffer, 0, buffer.length);
            sendAudioData(buffer, bytesRead);
        }
    }).start();

    new Thread(() -> {
        byte[] receiveBuffer = new byte[8192];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        while (true) {
    try {
        socket.receive(receivePacket);
        int bytesRead = receivePacket.getLength();
        if (bytesRead > 0) {
            // Adjust the volume of each received audio sample
            float volume = 2.0f; // You can adjust this value to control the volume
            for (int i = 0; i < bytesRead; i += 2) {
                short sample = (short) ((receiveBuffer[i + 1] << 8) | (receiveBuffer[i] & 0xFF));
                sample = (short) (sample * volume);
                receiveBuffer[i] = (byte) sample;
                receiveBuffer[i + 1] = (byte) (sample >> 8);
            }

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
        // Adjust the volume by multiplying each audio sample by a volume factor
        float volume = 2.0f; // You can adjust this value to control the volume
        for (int i = 0; i < length; i += 2) {
            short sample = (short) ((data[i + 1] << 8) | (data[i] & 0xFF));
            sample = (short) (sample * volume);
            data[i] = (byte) sample;
            data[i + 1] = (byte) (sample >> 8);
        }

        DatagramPacket packet = new DatagramPacket(data, length, destinationIP, destinationPort);
        socket.send(packet);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    
}