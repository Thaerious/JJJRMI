/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.socket;

import static ca.frar.jjjrmi.Global.LOGGER;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.exceptions.ParameterCountException;
import ca.frar.jjjrmi.rmi.ClientMessage;
import ca.frar.jjjrmi.rmi.ClientRequestMessage;
import ca.frar.jjjrmi.rmi.JJJMessage;
import ca.frar.jjjrmi.rmi.MethodRequest;
import ca.frar.jjjrmi.rmi.MethodResponse;
import ca.frar.jjjrmi.rmi.ServerSideExceptionMessage;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.TranslatorResult;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.json.JSONException;

/**
 *
 * @author Ed Armstrong
 */
class MsgHandler implements MessageHandler.Whole<String>, InvokesMethods {
    private MethodBank methodBank = new MethodBank();
    private final Session session;
    private final Translator translator = new Translator();
    private int uid = 0;
    
    MsgHandler(Session session) {
        this.session = session;
        
        translator.addEncodeListener((obj) -> {
            if (obj instanceof HasWebsockets) ((HasWebsockets) obj).addInvoker(this);
        });        
    }

    @Override
    public void forget(HasWebsockets aThis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void invokeClientMethod(Object source, String methodName, Object... args) {
        try {
            ClientRequestMessage remoteInvocation = new ClientRequestMessage("" + uid++, translator.getReference(source), methodName, args);
            sendObject(remoteInvocation);
        } catch (InvalidJJJSessionException | IOException | JJJRMIException ex) {
            Logger.getLogger(JJJSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(String message) {
        this.processRequest(message);
    }

    @SuppressWarnings("UseSpecificCatch")
    private void processRequest(String message) {
        try {
            synchronized (this) {
                ClientMessage clientMessage = (ClientMessage) translator.decode(message).getRoot();
                switch (clientMessage.getType()) {
                    case METHOD_REQUEST:
                        onMethodRequest((MethodRequest) clientMessage);
                        break;
                }
            }
        } catch (JSONException ex) {
            LOGGER.warn(ex.getMessage());
            LOGGER.warn("message: \"" + message + "\"");
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
            this.session.getBasicRemote().sendText(encoded.toString());
        }
    }    
}
