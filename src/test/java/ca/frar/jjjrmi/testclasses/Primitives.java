package ca.frar.jjjrmi.testclasses;
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
    
    public Primitives(){}
    
    public Primitives(int x){
        string = "alpha" + x;
        bt = x % 2 == 0;
        bf = !bt;
        bite = (byte) x;
        car = (char) x;
        shrt = (short) (100 * x);
        loooong = 0x7fffffff * (long)x;
        duble = 1.2 * x;
        f = (float) 1.2 * x;
        i = x;        
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != this.getClass()) return false;
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
    
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(string).append("\n");
        builder.append(bt).append("\n");
        builder.append(bf).append("\n");
        builder.append(bite).append("\n");
        builder.append(car).append("\n");
        builder.append(shrt).append("\n");
        builder.append(loooong).append("\n");
        builder.append(duble).append("\n");
        builder.append(f).append("\n");
        builder.append(i).append("\n");
        return builder.toString();
    }
}
