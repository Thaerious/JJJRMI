package ca.frar.jjjrmi.jsbuilder.testclasses;

import ca.frar.jjjrmi.annotations.AfterDecode;
import ca.frar.jjjrmi.annotations.NativeJS;

import java.util.ArrayList;
import java.util.Iterator;

public class StringCollection extends ModelElement implements Iterable<String>{
    private ArrayList<String> list = new ArrayList<>();

    @Override
    public Iterator<String> iterator() {
        return null;
    }

    @AfterDecode
    @NativeJS
    public void afterDecode(){
        for (String s : this){

        }
    }

    @NativeJS("*[Symbol.iterator]")
    private void JSIterator() {
        /*
        JS{
            for (let ax of this.list){
                if (ax !== null) yield ax;
            }
        }
        */
    }

}
