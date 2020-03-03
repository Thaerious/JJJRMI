/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

/**
 *
 * @author Ed Armstrong
 */
public class WSTestSession implements Session, Runnable {

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final ArrayList<MessageHandler.Whole<String>> messageHandlers;
    private boolean openFlag = true;

    private class Message {

        String message;
        int opcode;

        Message(int opcode, String message) {
            this.opcode = opcode;
            this.message = message;
        }
    }

    public void run() {
        try {
            while (this.isOpen()) {
                this.readNextFrame();
            }
        } catch (IOException ex) {
            Logger.getLogger(WSTestSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param inputStream read from input
     * @param outputStream write to output
     */
    WSTestSession(InputStream inputStream, OutputStream outputStream) {
        this.messageHandlers = new ArrayList<>();
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    void sendClose() throws IOException {
        byte[] buffer = new byte[6];
        buffer[0] = (byte) 0B10001000;
        buffer[1] = (byte) 0B10000000;
        this.outputStream.write(buffer);
        this.outputStream.flush();
    }

    private Message readNextFrame() throws IOException {
        byte[] key = new byte[4];

        int opcode = readOpcode();
        long len = readLen();

        inputStream.read(key);
        byte buffer[] = new byte[(int) len];
        int read = inputStream.read(buffer);

        for (int i = 0; i < read; i++) {
            buffer[i] = (byte) (buffer[i] ^ key[i & 0x3]);
        }

        return new Message(opcode, new String(buffer));
    }

    private int readOpcode() throws IOException {
        byte[] buffer = new byte[1];
        int read = inputStream.read(buffer);
        if (read <= 0) throw new IOException("Websocket terminated early.");
        return buffer[0] & (byte) 0x0f;
    }

    private long readLen() throws IOException {
        long len = 0;
        byte buffer[] = new byte[8];

        int read = inputStream.read(buffer, 0, 1);
        if (read <= 0) throw new IOException("Websocket terminated early.");

        len = 128 + buffer[0];

        if (len == 126) {
            read = inputStream.read(buffer, 0, 2);
            if (read <= 0) throw new IOException("Websocket terminated early.");
            len = 0;
            for (int i = 0; i < 2; i++) {
                len = (len << 8) + (buffer[i] & 0xff);
            }
        } else if (len == 127) {
            read = inputStream.read(buffer, 0, 8);
            if (read <= 0) throw new IOException("Websocket terminated early.");
            for (int i = 0; i < 8; i++) {
                len = (len << 8) + (buffer[i] & 0xff);
            }
        }

        return len;
    }

    @Override
    public WebSocketContainer getContainer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addMessageHandler(MessageHandler handler) throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Whole<T> handler) {
        if (clazz != String.class) return;
        this.messageHandlers.add((MessageHandler.Whole<String>) handler);
    }

    @Override
    public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Partial<T> handler) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<MessageHandler> getMessageHandlers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeMessageHandler(MessageHandler handler) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getProtocolVersion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getNegotiatedSubprotocol() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Extension> getNegotiatedExtensions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isOpen() {
        return this.openFlag;
    }

    @Override
    public long getMaxIdleTimeout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMaxIdleTimeout(long milliseconds) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMaxBinaryMessageBufferSize(int length) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getMaxBinaryMessageBufferSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMaxTextMessageBufferSize(int length) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getMaxTextMessageBufferSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RemoteEndpoint.Async getAsyncRemote() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RemoteEndpoint.Basic getBasicRemote() {
        return new WSRemoteEndpoint(this.outputStream);
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() throws IOException {
        this.sendClose();
        this.openFlag = false;
    }

    @Override
    public void close(CloseReason closeReason) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public URI getRequestURI() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, List<String>> getRequestParameterMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getQueryString() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, String> getPathParameters() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, Object> getUserProperties() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Session> getOpenSessions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
