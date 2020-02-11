package ca.frar.jjjrmi.testclasses;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;

public class NonEmptyConstructor extends JJJObject{
    private final MyEnum x;

    @NativeJS
    public NonEmptyConstructor(MyEnum x){
        this.x = x;
    }        
}