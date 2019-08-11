package ca.frar.stream;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TemplateVariableReader extends Reader{
    private Map<String, String> varMap = new HashMap<>();
    private Map<String, Function<String, String>> cbMap = new HashMap<>();
    
    private final PushbackReader pushbackReader;
    private boolean eos = false;
    private final int MAX_VAR_SZ = 256;
    
    public TemplateVariableReader(Reader reader){
        this.pushbackReader = new PushbackReader(reader, MAX_VAR_SZ);
    }

    public TemplateVariableReader set(String key, String value){
        this.cbMap.remove(key);
        this.varMap.put(key, value);
        return this;
    }
    
    public TemplateVariableReader set(String key, Function<String, String> callback){
        this.varMap.remove(key);
        this.cbMap.put(key, callback);
        return this;
    }

    public TemplateVariableReader unset(String key){
        this.varMap.remove(key);
        this.cbMap.remove(key);
        return this;
    }
    
    public int read() throws IOException{
        int data = this.pushbackReader.read();
        if (data != '$') return data;
        data = this.pushbackReader.read();
        if (data != '{'){
            this.pushbackReader.unread(data);
            return '$';
        }
        
        StringBuilder builder = new StringBuilder();
        data = this.pushbackReader.read();
        int dataCount = 0;
        
        while (data != '}'){
            if (data == -1) throw new IOException("Variable not closed, expecting '}'.");
            builder.append((char)data);
            data = this.pushbackReader.read();
            dataCount++;
            if (dataCount > MAX_VAR_SZ) throw new IOException("Variable name buffer overflow.");
        }
        
        String varName = builder.toString();
        String varValue;
        if (varMap.containsKey(varName)){
            varValue = varMap.get(varName);        
        }
        else if (cbMap.containsKey(varName)){
            varValue = cbMap.get(varName).apply(varName);
        }
        else {
            varValue = "${" + varName + "}";            
        }
        
        char[] cbuf = varValue.toCharArray();
        this.pushbackReader.unread(cbuf, 0, varValue.length());        
        return this.pushbackReader.read();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int pos = off;
        int read = 0;
        if (eos) return -1;
        
        while (pos < off + len){
            int c = read();
            if (c == -1){
                eos = true;
                return read;
            }
            cbuf[pos++] = (char)c;
            read++;
        }
        return read;
    }

    @Override
    public void close() throws IOException {
        this.pushbackReader.close();
    }
}