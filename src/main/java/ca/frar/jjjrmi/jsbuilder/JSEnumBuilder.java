package ca.frar.jjjrmi.jsbuilder;
import ca.frar.jjjrmi.jsbuilder.code.JSCodeSnippet;
import java.util.List;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;

public class JSEnumBuilder <T extends Enum> extends JSClassBuilder<T>{

    JSEnumBuilder(CtClass<T> ctClass){
        super(ctClass);
    }

    @Override
    public JSEnumBuilder<T> build(){
        addJJJMethods();
        this.header.setName(getCtClass().getSimpleName());
        addConstructor();

        CtEnum<?> ctEnum = (CtEnum<?>) getCtClass();
        List<CtEnumValue<?>> enumValues = ctEnum.getEnumValues();

        super.addSequel(new JSCodeSnippet(String.format("%s.valueArray = [];", getCtClass().getSimpleName())));

        for (CtEnumValue<?> value : enumValues) {
            super.addSequel(buildMember(getCtClass(), value.getSimpleName()));
        }

        super.addSequel(new JSCodeSnippet(String.format("%s.values = function(){return %s.valueArray;};", getCtClass().getSimpleName(), getCtClass().getSimpleName())));
        return this;
    }

    private void addConstructor(){
        JSMethodBuilder jsMethodBuilder = new JSMethodBuilder();
        jsMethodBuilder.setName("constructor");
        jsMethodBuilder.addParameter("value");
        JSCodeSnippet jsCodeSnippet = new JSCodeSnippet("this.__value = value;");
        jsMethodBuilder.getBody().add(jsCodeSnippet);
        this.addMethod(jsMethodBuilder);
    }

    private JSCodeSnippet buildMember(CtClass<?> ctClass, String value){
        StringBuilder builder = new StringBuilder();
        builder.append(ctClass.getSimpleName()).append(".");
        builder.append("valueArray.push(");
        builder.append(ctClass.getSimpleName()).append(".");
        builder.append(value);
        builder.append(" = new ");
        builder.append(ctClass.getSimpleName());
        builder.append("(\"").append(value).append("\"));");

        return new JSCodeSnippet(builder.toString());
    }

    protected void addJJJMethods() {
        super.addJJJMethods();
        
        JSMethodBuilder jsTransMethod = new JSMethodBuilder();
        jsTransMethod.setStatic(true);
        jsTransMethod.setName("__isTransient");
        jsTransMethod.getBody().add(new JSCodeSnippet("return true;"));
        addMethod(jsTransMethod);

        JSMethodBuilder jsIsEnumMethod = new JSMethodBuilder();
        jsIsEnumMethod.setStatic(true);
        jsIsEnumMethod.setName("__isEnum");
        jsIsEnumMethod.getBody().add(new JSCodeSnippet("return true;"));
        addMethod(jsIsEnumMethod);
        
        JSMethodBuilder jsToString = new JSMethodBuilder();
        jsToString.setName("toString");
        jsToString.getBody().add(new JSCodeSnippet("return this.__value;"));
        addMethod(jsToString);        
    }
}
