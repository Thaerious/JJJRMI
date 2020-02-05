package ca.frar.jjjrmi.testableclasses;
import ca.frar.jjjrmi.annotations.JJJ;

@JJJ(retain = false)
public class Primitives {
    public String string = "alpha";
    public boolean bt = true;
    public boolean bf = false;
    public byte bite = 7;
    public char car = 'a';
    public short shrt = 100;
    public long loooong = (long)Integer.MAX_VALUE * (long)10;
    public double duble = 1.2;
    public float f = (float) 1.2;
    public int i = -1;
    
    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != Primitives.class) return false;
        Primitives that = (Primitives)obj;
        if (!this.string.equals(that.string)) return false;
        if (this.bt != that.bt) return false;
        if (this.bf != that.bf) return false;
        if (this.bite != that.bite) return false;
        if (this.car != that.car) return false;
        if (this.shrt != that.shrt) return false;
        if (this.loooong != that.loooong) return false;
        if (this.duble != that.duble) return false;
        if (this.i != that.i) return false;
        return true;
    }
}
