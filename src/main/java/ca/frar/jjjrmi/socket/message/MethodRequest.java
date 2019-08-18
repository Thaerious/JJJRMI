package ca.frar.jjjrmi.socket.message;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.annotations.ProcessLevel;
import ca.frar.jjjrmi.annotations.SkipJS;
import static ca.frar.jjjrmi.socket.message.ClientMessageType.METHOD_REQUEST;
import java.lang.reflect.Array;
import java.lang.reflect.Parameter;

/**
 * Message from client to server to invoke a server side method.
 * @author Ed Armstrong
 */
@SuppressWarnings("PublicField")
@JJJ(retain=false, processLevel=ProcessLevel.ALL)
public class MethodRequest extends ClientMessage{
    public String uid;
    public String objectPTR;
    public String methodName;
    public Object[] methodArguments;

    @SkipJS
    private MethodRequest(){
        super(METHOD_REQUEST);
    }

    public MethodRequest(String uid, String ptr, String methodName, Object[] args){
        super(METHOD_REQUEST);
        this.uid = uid;
        this.objectPTR = ptr;
        this.methodName = methodName;
        this.methodArguments = args;
    }

    /**
    This method is called after the method request is decoded to fit the received parameter values into the expected
    parameter types.  The method parameter types are not known when the message is first decoded.
    @param parameters
    */
    @SkipJS
    public void update(Parameter[] parameters) {
        for (int i = 0; i < parameters.length; i++){
            Parameter parameter = parameters[i];
            if (methodArguments[i] == null) continue;

            if (parameter.getType().isArray()){
                Object[] argument = (Object[]) methodArguments[i];
                Object newInstance = Array.newInstance(parameter.getType().getComponentType(), argument.length);
                for (int j = 0; j < argument.length; j++){
                    Array.set(newInstance, j, argument[j]);
                }
                methodArguments[i] = newInstance;
                return;
            }

            switch(parameter.getType().getCanonicalName()){
                case "java.lang.String":
                    break;
                case "boolean":
                case "java.lang.Boolean":
                    break;
                case "byte":
                case "java.lang.Byte":
                    methodArguments[i] = Byte.parseByte(methodArguments[i].toString());
                    break;
                case "char":
                case "java.lang.Character":
                    methodArguments[i] = methodArguments[i].toString().charAt(0);
                    break;
                case "short":
                case "java.lang.Short":
                    methodArguments[i] = Short.parseShort(methodArguments[i].toString());
                    break;
                case "long":
                case "java.lang.Long":
                    methodArguments[i] = Long.parseLong(methodArguments[i].toString());
                    break;
                case "float":
                case "java.lang.Float":
                    methodArguments[i] = Float.parseFloat(methodArguments[i].toString());
                    break;
                case "double":
                case "java.lang.Double":
                    methodArguments[i] = Double.parseDouble(methodArguments[i].toString());
                    break;
                case "int":
                case "java.lang.Integer":
                    break;
            }
        }
    }

    @SkipJS
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(methodName);
        builder.append("(");
        for (int i = 0; i < methodArguments.length; i++){
            Object obj = methodArguments[i];
            if (obj != null) {
                builder.append(obj.getClass().getSimpleName()).append(":").append(obj.toString());
            } else {
                builder.append("null");
            }
            if (i != methodArguments.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(")");
        return builder.toString();
    }
}