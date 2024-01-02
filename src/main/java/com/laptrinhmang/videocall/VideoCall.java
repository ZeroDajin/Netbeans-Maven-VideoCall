/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.laptrinhmang.videocall;

import ClientHandler.MyTargetDataLine;
import ClientHandler.WebcamDisplayWithUDP;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;

/**
 *
 * @author unkno
 */
public class VideoCall extends javax.swing.JFrame {
    private OpenCVFrameGrabber grabber;
    private boolean IsCalling = false;
    private WebcamDisplayWithUDP webcamDisplay;
    private MyTargetDataLine audioCapture;
    /**
     * Creates new form Hub
     */
    public VideoCall() throws UnknownHostException {
        initComponents();
        webcamDisplay = new WebcamDisplayWithUDP("127.0.0.1", 6869);
        audioCapture = new MyTargetDataLine("127.0.0.1", 6870);
    }
    private BufferedImage convertToBufferedImage(Frame frame) {
        int width = frame.imageWidth;
        int height = frame.imageHeight;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        ByteBuffer buffer = (ByteBuffer) frame.image[0];
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        img.getRaster().setDataElements(0, 0, width, height, data);
        return img;
    }
    private void drawOnCanvas(BufferedImage img) {
        Graphics g = UserVideoCapture.getGraphics();
        g.drawImage(img, 0, 0, UserVideoCapture);
    }
    private void drawOnReceiveCanvas(BufferedImage img){
        Graphics g = UserReceiveVideoCapture.getGraphics();
        g.drawImage(img, 0, 0, UserReceiveVideoCapture);
    }
    private void startVideoCaptureWithTwoThreads(){
    new Thread(() -> {
        while (IsCalling) {
            try {
                Frame frame = grabber.grabFrame();
                if (frame != null) {
                    BufferedImage img = convertToBufferedImage(frame);
                    webcamDisplay.sendCompressedBufferedImageOverUDP(img);
                    drawOnCanvas(img);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }).start();
    new Thread(() -> {
        while (IsCalling) {
            try {
                BufferedImage receivedImg = webcamDisplay.receiveCompressedBufferedImageOverUDP();
                drawOnReceiveCanvas(receivedImg);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }).start();
    }
    private void startVoiceChat() {
    new Thread(() -> {
        try {
            // Start capturing and playing audio
            audioCapture.startCaptureAndPlayback();

            while (IsCalling) {
                // Your audio processing logic here

                // For example, check if IsCalling is still true and sleep for a short duration
                // This loop is where you process audio while the call is ongoing

                try {
                    Thread.sleep(5); // You can adjust this duration
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Voice chat stopped, so stop capturing and playing audio
            System.out.println("Voice chat stopped.");
            audioCapture.stopCaptureAndPlayback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Connect = new javax.swing.JButton();
        Disconnect = new javax.swing.JButton();
        UserReceiveVideoCapture = new java.awt.Canvas();
        UserVideoCapture = new java.awt.Canvas();
        turnmyvolume = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Connect.setText("Connect");
        Connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectActionPerformed(evt);
            }
        });

        Disconnect.setText("Disconnect");
        Disconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DisconnectActionPerformed(evt);
            }
        });

        turnmyvolume.setText("tắt âm");
        turnmyvolume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                turnmyvolumeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(UserVideoCapture, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(22, 22, 22)
                .addComponent(UserReceiveVideoCapture, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(turnmyvolume)
                .addGap(42, 42, 42)
                .addComponent(Connect)
                .addGap(36, 36, 36)
                .addComponent(Disconnect)
                .addGap(246, 246, 246))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UserVideoCapture, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UserReceiveVideoCapture, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Disconnect)
                    .addComponent(Connect)
                    .addComponent(turnmyvolume))
                .addGap(0, 58, Short.MAX_VALUE))
        );

        UserVideoCapture.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConnectActionPerformed
        IsCalling = true;
        
        grabber = new OpenCVFrameGrabber(0);
        grabber.setImageMode(FrameGrabber.ImageMode.COLOR); // Set the image mode
        grabber.setFormat("javafx");
        try {
           
            startVoiceChat();
            grabber.start();
            startVideoCaptureWithTwoThreads();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_ConnectActionPerformed

    private void DisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisconnectActionPerformed
        IsCalling = false;
        try{
            audioCapture.stopCaptureAndPlayback();
            grabber.stop();
             audioCapture.reconnect();
             
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }//GEN-LAST:event_DisconnectActionPerformed

    private void turnmyvolumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_turnmyvolumeActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_turnmyvolumeActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VideoCall.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VideoCall.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VideoCall.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VideoCall.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new VideoCall().setVisible(true);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(VideoCall.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Connect;
    private javax.swing.JButton Disconnect;
    private java.awt.Canvas UserReceiveVideoCapture;
    private java.awt.Canvas UserVideoCapture;
    private javax.swing.JButton turnmyvolume;
    // End of variables declaration//GEN-END:variables
}
