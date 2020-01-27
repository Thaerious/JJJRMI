package ca.frar.jjjrmi.testable.gamecontroller;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.socket.JJJObject;

/**
 * The root object for a client who has an active game.  A GameController object is created
 * by, and stored in, the game server.  It is retrieved by the root controller and sent to the user.
 * The game controller is associated with a remote model object, which was created based on 
 * player identifier (pid) of the user.
 * @author edward
 */
public class GameController extends JJJObject{
    @Transient private GameInstance gameInstance;
    @Transient private ChatSocket chatSocket;
    public int pid;

    public GameController(GameInstance gameInstance, int pid){
        this.gameInstance = gameInstance;
        this.pid = pid;
    }

    @NativeJS
    public int getPID() {
        return this.pid;
    }
}
