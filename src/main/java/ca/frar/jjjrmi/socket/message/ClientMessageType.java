package ca.frar.jjjrmi.socket.message;

import ca.frar.jjjrmi.annotations.JJJ;

@JJJ(retain=false)
public enum ClientMessageType {
    METHOD_REQUEST, INVOCATION_RESPONSE
}
