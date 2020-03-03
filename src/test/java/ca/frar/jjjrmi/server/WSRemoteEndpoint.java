/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.server;

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
class WSRemoteEndpoint implements RemoteEndpoint.Basic{
    private final OutputStream outputStream;

    WSRemoteEndpoint(OutputStream outputStream){
        this.outputStream = outputStream;
    }
    
    @Override
    public void sendText(String message) throws IOException {
        int len = message.length();
        byte[] buffer;
        int i;

        if (len <= 125) {
            buffer = new byte[6 + message.length()];
            buffer[1] = (byte) (message.length() + 128);
            i = 2;
        } else if (len <= 65536) {
            buffer = new byte[8 + message.length()];
            for (i = 0; i < 2; i++) {
                buffer[i + 2] = (byte) ((len >> 24) & 0xff);
            }
        } else {
            buffer = new byte[14 + message.length()];
            for (i = 0; i < 8; i++) {
                buffer[i + 2] = (byte) ((len >> 24) & 0xff);
            }
        }

        buffer[0] = (byte) 0B10000001;

        try {
            String source = "" + System.currentTimeMillis();
            byte[] key = MessageDigest.getInstance("SHA-1").digest((source).getBytes("UTF-8"));
            for (byte b : key) buffer[i++] = b;

            byte[] msgBytes = message.getBytes();
            for (int j = 0; j < msgBytes.length; j++) {
                buffer[i++] = (byte) (msgBytes[j] ^ key[j & 0x3]);
            }

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(WSTestServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.outputStream.write(buffer);
        this.outputStream.flush();
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
