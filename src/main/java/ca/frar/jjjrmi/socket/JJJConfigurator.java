package ca.frar.jjjrmi.socket;
import ca.frar.jjjrmi.socket.observer.events.JJJHandshakeEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/* see http://stackoverflow.com/questions/17936440/accessing-httpsession-from-httpservletrequest-in-a-web-socket-serverendpoint */
public class JJJConfigurator extends ServerEndpointConfig.Configurator {
    final static org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(JJJConfigurator.class);
    private JJJSocket<?> instance;
    private final Class<? extends JJJSocket<?>> rmiClass;

    JJJConfigurator(Class<? extends JJJSocket<?>> jjjSocket) {
        LOGGER.trace("Loading JJJ Class Configurator for " + jjjSocket.getCanonicalName());
        this.rmiClass = jjjSocket;
    }

    /**
     * Store 'socket' in the 'httpSession' under the classes canonical name. If
     * the class has previously been registered it will be over written.
     *
     * @param httpSession
     * @param socket
     */
    public static void addSocket(HttpSession httpSession, JJJSocket<?> socket) {
        LOGGER.trace("JJJConfigurator.addSocket(httpSession, " + socket.getClass().getSimpleName() + ")");
        httpSession.setAttribute(socket.getClass().getCanonicalName(), socket);
        ServletContext servletContext = httpSession.getServletContext();
        socket.setContext(servletContext);
    }

    /**
     * Remote a previously stored socket.
     *
     * @param httpSession
     * @param aClass The identifying class of the rmi websocket.
     */
    public static void clearSocket(HttpSession httpSession, Class<? extends JJJSocket<?>> aClass) {
        LOGGER.trace("JJJConfigurator.clearSocket(httpSession, " + aClass.getSimpleName() + ")");
        httpSession.setAttribute(aClass.getCanonicalName(), null);
    }

    /**
     * Retrieve a previously stored rmi websocket.
     *
     * @param httpSession
     * @param aClass The identifying class of the rmi websocket.
     * @return
     */
    public static JJJSocket<?> getSocket(HttpSession httpSession, Class<? extends JJJSocket<?>> aClass) {
        LOGGER.trace("JJJConfigurator.getSocket(httpSession, " + aClass.getSimpleName() + ")");
        return (JJJSocket<?>) httpSession.getAttribute(aClass.getCanonicalName());
    }

    /**
     * Determine if a rmi websocket has been stored.
     *
     * @param httpSession
     * @param aClass The identifying class of the rmi websocket.
     * @return
     */
    public static boolean hasSocket(HttpSession httpSession, Class<? extends JJJSocket<?>> aClass) {
        LOGGER.trace("JJJConfigurator.hasSocket(httpSession, " + aClass.getSimpleName() + ")");
        return httpSession.getAttribute(aClass.getCanonicalName()) != null;
    }

    private Object createInstance(HttpSession httpSession) {
        LOGGER.trace("JJJConfigurator.createInstance()");
        JJJSocket<?> rvalue = null;
        JJJSocket<?> attribute = (JJJSocket<?>) httpSession.getAttribute(this.rmiClass.getCanonicalName());

        if (attribute != null) {
            rvalue = attribute;
        } else {
            try {
                Constructor<? extends JJJSocket<?>> constructor = this.rmiClass.getConstructor();
                constructor.setAccessible(true);
                rvalue = constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                Logger.getLogger(JJJConfigurator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return rvalue;
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        LOGGER.trace("JJJConfigurator.modifyHandshake()");
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        this.instance = (JJJSocket<?>) createInstance(httpSession);
        JJJConfigurator.addSocket(httpSession, this.instance);       
        this.instance.observers.handshake(new JJJHandshakeEvent(request, response));
    }

    @Override
    public <T> T getEndpointInstance(Class<T> getEndpointInstance) throws InstantiationException {
        LOGGER.trace("JJJConfigurator.getEndpointInstance()");
        return (T) this.instance;
    }
}
