package ca.frar.jjjrmi.jsbuilder;
import ca.frar.jjjrmi.annotations.JSRequire;
import java.util.Objects;

public class RequireRecord {
    final String name;
    final String postfix;
    final String value;
    
    /**
     * const NAME = require("VALUE").POSTFIX
     * @param name The name of the class
     * @param value The name of the package
     * @param postfix The name of the class in the package
     */
    public RequireRecord(String name, String value, String postfix){
        this.name = name;
        this.value = value;
        this.postfix = postfix;
    }
    
    RequireRecord(JSRequire jsRequire){
        this.name = jsRequire.name();
        this.value = jsRequire.value();
        this.postfix = jsRequire.postfix();
    }        

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final RequireRecord other = (RequireRecord) obj;
        if (!Objects.equals(this.name, other.name)) return false;
        return true;
    }
    
    @Override
    public int hashCode(){
        return name.hashCode();
    }
}