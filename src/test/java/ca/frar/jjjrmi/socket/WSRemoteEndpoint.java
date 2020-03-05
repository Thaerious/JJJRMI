/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.socket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint;

/**
 *
 * @author Ed Armstrong
 */
class WSRemoteEndpoint implements RemoteEndpoint.Basic {
    private final OutputStream outputStream;

    WSRemoteEndpoint(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public static byte[] buildFrame(int opcode, String message) throws IOException {
        int len = message.length();
        byte[] buffer;
        int i;

        if (len <= 125) {
            buffer = new byte[6 + message.length()];
            buffer[1] = (byte) (message.length() + 128);
            i = 2;
        } else if (len <= 65536) {
            buffer = new byte[8 + message.length()];
            buffer[1] = (byte) (254);
            int shift = 0;
            for (i = 3; i >= 2; i--) {
                buffer[i] = (byte) (len >> shift);
                shift += 8;
            }
            i = 4;
        } else {
            buffer = new byte[14 + message.length()];
            buffer[1] = (byte) (255);
            int shift = 0;
            for (i = 9; i >= 2; i++) {
                buffer[i] = (byte) (len >> shift);
                shift += 8;
            }
            i = 10;
        }

        buffer[0] = (byte)opcode;
        buffer[0] += 128;

        try {
            String source = "" + System.currentTimeMillis();
            byte[] key = MessageDigest.getInstance("SHA-1").digest((source).getBytes("UTF-8"));
            for (int j = 0; j < 4; j++) buffer[i++] = key[j];

            byte[] msgBytes = message.getBytes();
            for (int j = 0; j < msgBytes.length; j++) {
                buffer[i++] = (byte) (msgBytes[j] ^ key[j & 0x3]);
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(WSTestServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return buffer;
    }

    @Override
    public void sendText(String message) throws IOException {
        byte[] frame = WSRemoteEndpoint.buildFrame(1, message);
//        System.out.println(": out " + outputStream.hashCode());
//        for (StackTraceElement ele : Thread.currentThread().getStackTrace()){
//            System.out.println(ele);
//        }
        outputStream.write(frame);
        outputStream.flush();        
    }

    @Override
    public void sendBinary(ByteBuffer data) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendText(String partialMessage, boolean isLast) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendBinary(ByteBuffer partialByte, boolean isLast) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public OutputStream getSendStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Writer getSendWriter() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendObject(Object data) throws IOException, EncodeException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBatchingAllowed(boolean allowed) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getBatchingAllowed() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void flushBatch() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendPing(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendPong(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
