package ca.frar.jjjrmi.rmi;

import ca.frar.jjjrmi.annotations.JJJ;

@JJJ(retain=false)
public enum ClientMessageType {
    METHOD_REQUEST, INVOCATION_RESPONSE
}
