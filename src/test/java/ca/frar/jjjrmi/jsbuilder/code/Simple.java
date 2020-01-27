package ca.frar.jjjrmi.jsbuilder.code;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;

public class Simple extends JJJObject{
    
    @NativeJS
    public int foo(int x, int y){
        int r = x * y;
        return r;
    }
    
    @NativeJS
    public Alphabet bar(String x){
        return Alphabet.valueOf(x);
    }    
    
}