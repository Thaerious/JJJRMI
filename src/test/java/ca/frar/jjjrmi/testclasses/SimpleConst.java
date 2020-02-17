package ca.frar.jjjrmi.testclasses;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;

/**
 * Simple class with annotated default constructor provided and variables
 * initialized at declaration.
 * @author Ed Armstrong
 */
public class SimpleConst extends JJJObject{
    int x = 5;
    int y = 7;
    Shapes shape = Shapes.CIRCLE;
    
    @NativeJS
    public SimpleConst(){}
}