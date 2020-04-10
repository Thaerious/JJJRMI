package ca.frar.jjjrmi.translator.testclasses;
import ca.frar.jjjrmi.annotations.AfterDecode;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.rmi.socket.JJJObject;

@JJJ
public class HasAfterDecode extends Has<Integer>{

    @NativeJS
    public HasAfterDecode(){
        this.set(5);
    }

    @NativeJS
    @AfterDecode
    public void doAfterDecode(){
        this.set(this.get() * 2);
    }
}
