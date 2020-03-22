package ca.frar.jjjrmi.translator.testclasses;
import ca.frar.jjjrmi.rmi.socket.JJJObject;

public class Simple extends JJJObject{
    public int x = 5;
    public int y = 7;
    public Shapes shape = Shapes.CIRCLE;
}