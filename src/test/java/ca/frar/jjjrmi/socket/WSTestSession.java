package ca.frar.jjjrmi.socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
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

public class WSTestSession implements Session, Runnable {
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final ArrayList<MessageHandler.Whole<String>> messageHandlers = new ArrayList<>();
    private boolean openFlag = true;
    private final JJJSocket socket;
    
    private class Message {
        String message;
        int opcode;

        Message(int opcode, String message) {
            this.opcode = opcode;
            this.message = message;
        }
    }

    /**
     *
     * @param inputStream read from input
     * @param outputStream write to output
     */
    WSTestSession(JJJSocket socket, InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.socket = socket;
    }

    public void run() {
        try {
            while (openFlag) {
                Message message = this.readNextFrame();

                System.out.println("+-----------------------");
                System.out.println("| session : " + this.hashCode());
                System.out.println("| opcode : " + message.opcode);
                System.out.println("| hnd-cnt: " + this.messageHandlers.size());
                System.out.println("| in: " + this.inputStream.hashCode());
                System.out.println("| out: " + this.outputStream.hashCode());
                System.out.println("+-----------------------");

                switch (message.opcode) {
                    case 1:
                        for (MessageHandler.Whole<String> msghnd : messageHandlers) {
                            msghnd.onMessage(message.message);
                        }
                        break;
                    case 8:
                        this.close();
                        break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(WSTestSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void sendClose() throws IOException {
        byte[] frame = WSRemoteEndpoint.buildFrame(8, "");
        outputStream.write(frame);
        outputStream.flush();
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
        byte buffer[] = new byte[8];

        int read = inputStream.read(buffer, 0, 1);
        if (read <= 0) throw new IOException("Websocket terminated early.");

        long len = 128 + buffer[0];

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
        if (!this.openFlag) return;
        System.out.println(": closing socket");
        this.sendClose();
        this.openFlag = false;
        this.inputStream.close();
        this.outputStream.close();
        this.socket.onClose(this, null);
    }

    @Override
    public void close(CloseReason closeReason) throws IOException {
        if (!this.openFlag) return;
        System.out.println(": closing socket");
        this.sendClose();
        this.openFlag = false;
        this.inputStream.close();
        this.outputStream.close();
        this.socket.onClose(this, null);
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
