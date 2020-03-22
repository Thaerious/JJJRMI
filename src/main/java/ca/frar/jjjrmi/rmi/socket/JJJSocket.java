/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.rmi.socket;

import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.rmi.ReadyMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/**
 * Abstract class to handle a single client connection to the server.
 * this.setReady, needs to be the first operation called as it sends the root
 * object to the client.
 * 
 * Whatever method/class handles incoming text sends the text to the 
 * processRequest(string) method.  Outgoing text requires the sendText method
 * to be implemented.
 * @author Ed Armstrong
 */
public abstract class JJJSocket<T> extends Endpoint  {
    private HashMap<Session, MsgHandler> handlerMap = new HashMap<>();

    public abstract T getRoot();
    
    @Override
    public final void onOpen(Session session, EndpointConfig ec) {
        synchronized (this) {
            try {
                MsgHandler msgHandler = new MsgHandler(session);
                handlerMap.put(session, msgHandler);
                session.addMessageHandler(String.class, msgHandler);
                ReadyMessage<T> readyMessage = new ReadyMessage<>(this.getRoot());
                msgHandler.sendObject(readyMessage);
            } catch (InvalidJJJSessionException | JJJRMIException | IOException ex) {
                Logger.getLogger(JJJSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void onClose(Session session, CloseReason closeReason) {
        ca.frar.jjjrmi.Global.LOGGER.info("Closing session: " + session.hashCode());
        MsgHandler handler = handlerMap.remove(session);
        handler.close();
    }    
}