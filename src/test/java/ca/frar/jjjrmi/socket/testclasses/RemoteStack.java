package ca.frar.jjjrmi.socket.testclasses;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.ServerSide;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.socket.JJJObject;
import java.util.ArrayList;

public class RemoteStack extends JJJObject{
    private ArrayList<Integer> list;

    @Transient
    int mem = 0;

    private RemoteStack(){}    
    
    public RemoteStack(ArrayList<Integer> list){
        this.list = list;
    }
    
    @ServerSide
    public void clear() {
        this.list.clear();
        jsUpdate(Instruction.CLEAR, 0);
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
        jsUpdate(Instruction.POP, 0);
        return removed;
    }

    @ServerSide
    public void push(int value) {
        list.add(value);
        this.invokeClientMethod("update", value);
        jsUpdate(Instruction.POP, value);
    }

    @ServerSide
    public int size() {
        return list.size();
    }

    @ServerSide
    public void op(Operation op) throws NoDataException{
        op.run(this);
    }

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
