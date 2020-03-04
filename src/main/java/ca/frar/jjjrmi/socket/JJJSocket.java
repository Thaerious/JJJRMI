/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.rmi;

import static ca.frar.jjjrmi.Global.LOGGER;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.exceptions.ParameterCountException;
import ca.frar.jjjrmi.socket.HasWebsockets;
import ca.frar.jjjrmi.socket.InvalidJJJSessionException;
import ca.frar.jjjrmi.socket.InvokesMethods;
import ca.frar.jjjrmi.socket.MethodBank;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.TranslatorResult;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
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
public abstract class ARMIHandle<T> extends Endpoint implements InvokesMethods {
    private MethodBank methodBank;
    private final Translator translator;
    private int uid;

    public ARMIHandle() {
        this.methodBank = new MethodBank();
        this.translator = new Translator();
        this.uid = 0;

        translator.addEncodeListener((obj) -> {
            if (obj instanceof HasWebsockets) ((HasWebsockets) obj).addWebsocket(this);
        });
    }

    public abstract void sendText(String text);
    public abstract T getRoot();
    
    public void makeReady(){
        try {            
            this.sendObject(new ReadyMessage<>(this.getRoot()));
        } catch (InvalidJJJSessionException | JJJRMIException | IOException ex) {
            Logger.getLogger(ARMIHandle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @SuppressWarnings("UseSpecificCatch")
    protected void processRequest(String message) {
        try {
            synchronized (this) {
                ClientMessage clientMessage = (ClientMessage) translator.decode(message).getRoot();
                switch (clientMessage.getType()) {
                    case METHOD_REQUEST:
                        onMethodRequest((MethodRequest) clientMessage);
                        break;
                }
            }
        } catch (Exception ex) {
            LOGGER.warn(ex.getClass().getSimpleName());
            LOGGER.warn(ex.getMessage());
            LOGGER.warn(message);
            if (ex.getCause() != null) LOGGER.catching(ex);
        }
    }

    private void onMethodRequest(MethodRequest request) throws SecurityException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, IOException, InvalidJJJSessionException, JJJRMIException {
        Object object = translator.getReferredObject(request.objectPTR);
        if (object == null) throw new NullPointerException();

        Method method = methodBank.getMethod(object.getClass(), request.methodName);

        if (method == null) {
            this.sendObject(new ServerSideExceptionMessage(request.uid, request.objectPTR, request.methodName, "methodNotFound", "Method " + request.methodName + " not found."));
            return;
        }

        try {
            if (method.getParameters().length != request.methodArguments.length) {
                throw new ParameterCountException();
            }

            request.update(method.getParameters());
            Object returnedFromInvoke;

            try {
                returnedFromInvoke = method.invoke(object, request.methodArguments);
                sendObject(new MethodResponse(request.uid, request.objectPTR, request.methodName, returnedFromInvoke));
            } catch (IllegalAccessException | InvocationTargetException ex) {
                String msg = String.format("Error accessing method %s in class %s. Ensure both class and method are public.", method.getName(), object.getClass().getSimpleName());
                LOGGER.error(msg);
                if (ex.getCause() != null) LOGGER.catching(ex.getCause());
                LOGGER.catching(ex);
                if (ex.getCause() != null) this.sendObject(new ServerSideExceptionMessage(request.uid, request.objectPTR, request.methodName, ex.getCause()));
                else this.sendObject(new ServerSideExceptionMessage(request.uid, request.objectPTR, request.methodName, ex));
            }
        } catch (java.lang.IllegalArgumentException ex) {
            LOGGER.catching(ex);
            if (request.methodArguments.length == 0) {
                LOGGER.warn(object.getClass().getSimpleName() + "." + method.getName() + " could not be invoked with no arguments.");
            } else {
                LOGGER.warn(object.getClass().getSimpleName() + "." + method.getName() + " could not be invoked with the following argument types:");
                for (Object o : request.methodArguments) {
                    if (o != null) LOGGER.warn(": " + o.getClass().getSimpleName());
                    else LOGGER.warn(": null");
                }
            }
            this.sendObject(new ServerSideExceptionMessage(request.uid, request.objectPTR, request.methodName, ex));
        }
    }

    /**
     * Encode an object as a JJJ string and send it to the client.
     *
     * @param obj
     */
    public final void sendObject(JJJMessage msg) throws InvalidJJJSessionException, JJJRMIException, IOException {
        synchronized (this) {
            TranslatorResult encoded = translator.encode(msg);
            String exAsString = encoded.toString();
            this.sendText(exAsString);
        }
    }

    @Override
    public final void invokeClientMethod(Object source, String methodName, Object... args) {
        try {
            ClientRequestMessage remoteInvocation = new ClientRequestMessage("" + uid++, translator.getReference(source), methodName, args);
            sendObject(remoteInvocation);
        } catch (InvalidJJJSessionException | IOException | JJJRMIException ex) {
            Logger.getLogger(ARMIHandle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void close(String localizedMessage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void handleException(Exception ex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void forget(HasWebsockets aThis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
