/*
 * To change this license header, choose License Headers inputStream Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template inputStream the editor.
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
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ed Armstrong
 */
public class WSServer extends Thread {

    public static void main(String... args) throws IOException, NoSuchAlgorithmException {
        WSServer testServer = WSServer.listen(8000);
    }

    public static WSServer listen(int port) throws IOException, NoSuchAlgorithmException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server has started on 127.0.0.1:" + port + ".\nWaiting for a connection...");
        Socket client = server.accept();
        WSServer testServer = new WSServer(client);
        testServer.start();
        return testServer;
    }

    private final Socket clientSocket;
    private BufferedReader bsr;
    private InputStream inputStream;
    private String lastMessage;
    private OutputStreamWriter osw;
    private OutputStream out;

    public WSServer(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            onConnect();
            HashMap<String, String> headers = listen();
            upgrade(headers.get("Sec-WebSocket-Key"));
            while (listenWS()) {
                this.send("echoing, " + this.lastMessage);
            }
            this.sendClose();
        } catch (IOException | NoSuchAlgorithmException ex) {
            Logger.getLogger(WSServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void onConnect() throws IOException, NoSuchAlgorithmException {
        System.out.println("A client connected.");
        inputStream = clientSocket.getInputStream();
        out = clientSocket.getOutputStream();

        InputStreamReader isr = new InputStreamReader(inputStream);
        bsr = new BufferedReader(isr);
        osw = new OutputStreamWriter(out);
    }

    void upgrade(String websocketKey) throws NoSuchAlgorithmException, IOException {
        byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                + "Connection: Upgrade\r\n"
                + "Upgrade: websocket\r\n"
                + "Sec-WebSocket-Accept: "
                + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((websocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                + "\r\n\r\n").getBytes("UTF-8");
        out.write(response, 0, response.length);
    }

    boolean listenWS() throws IOException {
        byte[] key = new byte[4];

        int opcode = readOpcode();
        long len = readLen();

        inputStream.read(key);
        byte buffer[] = new byte[(int) len];
        int read = inputStream.read(buffer);

        for (int i = 0; i < read; i++) {
            buffer[i] = (byte) (buffer[i] ^ key[i & 0x3]);
        }

        this.lastMessage = new String(buffer);
        System.out.println(opcode + " " + len + " : " + this.lastMessage);

        return opcode != 8;
    }

    int readOpcode() throws IOException {
        byte[] buffer = new byte[1];
        int read = inputStream.read(buffer);
        if (read <= 0) throw new IOException("Websocket terminated early.");
        return buffer[0] & (byte) 0x0f;
    }

    long readLen() throws IOException {
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

    HashMap<String, String> listen() throws IOException {
        HashMap<String, String> headers = new HashMap<String, String>();

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
        return headers;
    }

    void sendClose() throws IOException {
        byte[] buffer = new byte[6];
        buffer[0] = (byte) 0B10001000;
        buffer[1] = (byte) 0B10000000;
        out.write(buffer);
        out.flush();
    }

    void send(String message) throws IOException {
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
            buffer[i++] = key[0];
            buffer[i++] = key[1];
            buffer[i++] = key[2];
            buffer[i++] = key[3];

            byte[] msgBytes = message.getBytes();
            for (int j = 0; j < msgBytes.length; j++) {
                buffer[i++] = (byte) (msgBytes[j] ^ key[j & 0x3]);
            }

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(WSServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        out.write(buffer);
        out.flush();
    }

}
