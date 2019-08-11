package ca.frar.jjjrmi.socket;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
    private JJJSocket <?> instance;
    private final Class<? extends JJJSocket <?>> rmiClass;

    JJJConfigurator(Class<? extends JJJSocket <?>> rmiClass) {
        LOGGER.debug("Loading JJJ Class Configurator for " + rmiClass.getCanonicalName());
        this.rmiClass = rmiClass;
    }

    /**
    Store 'socket' in the 'httpSession' under the classes canonical name.  If the class has previously been registered
    it wil be over written.
    @param httpSession
    @param socket
    */
    public static void addSocket(HttpSession httpSession, JJJSocket<?> socket) {
        httpSession.setAttribute(socket.getClass().getCanonicalName(), socket);
        ServletContext servletContext = httpSession.getServletContext();
        socket.setContext(servletContext);
    }

    /**
    Remote a previously stored socket.
    @param httpSession
    @param aClass The identifying class of the rmi websocket.
    */
    public static void clearSocket(HttpSession httpSession, Class<? extends JJJSocket<?>> aClass) {
         httpSession.setAttribute(aClass.getCanonicalName(), null);
    }

    /**
    Retrieve a previously stored rmi websocket.
    @param httpSession
    @param aClass The identifying class of the rmi websocket.
    @return
    */
    public static JJJSocket<?> getSocket(HttpSession httpSession, Class<? extends JJJSocket<?>> aClass) {
        return (JJJSocket<?>) httpSession.getAttribute(aClass.getCanonicalName());
    }

    /**
    Determine if a rmi websocket has been stored.
    @param httpSession
    @param aClass The identifying class of the rmi websocket.
    @return
    */
    public static boolean hasSocket(HttpSession httpSession, Class<? extends JJJSocket<?>> aClass) {
        return httpSession.getAttribute(aClass.getCanonicalName()) != null;
    }

    private Object createInstance(HttpSession httpSession) {
        JJJSocket<?> rvalue = null;
        JJJSocket<?> attribute = (JJJSocket<?>) httpSession.getAttribute(this.rmiClass.getCanonicalName());

        if (attribute != null) {
            rvalue = attribute;
        }
        else{
            try {
                Constructor <? extends JJJSocket<?>> constructor = this.rmiClass.getConstructor();
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
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        this.instance = (JJJSocket<?>) createInstance(httpSession);
        JJJConfigurator.addSocket(httpSession, this.instance);
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return (T) this.instance;
    }
}
