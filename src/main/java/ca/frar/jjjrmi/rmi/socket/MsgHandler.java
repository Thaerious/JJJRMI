/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.frar.jjjrmi.rmi.socket;

import static ca.frar.jjjrmi.Global.VERBOSE;

import ca.frar.jjjrmi.Global;
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
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Ed Armstrong
 */
class MsgHandler implements MessageHandler.Whole<String>, InvokesMethods, Consumer<Object> {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(MsgHandler.class);
    private MethodBank methodBank = new MethodBank();
    private final Session session;
    private final Translator translator = new Translator();
    private int uid = 0;
    private boolean closed = false;

    MsgHandler(Session session) {
        this.session = session;
        translator.addReferenceListener(this);
    }

    public Translator getTranslator(){
        return this.translator;
    }

    /**
     * New referenced objects.
     *
     * @param obj
     */
    public void accept(Object obj) {
        if (obj instanceof HasWebsockets) ((HasWebsockets) obj).addInvoker(this);
    }

    public synchronized void close() {
        this.translator.removeReferenceListener(this);
        this.closed = true;
    }

    @Override
    public synchronized void invokeClientMethod(Object source, String methodName, Object... args) {
        // lazy removal of method invokers from object
        if (this.closed) {
            if (source instanceof HasWebsockets) {
                ((HasWebsockets) source).removeInvoker(this);
            }
            return;
        }

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
            LOGGER.warn(new JSONObject(message).toString(2));
            if (ex.getCause() != null) LOGGER.catching(ex);
        }
    }

    private void onMethodRequest(MethodRequest request) {
        Object object;

        try {
            object = translator.getReferredObject(request.objectPTR);
            Method method = methodBank.getMethod(object.getClass(), request.methodName);
            if (method.getParameters().length != request.methodArguments.length) throw new ParameterCountException();

            request.update(method.getParameters());
            Object returnedFromInvoke = this.invokeMethod(object, method, request);
            this.sendObject(new MethodResponse(request.uid, request.objectPTR, request.methodName, returnedFromInvoke));
        } catch (JJJRMIException | NoSuchMethodException | InvalidJJJSessionException | IOException e) {
            var sse = new ServerSideExceptionMessage(request.uid, request.objectPTR, request.methodName, e);
            this.sendException(sse);
        }
    }

    private Object invokeMethod(Object object, Method method, MethodRequest request) {
        try {
            return method.invoke(object, request.methodArguments);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            String msg = String.format("Could not invoke method '%s' on object '%s'", request.methodName, object.getClass().getSimpleName());
            LOGGER.error(msg);
            LOGGER.error(object.getClass().getCanonicalName() + " " + object.getClass().hashCode());

            LOGGER.error("Expected parameter types:");
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                LOGGER.error(parameterType.getCanonicalName() + " " + parameterType.hashCode());
                LOGGER.error("classloader " + parameterType.getClassLoader().hashCode());
                LOGGER.error(parameterType.getClassLoader());
            }

            int i = 0;
            LOGGER.error("Found parameter types:");
            for (Object methodArgument : request.methodArguments) {
                LOGGER.error(methodArgument.getClass().getCanonicalName() + " " + methodArgument.getClass().hashCode());
                LOGGER.error(parameterTypes[i] == request.methodArguments[i].getClass());
                LOGGER.error("classloader " + request.methodArguments[i].getClass().getClassLoader().hashCode());
                LOGGER.error(request.methodArguments[i].getClass().getClassLoader());
            }

            LOGGER.error("Translator declared classloader: " + this.translator.getClassLoader().hashCode());
            LOGGER.error(this.translator.getClassLoader());

            var sse = new ServerSideExceptionMessage(request.uid, request.objectPTR, request.methodName, e);
            this.sendException(sse);
        }
        return object;
    }

    public final void sendException(ServerSideExceptionMessage msg) {
        Throwable throwable = msg.getThrowable();
        if (throwable == null) {
            ServerSideExceptionMessage.LOGGER.error(msg.getMessage());
        } else {
            ServerSideExceptionMessage.LOGGER.error(throwable.getMessage());
            this.printStackTrace(throwable);
        }

        try {
            this.sendObject(msg);
        } catch (IOException | JJJRMIException | InvalidJJJSessionException e) {
            LOGGER.catching(e);
        }
    }

    private void printStackTrace(Throwable throwable){
        ServerSideExceptionMessage.LOGGER.error(Global.header(throwable.getClass().getSimpleName()));
        ServerSideExceptionMessage.LOGGER.error(throwable.getMessage());
        for (StackTraceElement ste : throwable.getStackTrace()) {
            ServerSideExceptionMessage.LOGGER.error(ste);
        }

        if(throwable.getCause() != null){
            printStackTrace(throwable.getCause());
        }
    }

    /**
     * Encode an object as a JJJ string and send it to the client.
    *
     **/
    public final void sendObject(JJJMessage msg) throws InvalidJJJSessionException, JJJRMIException, IOException {
        synchronized (this) {
            TranslatorResult encoded = translator.encode(msg);
            this.session.getBasicRemote().sendText(encoded.toString());
        }
    }
}
