package ca.frar.jjjrmi.translator;

public class EncodedNull extends EncodedJSON{
    public EncodedNull(){
        super(null);
        this.put(Constants.TypeParam, Constants.NullValue);
    }
}