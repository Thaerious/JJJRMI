package ca.frar.jjjrmi.test.testable;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.socket.JJJObject;

@JJJ(retain = false)
public class NoRetain{
    int x = 5;
}