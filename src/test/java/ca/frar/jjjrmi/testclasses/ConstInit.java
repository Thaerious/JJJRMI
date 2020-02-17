package ca.frar.jjjrmi.testclasses;

import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.socket.JJJObject;

public class ConstInit extends JJJObject {
    int x;
    int y;
    Shapes shape;

    @NativeJS
    public ConstInit() {
        int x = 5;
        int y = 7;
        Shapes shape = Shapes.CIRCLE;
    }
}
