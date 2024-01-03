package ClientHandler;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.sound.sampled.*;

public class MyTargetDataLine {

    private DatagramSocket socket;
    private InetAddress destinationIP;
    private TargetDataLine targetDataLine;
    private SourceDataLine sourceDataLine;
    private int destinationPort;

    public MyTargetDataLine(String destinationIP, int destinationPort) throws UnknownHostException {
        this.destinationPort = destinationPort;

        try {
            this.destinationIP = InetAddress.getByName(destinationIP);
            this.socket = new DatagramSocket(6870);

            // Specify audio format
            AudioFormat audioFormat = getAudioFormat();

            // Get an appropriate target data line
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);

            DataLine.Info sourceDataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceDataLineInfo);
            sourceDataLine.open(audioFormat);
        } catch (LineUnavailableException | SocketException e) {
            e.printStackTrace();
        }
    }

    public void reconnect() {
    // Close the existing socket
    if (socket != null && !socket.isClosed()) {
        socket.close();
    }

    // Create a new socket
    try {
        this.socket = new DatagramSocket(6870);
        
        // Reopen the targetDataLine and sourceDataLine
        AudioFormat audioFormat = getAudioFormat();
        
        // Reopen the targetDataLine
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
        targetDataLine.open(audioFormat);
        
        // Reopen the sourceDataLine
        DataLine.Info sourceDataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceDataLineInfo);
        sourceDataLine.open(audioFormat);
        
    } catch (LineUnavailableException | SocketException e) {
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

    
    public SourceDataLine getSourceDataLine() {
        return sourceDataLine;
    }

    public void startCaptureAndPlayback() {
        // Start capturing audio
        targetDataLine.start();
        sourceDataLine.start();

        new Thread(() -> {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while (true) {
                if (!targetDataLine.isOpen()) {
                    // The targetDataLine is closed, so break out of the loop
                    break;
                }

                bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                sendAudioData(buffer, bytesRead);
            }
        }).start();

        new Thread(() -> {
            byte[] receiveBuffer = new byte[8192];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            while (socket != null && !socket.isClosed()) {
                try {
                    socket.receive(receivePacket);
                    int bytesRead = receivePacket.getLength();
                    if (bytesRead > 0) {
                        adjustVolumeAndPlay(receiveBuffer, bytesRead);
                    }
                } catch (Exception e) {
                    handleException(e);
                }
            }
        }).start();
    }


    public void stopCaptureAndPlayback() {
        // Stop capturing audio
        if (targetDataLine != null && targetDataLine.isOpen()) {
            targetDataLine.stop();
            targetDataLine.close();
        }

        // Stop playback
        if (sourceDataLine != null && sourceDataLine.isOpen()) {
            sourceDataLine.stop();
            sourceDataLine.close();
        }

        // Close the socket
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    private void sendAudioData(byte[] data, int length) {
        try {
            DatagramPacket packet = new DatagramPacket(data, length, destinationIP, destinationPort);
            socket.send(packet);
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void adjustVolumeAndPlay(byte[] receiveBuffer, int bytesRead) {
        float volume = 2.0f; // You can adjust this value to control the volume

        for (int i = 0; i < bytesRead; i += 2) {
            short sample = (short) ((receiveBuffer[i + 1] << 8) | (receiveBuffer[i] & 0xFF));
            sample = (short) (sample * volume);
            receiveBuffer[i] = (byte) sample;
            receiveBuffer[i + 1] = (byte) (sample >> 8);
        }

        sourceDataLine.write(receiveBuffer, 0, bytesRead);
    }

    private void handleException(Exception e) {
        if (!(e instanceof SocketException && e.getMessage().equals("Socket closed"))) {
            e.printStackTrace();
        }
    }
}
