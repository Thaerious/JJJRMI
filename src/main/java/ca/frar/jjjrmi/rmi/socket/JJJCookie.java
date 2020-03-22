package ca.frar.jjjrmi.rmi.socket;
import static ca.frar.jjjrmi.rmi.socket.JJJCookie.SameSitePolicy.None;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class JJJCookie extends javax.servlet.http.Cookie{
    private static final long serialVersionUID = 1L;
    private String expires; /* todo add to toString */

    @SuppressWarnings("PublicInnerClass")
    public enum SameSitePolicy {Strict, Lax, None};
    private SameSitePolicy sameSitePolicy = None;

    public JJJCookie(String name, Object value) {
        super(name, value.toString());
    }

    @Override
    @SuppressWarnings("CloneDoesntCallSuperClone")
    public Object clone() {
        JJJCookie clone = new JJJCookie(this.getName(), this.getValue());
        clone.setSameSitePolicy(this.sameSitePolicy);
        clone.setMaxAge(this.getMaxAge());
        return clone;
    }    
    
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(this.getName()).append("=").append(this.getValue());
        if (this.getDomain() != null) builder.append(" ;Domain=").append(this.getDomain());
        if (this.getPath() != null) builder.append(" ;Path=").append(this.getPath());
        if (this.getSecure()) builder.append(" ;Secure");
        if (this.isHttpOnly()) builder.append(" ;HttpOnly");
        if (this.getSameSitePolicy() != None) builder.append(" ;SameSite=").append(sameSitePolicy);
        if (this.getMaxAge() != -1) builder.append(" ;Max-Age=").append(this.getMaxAge());
        return builder.toString();
    }

    @Override
    public void setMaxAge(int i){
        super.setMaxAge(i);
        long time = new Date().getTime();
        this.expires = formatDate(new Date(time));
    }

    public SameSitePolicy getSameSitePolicy() {
        return sameSitePolicy;
    }

    public void setSameSitePolicy(SameSitePolicy sameSitePolicy) {
        this.sameSitePolicy = sameSitePolicy;
    }

    private String formatDate(Date date){
        DateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(date);
    }
}
