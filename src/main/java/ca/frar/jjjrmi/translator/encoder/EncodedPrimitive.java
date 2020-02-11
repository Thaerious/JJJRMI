package ca.frar.jjjrmi.translator.encoder;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.translator.Constants;
import org.json.JSONObject;

@JJJ(insertJJJMethods=false)
public class EncodedPrimitive extends JSONObject{

    @NativeJS
    public EncodedPrimitive(Object value) {
        this.put(Constants.ValueParam, value);

        switch (value.getClass().getCanonicalName()) {
            case "java.lang.String":
                this.put(Constants.PrimitiveParam, Constants.PrimativeTypeString);
                break;
            case "java.lang.Boolean":
                this.put(Constants.PrimitiveParam, Constants.PrimativeTypeBoolean);
                break;
            case "java.lang.Byte":
                this.put(Constants.PrimitiveParam, Constants.PrimativeTypeNumber);
                break;
            case "java.lang.Character":
                this.put(Constants.PrimitiveParam, Constants.PrimativeTypeString);
                break;
            case "java.lang.Short":
                this.put(Constants.PrimitiveParam, Constants.PrimativeTypeNumber);
                break;
            case "java.lang.Long":
                this.put(Constants.PrimitiveParam, Constants.PrimativeTypeNumber);
                break;
            case "java.lang.Float":
                this.put(Constants.PrimitiveParam, Constants.PrimativeTypeNumber);
                break;
            case "java.lang.Double":
                this.put(Constants.PrimitiveParam, Constants.PrimativeTypeNumber);
                break;
            case "java.lang.Integer":
                this.put(Constants.PrimitiveParam, Constants.PrimativeTypeNumber);
                break;
        }
    }
}
