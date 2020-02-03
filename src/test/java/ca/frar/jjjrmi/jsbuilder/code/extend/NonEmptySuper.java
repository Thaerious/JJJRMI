package ca.frar.jjjrmi.jsbuilder.code.extend;
import ca.frar.jjjrmi.annotations.NativeJS;

public class NonEmptySuper extends NonEmptyConstructor{

    @NativeJS
    public NonEmptySuper(){
        super(MyEnum.C);
    }        
}