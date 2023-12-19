package ClientHandler;

import javax.sound.sampled.*;

public class MyTargetDataLine {

    private TargetDataLine targetDataLine;

    public MyTargetDataLine() {
        try {
            // Specify audio format
            AudioFormat audioFormat = getAudioFormat();

            // Get an appropriate target data line
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
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

    public void startCapture() {
        // Start capturing audio
        targetDataLine.start();

        // Create a buffer for captured data
        int bufferSize = (int) targetDataLine.getBufferSize() / 5;
        byte[] buffer = new byte[bufferSize];

        // Continuously read and process captured data
        while (true) {
            int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
            // Process the captured audio data as needed
            // (e.g., send it over a network, save it to a file, etc.)
        }
    }

    public void stopCapture() {
        // Stop capturing audio
        targetDataLine.stop();
        targetDataLine.close();
    }

    public static void main(String[] args) {
        MyTargetDataLine myTargetDataLine = new MyTargetDataLine();
        myTargetDataLine.startCapture();
    }
}
