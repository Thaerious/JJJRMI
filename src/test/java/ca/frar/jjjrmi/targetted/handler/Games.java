package ca.frar.jjjrmi.targetted.handler;
import ca.frar.jjjrmi.annotations.NativeJS;
import ca.frar.jjjrmi.annotations.Transient;
import ca.frar.jjjrmi.exceptions.JJJRMIException;
import ca.frar.jjjrmi.rmi.socket.JJJObject;
import ca.frar.jjjrmi.translator.HandlerFactory;
import ca.frar.jjjrmi.translator.Translator;
import ca.frar.jjjrmi.translator.TranslatorResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class Games extends JJJObject {
    @Transient private static final Logger LOGGER = LogManager.getLogger(Games.class);

    public static void main(String[] args) throws MalformedURLException {
//        HandlerFactory.getInstance().addJar(new File("target/lib/jjjrmi-ext-0.7.0.jar"));
    }

    private ArrayList<Game> list = new ArrayList<>();
    @Transient private int nextID = 0;

    @NativeJS
    public Game newPrivate(String name){
        Game game = new Game(nextID++, name, Game.Type.PRIVATE);
        this.list.add(game);
        super.invokeClientMethod("newGame", game);
        return game;
    }

    @NativeJS
    public Game newPublic(String name){
        Game game = new Game(nextID++, name, Game.Type.PUBLIC);
        this.list.add(game);
        super.invokeClientMethod("newGame", game);
        return game;
    }

    @NativeJS
    public Game startGame(Game game){
        this.list.remove(game);
        Game newGame = new Game(game.id, game.name, Game.Type.RUNNING);
        this.list.add(newGame);

        super.invokeClientMethod("deleteGame", game);
        super.invokeClientMethod("newGame", newGame);

        return newGame;
    }

    @NativeJS
    public void deleteGame(Game game){
        super.invokeClientMethod("deleteGame", game);
        this.list.remove(game);
    }

    @NativeJS
    public Game queryGame(int id){
        for (Game game : this.list) if (game.id == id) return game;
        return null;
    }
}
