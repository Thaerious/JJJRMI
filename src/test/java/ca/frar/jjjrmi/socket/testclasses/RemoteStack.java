package ca.frar.jjjrmi.socket.testclasses;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.ServerSide;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.socket.JJJObject;
import java.util.ArrayList;

public class RemoteStack extends JJJObject{
    ArrayList<Integer> list;

    @Transient
    int mem = 0;

    private RemoteStack(){}    
    
    public RemoteStack(ArrayList<Integer> list){
        this.list = list;
    }
    
    /**
     * Call this method remotely to cause all clients to clear the stack.
     * @return
     * @throws NoDataException 
     */    
    @ServerSide
    public void clear() {
        this.list.clear();
        this.invokeClientMethod("update", Instruction.CLEAR, 0);
    }

    /**
     * Look at the top value of the stack on the client.
     * @return
     * @throws NoDataException 
     */    
    @NativeJS
    public int peek() {
        return list.get(list.size() - 1);
    }

    /**
     * Call this method remotely to cause all clients to pop a value.
     * @return
     * @throws NoDataException 
     */
    @ServerSide
    public int pop() throws NoDataException {
        if (this.list.isEmpty()) throw new NoDataException();
        Integer removed = list.remove(list.size() - 1);
        if(!this.list.isEmpty()) this.invokeClientMethod("update", Instruction.POP, 0);
        return removed;
    }

    /**
     * Call this method remotely to cause all clients to push a value.
     * used to best passing a number to the server.
     * @return
     * @throws NoDataException 
     */    
    @ServerSide
    public void push(int value) {
        this.list.add(value);
        this.invokeClientMethod("update", Instruction.PUSH, value);
    }

    /**
     * Get the stack size from the server.
     * @return 
     */
    @ServerSide
    public int size() {
        return list.size();
    }

    /**
     * Call this method remotely to save a value.
     * Used to test enums coming from the client.
     * @param code
     * @throws NoDataException 
     */
    @ServerSide
    public void mem(MemOpCode code) throws NoDataException{
        if (code == null) throw new NullPointerException();
        
        switch(code){
            case SAVE:
                this.mem = this.peek();
            break;
            case RESTORE:
                this.push(this.mem);
            break;
            case CLEAR:
                this.mem = 0;
            break;
        }
    }
    
    /**
     * Perform operation of the server, can cause callbacks to the clients.
     * Use to test passing an object to the server, including null.
     * @param op
     * @throws NoDataException 
     */
    @ServerSide
    public void op(Operation op) throws NoDataException{
        if (op == null) throw new NullPointerException();
        op.run(this);
    }

    /**
     * Java calls this method (called up date in JS) to perform PUSH, POP, and
     * CLEAR.  Used to test enums coming from the server.
     * @param inst
     * @param value 
     */
    @NativeJS("update")
    private void jsUpdate(Instruction inst, int value){
        switch(inst){
            case PUSH:
                this.list.add(value);
            break;
            case POP:
                this.list.remove(list.size() - 1);
            break;
            case CLEAR:
                this.list.clear();
            break;
        }
    }
}
