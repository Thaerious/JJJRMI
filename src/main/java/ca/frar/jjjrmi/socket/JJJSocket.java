/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.socket;
import ca.frar.jjjrmi.rmi.ARMIHandle;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

/**
 *
 * @author Ed Armstrong
 */
public abstract class JJJSocket<T> extends ARMIHandle<T> {    
    private class MsgHandler implements MessageHandler.Whole<String> {
        private final JJJSocket<?> handle;

        private MsgHandler(JJJSocket<?> handle) {
            this.handle = handle;
        }

        @Override
        public void onMessage(String message) {
            handle.processRequest(message);
        }
    }
    
    private Session session;

    @Override
    public final void onOpen(Session session, EndpointConfig ec) {
        this.session = session;
        
        synchronized (this) {
            session.addMessageHandler(String.class, new JJJSocket.MsgHandler(this));
            this.makeReady();
        }
    }

    @Override
    public void sendText(String text) {
        try {
            session.getBasicRemote().sendText(text);
        } catch (IOException ex) {
            Logger.getLogger(JJJSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
