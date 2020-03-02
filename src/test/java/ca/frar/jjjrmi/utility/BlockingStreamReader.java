package ca.frar.jjjrmi.utility;
import ca.frar.jjjrmi.utility.JSGetter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ed Armstrong
 */
class BlockingStreamReader extends Thread {    
    private BufferedReader bsr;
    private boolean doEcho = false;
    private InputStreamReader isr;
    private final InputStream stream;
    private final AcceptsResult<String> outer;

    public BlockingStreamReader(InputStream stream, final AcceptsResult<String> outer) {
        this.outer = outer;
        this.stream = stream;
    }

    public BlockingStreamReader echo(boolean value){
        this.doEcho = value;
        return this;
    }
    
    public void close() throws IOException {
        bsr.close();
        isr.close();
    }

    public void run() {
        isr = new InputStreamReader(stream);
        bsr = new BufferedReader(isr);
        StringBuilder builder = new StringBuilder();
        System.out.println("Waiting for response");
        
        try {
            String line = bsr.readLine();
            if (doEcho) System.out.println("> " + line);
            while (line != null) {
                if (line.charAt(0) == 3) {
                    String string = builder.toString();                    
                    
                    synchronized (outer) {
                        outer.addResult(string);
                        outer.notify();
                    }
                    
                    builder = new StringBuilder();
                    line = bsr.readLine();
                } else {
                    builder.append(line).append("\n");
                    line = bsr.readLine();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(JSGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}