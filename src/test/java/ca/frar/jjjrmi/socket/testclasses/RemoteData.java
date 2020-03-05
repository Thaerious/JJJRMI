package ca.frar.jjjrmi.socket.testclasses;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.ServerSide;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.socket.JJJObject;
import java.util.ArrayList;

public class RemoteData extends JJJObject{
    @Transient
    ArrayList<Integer> list = new ArrayList<>();
    
    @Transient
    int mem = 0;

    @ServerSide
    public void clear() {
        this.list.clear();
    }    
    
    @ServerSide
    public int peek() {
        return list.get(list.size() - 1);
    }
    
    @ServerSide
    public int pop() throws NoDataException {
        if (this.list.isEmpty()) throw new NoDataException();
        Integer removed = list.remove(list.size() - 1);
        if(!this.list.isEmpty()) this.invokeClientMethod("update", this.peek());
        return removed;
    }

    @ServerSide
    public void push(int value) {
        list.add(value);
        this.invokeClientMethod("update", value);
    }
    
    @ServerSide
    public int size() {
        return list.size();
    }    
    
    @ServerSide
    public void op(Operation op) throws NoDataException{
        op.run(this);
    }
    
    @ServerSide
    public void inst(Instruction inst){
        if (inst == null) throw new NullPointerException();
        
        switch (inst){
            case CLEAR_ALL:
                this.clear();
                mem = 0;
            break;
            case MEM_SAVE:
                this.mem = this.peek();
            break;
            case MEM_PUSH:
                this.push(this.mem);
            break;            
        }
    }
    
    @NativeJS("update")
    private void jsUpdate(int value){
        /*JS{
            if (this.onUpdate) this.onUpdate(value);
        }*/
    }    
}
