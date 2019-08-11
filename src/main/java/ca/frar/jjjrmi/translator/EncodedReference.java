package ca.frar.jjjrmi.translator;

public class EncodedReference extends EncodedJSON{
    public EncodedReference(String ref){
        super(null);
        this.put(Constants.PointerParam, ref);
    }
}