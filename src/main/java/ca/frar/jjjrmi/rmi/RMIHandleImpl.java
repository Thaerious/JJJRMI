/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.rmi;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.socket.InvalidJJJSessionException;
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
public class RMIHandleImpl<T> extends ARMIHandle<T> {    
    private class MsgHandler implements MessageHandler.Whole<String> {
        private final Session session;
        private final RMIHandleImpl<T> handle;

        private MsgHandler(RMIHandleImpl<T> handle, Session session) {
            this.session = session;
            this.handle = handle;
        }

        @Override
        public void onMessage(String message) {
            handle.processRequest(message);
        }
    }
    
    private Session session;

    public RMIHandleImpl(T root) {
        super(root);
    }

    public final void onOpen(Session session, EndpointConfig ec) {
        this.session = session;
        
        synchronized (this) {
            session.addMessageHandler(new RMIHandleImpl.MsgHandler(this, session));
            this.setReady();
        }
    }

    public void sendText(String text) {
        try {
            session.getBasicRemote().sendText(text);
        } catch (IOException ex) {
            Logger.getLogger(RMIHandleImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
