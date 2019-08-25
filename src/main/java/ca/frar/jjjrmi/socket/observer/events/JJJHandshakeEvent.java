package ca.frar.jjjrmi.socket.observer.events;

import javax.websocket.HandshakeResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;

/**
 *
 * @author Ed Armstrong
 */
public class JJJHandshakeEvent extends JJJEvent {
    private final HandshakeResponse response;
    private final HandshakeRequest request;

    public JJJHandshakeEvent(HandshakeRequest request, HandshakeResponse response) {
        super(null);
        this.request = request;
        this.response = response;
    }

    /**
     * @return the response
     */
    public HandshakeResponse getResponse() {
        return response;
    }

    /**
     * @return the request
     */
    public HandshakeRequest getRequest() {
        return request;
    }

}
