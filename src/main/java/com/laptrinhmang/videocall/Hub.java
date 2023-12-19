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
import java.nio.ByteBuffer;
import javax.swing.ImageIcon;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.opencv_core.Mat;

/**
 *
 * @author unkno
 */
public class Hub extends javax.swing.JFrame {
    private OpenCVFrameGrabber grabber;
    private boolean IsCalling = false;
    private WebcamDisplayWithUDP webcamDisplay;
    private MyTargetDataLine audioCapture;
    /**
     * Creates new form Hub
     */
    public Hub() {
        initComponents();
        webcamDisplay = new WebcamDisplayWithUDP("25.58.17.239", 6869);
        audioCapture = new MyTargetDataLine();
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
                audioCapture.startCapture();

                byte[] audioData = new byte[1024];

                while (IsCalling) {
                    // Read and play audio data
                    audioCapture.read(audioData, 0, audioData.length);
                }

                // Stop capturing and playing audio
                audioCapture.stopCapture();

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
                .addGap(223, 223, 223)
                .addComponent(Connect)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Disconnect)
                .addGap(246, 246, 246))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UserVideoCapture, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(UserReceiveVideoCapture, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(Connect))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(Disconnect)))
                .addGap(0, 42, Short.MAX_VALUE))
        );

        UserVideoCapture.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConnectActionPerformed
        IsCalling = true;
        grabber = new OpenCVFrameGrabber(0);
        try {
            grabber.start();
            startVideoCaptureWithTwoThreads();
            startVoiceChat();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_ConnectActionPerformed

    private void DisconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DisconnectActionPerformed
        IsCalling = false;
        try{
            grabber.stop();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }//GEN-LAST:event_DisconnectActionPerformed

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
            java.util.logging.Logger.getLogger(Hub.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Hub.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Hub.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Hub.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Hub().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Connect;
    private javax.swing.JButton Disconnect;
    private java.awt.Canvas UserReceiveVideoCapture;
    private java.awt.Canvas UserVideoCapture;
    // End of variables declaration//GEN-END:variables
}
