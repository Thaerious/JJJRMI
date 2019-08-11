package ca.frar.jjjrmi.socket.message;

import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.JJJOptions;

@JJJ()
@JJJOptions(retain=false)
public enum ClientMessageType {
    METHOD_REQUEST, INVOCATION_RESPONSE
}
