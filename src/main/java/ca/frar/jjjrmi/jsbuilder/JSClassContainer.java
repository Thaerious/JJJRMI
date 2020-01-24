package ca.frar.jjjrmi.jsbuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JSClassContainer implements Iterable<JSClassBuilder<?>>{
    private final Map<String, JSClassBuilder<?>> map = new HashMap<>();
    private final List<JSClassBuilder<?>> jsClassBuilders = new ArrayList<>();

    public void addClass(JSClassBuilder<?> jsClassBuilder){
        int extendIndex = -1;
        int containerIndex = -1;

        int maxIndex = Math.max(extendIndex, containerIndex);
        jsClassBuilders.add(maxIndex + 1, jsClassBuilder);
        map.put(jsClassBuilder.getCtClass().getQualifiedName(), jsClassBuilder);
    }

    public JSClassBuilder<?> getJSClassBuilder(String ctName){
        return map.get(ctName);
    }

    @Override
    public Iterator<JSClassBuilder<?>> iterator() {
        return jsClassBuilders.iterator();
    }
}