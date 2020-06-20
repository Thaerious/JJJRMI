package ca.frar.jjjrmi.targetted.handler;
import ca.frar.jjjrmi.annotations.JJJ;
import ca.frar.jjjrmi.rmi.socket.JJJObject;
import org.json.JSONObject;

public class Game extends JJJObject {

    @JJJ
    public enum Type{PUBLIC, PRIVATE, RUNNING}

    public int id;
    public String name;
    public Type type;

    private Game(){}

    Game(int id, String name, Type type){
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String toString(){
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("name", this.name);
        json.put("type", this.type);
        return json.toString(2);
    }
}
