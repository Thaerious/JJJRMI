/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.management.Query.match;

/**
 *
 * @author Ed Armstrong
 */
public class TestServer extends Thread {

    public static void main(String... args) throws IOException, NoSuchAlgorithmException {
        TestServer testServer = TestServer.listen(8000);
    }

    public static TestServer listen(int port) throws IOException, NoSuchAlgorithmException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server has started on 127.0.0.1:" + port + ".\nWaiting for a connection...");
        Socket client = server.accept();
        TestServer testServer = new TestServer(client);
        testServer.start();
        return testServer;
    }

    private final Socket clientSocket;
    private BufferedReader bsr;
    private OutputStreamWriter osw;
    private OutputStream out;

    public TestServer(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            onConnect();
            HashMap<String, String> headers = listen();
            upgrade(headers.get("Sec-WebSocket-Key"));
            listen();
        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(TestServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void onConnect() throws IOException, NoSuchAlgorithmException {
        System.out.println("A client connected.");
        InputStream in = clientSocket.getInputStream();
        out = clientSocket.getOutputStream();

        InputStreamReader isr = new InputStreamReader(in);
        bsr = new BufferedReader(isr);
        osw = new OutputStreamWriter(out);
    }

    void upgrade(String match) throws NoSuchAlgorithmException, IOException {
        byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                + "Connection: Upgrade\r\n"
                + "Upgrade: websocket\r\n"
                + "Sec-WebSocket-Accept: "
                + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                + "\r\n\r\n").getBytes("UTF-8");
        out.write(response, 0, response.length);
    }

    void write(String message) throws IOException {
        osw.write(message);
        osw.flush();
    }

    HashMap<String, String> listen() throws IOException {
        HashMap<String, String> headers = new HashMap<String, String>();
        
        String line = bsr.readLine();
        while (line != null && !line.isEmpty()) {
            System.out.println(": " + line);
            if (line.contains(": ")){
                int idx = line.indexOf(": ");
                String name = line.substring(0, idx);
                String value = line.substring(idx + 2);
                headers.put(name, value);
            }
            line = bsr.readLine();
        }
        return headers;
    }
}
