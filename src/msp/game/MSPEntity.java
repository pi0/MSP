package msp.game;

import framework.GEntity;
import framework.GGame;
import framework.GProperty;

import java.util.ArrayList;
import java.util.List;

public class MSPEntity extends GEntity {

    protected MSPGame game;

    public List<String> getInfo() {

        List<String> a = new ArrayList<String>();

        Player owner = game.getPlayerByID(properties.getInt("owner"));
        a.add("Owner :\t" + (owner != null ? owner.getName() : "-"));
   //     a.add("ID : "+getID());


        return a;
    }


    public List<String> getButtons() {
        return new ArrayList<String>();
    }

    public MSPEntity(GProperty properties, int id, GGame game) {
        super(properties, id, game);
        this.game = (msp.game.MSPGame) game;
    }

    public void onAction(String command) {

    }

    public String[] getStaticVars() {
        String s = properties.getStr("staticVars");
        if (s == null)
            return new String[0];
        else
            return s.split(";");
    }

    public int getOwner() {
        return properties.getInt("owner");
    }

    public void setOwner(int owner) {
        properties.put("owner", owner);
    }

    public void setID(int ID) {
        properties.put("ID", ID);
    }



}
