package ca.frar.jjjrmi.translator.testclasses;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.rmi.socket.JJJObject;

@JJJ(retain = false)
public class NoRetain{
    int x = 5;
}