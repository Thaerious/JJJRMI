package ca.frar.jjjrmi.socket.testclasses;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.ServerSide;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.socket.JJJObject;
import java.util.ArrayList;

public class MutableData extends JJJObject{
    @Transient 
    ArrayList<Integer> list = new ArrayList<>();
    
    @NativeJS
    public MutableData(){
    
    }

    /**
     * @return the value
     */
    @ServerSide
    public int peek() {
        return list.get(list.size() - 1);
    }    
    
    /**
     * @return the value
     */
    @ServerSide
    public int pop() throws NoDataException {
        if (this.list.isEmpty()) throw new NoDataException();
        Integer removed = list.remove(list.size() - 1);
        return removed;
    }

    /**
     * @param value the value to set
     */
    @ServerSide
    public void push(int value) {
        list.add(value);
    }
    
    @NativeJS
    public void updateClients(){
        this.invokeClientMethod("update", this.peek());
    }
    
}
