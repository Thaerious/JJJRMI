package ca.frar.jjjrmi.socket;
import ca.frar.jjjrmi.rmi.ForgetMessage;
import ca.frar.jjjrmi.rmi.ReadyMessage;
import ca.frar.jjjrmi.rmi.MethodRequest;
import ca.frar.jjjrmi.rmi.ClientMessage;
import ca.frar.jjjrmi.rmi.JJJMessage;
import ca.frar.jjjrmi.rmi.MethodResponse;
import ca.frar.jjjrmi.rmi.RejectedConnectionMessage;
import ca.frar.jjjrmi.rmi.ServerSideExceptionMessage;
import ca.frar.jjjrmi.rmi.ClientRequestMessage;
import ca.frar.jjjrmi.translator.AHandler;
import ca.frar.jjjrmi.translator.TranslatorResult;
import static ca.frar.jjjrmi.Global.VERY_VERBOSE;
import ca.frar.jjjrmi.exceptions.EncoderException;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.exceptions.ParameterCountException;
import ca.frar.jjjrmi.socket.observer.*;
import ca.frar.jjjrmi.socket.observer.events.*;
import ca.frar.jjjrmi.translator.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.websocket.*;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;
import org.apache.logging.log4j.Level;

/**
 * A basic JJJ Socket. It will have the address
 * ws://host/app-name/socket-name<br>
 *
 * @author edward
 */
public abstract class JJJSocket<T> extends Endpoint implements InvokesMethods, ServerApplicationConfig {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger("JJJSOCKET");
    Level VERBOSE = Level.forName("VERBOSE", 450);        
    
    private int nextUID = 0;
    private final MethodBank methodBank = new MethodBank();
    private final HashMap<Session, Translator> sessionTranslators = new HashMap<>();
    final JJJObserverCollection observers = new JJJObserverCollection();
    private ServletContext context;
    private HashMap<String, Class <? extends AHandler<?>>> handlers = new HashMap<>();
    
    public void setHandler(Class<?> aClass, Class <? extends AHandler<?>> handler) {
        this.handlers.put(aClass.getCanonicalName(), handler);
    }

    public boolean hasHandler(Class<?> aClass) {
        return this.handlers.containsKey(aClass.getCanonicalName());
    }    
    
    void setContext(ServletContext servletContext) {
        this.context = servletContext;
    }

    protected ServletContext getContext() {
        return context;
    }

    private class MsgHandler implements MessageHandler.Whole<String> {
        private final Session session;
        private final JJJSocket<T> socket;

        private MsgHandler(JJJSocket<T> socket, Session session) {
            this.session = session;
            this.socket = socket;
        }

        @Override
        public void onMessage(String message) {
            synchronized (socket) {
                socket.processRequest(session, message);
            }
        }
    }

    public final void addObserver(JJJObserver observer) {
        observers.add(observer);
    }

    public final void clearObservers() {
        observers.clear();
    }

    public final void removeObserver(JJJObserver observer) {
        observers.remove(observer);
    }

    @Override
    public final void forget(HasWebsockets forgettable) {
        for (Session session : this.sessionTranslators.keySet()) {
            try {
                Translator translator = getTranslator(session);
                String key = translator.removeTrackedObject(forgettable);
                this.sendObject(session, new ForgetMessage(key));
            } catch (InvalidJJJSessionException | JJJRMIException | IOException ex) {
                LOGGER.catching(ex);
            }
        }
    }

    @SuppressWarnings("UseSpecificCatch")
    private void processRequest(Session session, String message) {
        try {
            synchronized (this) {
                Translator translator = getTranslator(session);
                ClientMessage clientMessage = (ClientMessage) translator.decode(message).getRoot();

                JJJReceiveEvent rmiReceiveEvent = new JJJReceiveEvent(session, clientMessage, message);
                this.observers.receive(rmiReceiveEvent);
                if (rmiReceiveEvent.isDefaultPrevented()) return;

                switch (clientMessage.getType()) {
                    case METHOD_REQUEST:
                        onMethodRequest(session, (MethodRequest) clientMessage);
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

    /**
     * This method is called by the server when a client connects. All of the
     * JJJObservers 'open' methods will be called passing in the rmiOpenEvent
     * object generated by this method. If any observer calls the
     * 'rejectConnection()' method on the event then a
     * 'RejectedConnectionMessage' is sent and the session is terminated. The
     * Websocket will continue to listen for more onOpen events.<br>
     * If no observer calls the preventDefault method of the JJJOpenEvent then
     * the translator this an encode and decode listeners to the translator -
     * which is unique to this session. Then a ReadyMessage is sent to the
     * client. ReadyMessage will contain the result of the abstract
     * intatiateObject method, which must be overridden by the implementer and
     * creates the root object that the client will get other objects from.
     *
     * @param session
     * @param ec
     */
    @Override
    public final void onOpen(Session session, EndpointConfig ec) {
        
        synchronized (this) {            
            Translator translator = new Translator();    
            
            LOGGER.log(VERBOSE, "Socket '" + this.getClass().getSimpleName() + "' adding session " + session.hashCode());
            sessionTranslators.put(session, translator);

            JJJOpenEvent rmiOpenEvent = new JJJOpenEvent(session, translator);
            this.observers.open(rmiOpenEvent);

            /* if any of the listeners reject the open event, terminate opening */
            if (rmiOpenEvent.isRejected()) {
                try {
                    this.sendObject(session, new RejectedConnectionMessage());
                    sessionTranslators.remove(session);
                    return;
                } catch (InvalidJJJSessionException | JJJRMIException | IOException ex) {
                    LOGGER.catching(ex);
                }
            }

            if (rmiOpenEvent.isDefaultPrevented()) return;

            translator.addEncodeListener((obj) -> {
                if (obj instanceof HasWebsockets) ((HasWebsockets) obj).addWebsocket(this);
            });
            
            session.addMessageHandler(new MsgHandler(this, session));

            try {
                this.sendObject(session, new ReadyMessage<>(retrieveRoot()));
            } catch (InvalidJJJSessionException | JJJRMIException | IOException ex) {
                LOGGER.catching(ex);
            }
        }
    }

    public abstract T retrieveRoot();

    private void sendObject(JJJMessage msg) throws InvalidJJJSessionException, JJJRMIException, IOException {
        for (Session session : sessionTranslators.keySet()) {
            this.sendObject(session, msg);
        }
    }

    private Translator getTranslator(Session session) throws InvalidJJJSessionException, JJJRMIException, IOException {
        Translator translator = this.sessionTranslators.get(session);

        if (translator == null) {
            throw new InvalidJJJSessionException();
        }

        return translator;
    }

    /**
     * Encode an object as a JJJ string and send it to the client.
     * @param obj
     */
    private final void sendObject(Session session, JJJMessage msg) throws InvalidJJJSessionException, JJJRMIException, IOException {
        JJJSendEvent<?> rmiSendEvent = new JJJSendEvent<>(session, msg);
        this.observers.send(rmiSendEvent);
        if (rmiSendEvent.isDefaultPrevented()) return;
        Translator translator = getTranslator(session);

        synchronized (this) {
            try {
                LOGGER.log(VERY_VERBOSE, msg.getClass().getSimpleName());
                TranslatorResult encoded = translator.encode(msg);
                String exAsString = encoded.toString();
                session.getBasicRemote().sendText(exAsString);

                JJJSentEvent<?> rmiSentEvent = new JJJSentEvent<>(session, msg, encoded);
                this.observers.sent(rmiSentEvent);
            } catch (IOException | IllegalStateException ex) {
                /* can be caused by the user refreshing the browser while a message is being processed */
                LOGGER.warn(this.getClass().getSimpleName() + " " + ex.getClass().getSimpleName());
                LOGGER.warn(ex.getMessage());   
                this.close(session, ex.getLocalizedMessage());
            } catch (EncoderException ex) {
                LOGGER.warn(ex.getClass().getSimpleName());
                LOGGER.warn(ex.getObject().getClass().getSimpleName());
                LOGGER.catching(ex);

                if (msg instanceof MethodResponse) {
                    handleException(ex, (MethodResponse) msg);
                } else {
                    handleException(ex);
                }
            } catch (Exception ex) {
                LOGGER.catching(ex);
                handleException(ex);
            }
        }
    }

    @Override
    public final void invokeClientMethod(Object source, String methodName, Object... args) {
        for (Session session : this.sessionTranslators.keySet()) {
            Translator translator;
            try {
                translator = getTranslator(session);
                ClientRequestMessage remoteInvocation = new ClientRequestMessage("" + nextUID++, translator.getReference(source), methodName, args);

                JJJMethodInvocationEvent rmiMethodInvocationEvent = new JJJMethodInvocationEvent(session, source, methodName, args);
                this.observers.clientMethodInvocation(rmiMethodInvocationEvent);
                if (rmiMethodInvocationEvent.isDefaultPrevented()) return;

                sendObject(session, remoteInvocation);
            } catch (InvalidJJJSessionException | JJJRMIException | IOException ex) {
                LOGGER.catching(ex);                
            }
        }
    }

    private final void handleException(Exception ex, MethodResponse methodResponse) throws JJJRMIException, IOException {
        for (Session session : this.sessionTranslators.keySet()) this.handleException(session, ex, methodResponse);
    }

    public final void handleException(Exception ex) throws JJJRMIException, IOException {
        for (Session session : this.sessionTranslators.keySet()) this.handleException(session, ex, null);
    }

    private void handleException(Session session, Exception ex, MethodResponse methodResponse) throws JJJRMIException, IOException {
        JJJExceptionEvent exceptionEvent = new JJJExceptionEvent(session, ex);
        this.observers.exception(exceptionEvent);
        if (exceptionEvent.isDefaultPrevented()) return;

        Translator translator;
        try {
            translator = getTranslator(session);
        } catch (InvalidJJJSessionException ex1) {
            LOGGER.catching(ex1);
            return;
        }

        ServerSideExceptionMessage serverSideExceptionMessage;
        if (methodResponse != null) {
            serverSideExceptionMessage = new ServerSideExceptionMessage(methodResponse.getUid(), methodResponse.getObjectPTR(), methodResponse.getMethodName(), ex);
        } else {
            serverSideExceptionMessage = new ServerSideExceptionMessage(ex);
        }
        String exAsString = translator.encode(serverSideExceptionMessage).toString();
        session.getBasicRemote().sendText(exAsString);
        ex.printStackTrace();
    }

    private void onMethodRequest(Session session, MethodRequest request) throws SecurityException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException, IOException, InvalidJJJSessionException, JJJRMIException {
        Translator translator = getTranslator(session);
        Object object = translator.getReferredObject(request.objectPTR);

        /* object was previously sent by this socket */
        if (object == null) {
            /* should never happen in normal running, indicates a coding/logic problem */
            this.sendObject(new ServerSideExceptionMessage(request.uid, request.objectPTR, request.methodName, "objectNotFound", "Null result from getRefferredObject()."));
            LOGGER.error(request.objectPTR);
            throw new NullPointerException();
        }

        JJJMethodRequestEvent methodRequestEvent = new JJJMethodRequestEvent(session, object, request);
        this.observers.serverMethodRequest(methodRequestEvent);
        if (methodRequestEvent.isDefaultPrevented()) return;

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
                sendObject(session, new MethodResponse(request.uid, request.objectPTR, request.methodName, returnedFromInvoke));
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

    @Override
    @OnClose
    public final void onClose(Session session, CloseReason cr) {
        this.close(session, cr.getReasonPhrase());
    }
    
    public final void close(Session session, String reason) {
        LOGGER.log(VERBOSE, "Socket '" + this.getClass().getSimpleName() + "' closing session " + session.hashCode());
        
        Translator removedTranslator = this.sessionTranslators.remove(session);
        JJJCloseEvent closeEvent = new JJJCloseEvent(session);
        
        if (removedTranslator != null) {
            Collection objRef = removedTranslator.getAllTrackedObjects();
            objRef.forEach(obj -> {
                if (obj instanceof HasWebsockets) ((HasWebsockets) obj).removeWebsocket(this);
            });
        }
        
        this.observers.close(closeEvent);
    }    

    @Override
    @OnError
    public final void onError(Session session, Throwable err) {
        Translator removedTranslator = this.sessionTranslators.remove(session);

        JJJExceptionEvent exceptionEvent = new JJJExceptionEvent(session, err);
        this.observers.exception(exceptionEvent);

        if (removedTranslator != null) {
            Collection objRef = removedTranslator.getAllTrackedObjects();
            objRef.forEach(obj -> {
                if (obj instanceof HasWebsockets) ((HasWebsockets) obj).removeWebsocket(this);
            });
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public final Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> set) {
        Set<ServerEndpointConfig> endpointConfigSet = new HashSet<>();
        String endpointName = "/" + this.getClass().getSimpleName();
        ServerEndpointConfig.Builder builder = ServerEndpointConfig.Builder.create(this.getClass(), endpointName);
        builder.configurator(new JJJConfigurator((Class<? extends JJJSocket<?>>) this.getClass()));
        endpointConfigSet.add(builder.build());
        return endpointConfigSet;
    }

    @Override
    public final Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> set) {
        return set;
    }

    public final Collection<Session> getSessions() {
        Set<Session> keySet = this.sessionTranslators.keySet();
        return new ArrayList<>(keySet);
    }
}
