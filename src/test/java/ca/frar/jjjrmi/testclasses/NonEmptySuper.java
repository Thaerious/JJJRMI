package ca.frar.jjjrmi.testclasses;
import ca.frar.jjjrmi.annotations.NativeJS;

public class NonEmptySuper extends NonEmptyConstructor{

    @NativeJS
    public NonEmptySuper(){
        super(MyEnum.C);
    }        
}