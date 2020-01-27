package ca.frar.jjjrmi;
import ca.frar.jjjrmi.jsbuilder.JSClassBuilder;
import ca.frar.jjjrmi.jsbuilder.JSParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Ed Armstrong
 */
public class Base {

    public static void writeClass(JSParser jsParser, String rootPath) throws FileNotFoundException {
        for (JSClassBuilder<?> jsClassBuilder : jsParser.jsClassBuilders()) {
            String outPath = String.format("%s/%s.js", rootPath, jsClassBuilder.getSimpleName());
            File outFile = new File(outPath);
            FileOutputStream fos = new FileOutputStream(outFile);
            PrintWriter pw = new PrintWriter(fos);

            pw.print(jsClassBuilder.fullString());
            pw.print(Global.FILE_STAMP);
            
            DateFormat df = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
            Date dateobj = new Date();
            pw.print("// " + df.format(dateobj) + "\n");
            
            pw.close();
        }
    }
}
