package ca.frar.jjjrmi.socket.observer.events;

import javax.websocket.Session;

abstract class JJJEvent {
    private boolean preventDefaultFlag = false;
    private boolean stopPropogationFlag = false;
    private final Session session;

    JJJEvent(Session session){
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    public void preventDefault(){
        preventDefaultFlag = true;
    }

    public void stopPropogation(){
        this.stopPropogationFlag = true;
    }

    public boolean isDefaultPrevented(){
        return preventDefaultFlag;
    }

    public boolean isPropgationStopped(){
        return this.stopPropogationFlag;
    }
}
