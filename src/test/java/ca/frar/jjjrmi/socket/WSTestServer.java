package ca.frar.jjjrmi.socket;
import ca.frar.jjjrmi.socket.testclasses.TestSocket;
import ca.frar.jjjrmi.rmi.socket.JJJSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ed Armstrong
 */
public class WSTestServer {

    public static void main(String... args) throws IOException, NoSuchAlgorithmException {
        WSTestServer server = new WSTestServer(new TestSocket());
        server.start(8000).awaitConnection();
    }

    private final JJJSocket jjjSocket;
    private ServerSocket server;
    private boolean running = true;

    public WSTestServer(JJJSocket jjjSocket) {
        this.jjjSocket = jjjSocket;
    }

    public void stop(){
        try {
            System.out.println(": Stopping Server");
            this.running = false;
            this.server.close();            
        } catch (IOException ex) {
            Logger.getLogger(WSTestServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public WSTestServer start(int port) throws IOException{
        System.out.println(": Server has started on 127.0.0.1:" + port);
        this.server = new ServerSocket(port);
        return this;
    }
    
    public WSTestServer awaitConnection() throws IOException, NoSuchAlgorithmException{        
        while(this.running){
            onConnect(server.accept());
        }
        return this;
    }

    private void onConnect(Socket clientSocket) throws IOException, NoSuchAlgorithmException {
        System.out.println(": connected");
        InputStream in = clientSocket.getInputStream();
        OutputStream out = clientSocket.getOutputStream();
        handshake(in, out);
        WSTestSession wsTestSession = new WSTestSession(this.jjjSocket, in, out);
        new Thread(wsTestSession).start();
        jjjSocket.onOpen(wsTestSession, null);
    }

    private HashMap<String, String> handshake(InputStream in, OutputStream out) throws IOException, NoSuchAlgorithmException {
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader bsr = new BufferedReader(isr);
        HashMap<String, String> headers = new HashMap<>();

        String line = bsr.readLine();
        while (line != null && !line.isEmpty()) {
            System.out.println(": " + line);
            if (line.contains(": ")) {
                int idx = line.indexOf(": ");
                String name = line.substring(0, idx);
                String value = line.substring(idx + 2);
                headers.put(name, value);
            }
            line = bsr.readLine();
        }
        
        upgrade(headers.get("Sec-WebSocket-Key"), out);        
        return headers;
    }

    private void upgrade(String websocketKey, OutputStream out) throws NoSuchAlgorithmException, IOException {
        byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
            + "Connection: Upgrade\r\n"
            + "Upgrade: websocket\r\n"
            + "Sec-WebSocket-Accept: "
            + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((websocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
            + "\r\n\r\n").getBytes("UTF-8");
        out.write(response);
    }
}