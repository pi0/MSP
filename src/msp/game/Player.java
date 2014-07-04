package msp.game;

import framework.GEntity;
import framework.GProperty;
import msp.game.entities.Castle;
import msp.game.entities.Human;
import msp.game.entities.King;
import msp.game.entities.Pier;

import java.util.List;

public class Player {

    GProperty properties = new GProperty();
    MSPGame game;

    public Player(MSPGame game) {
        this.game = game;
    }


    public int usedFood() {
        int food = 0;
        for (GEntity e : (List<GEntity>)game.entities.clone())
            if (e instanceof MSPEntity && ((MSPEntity) e).getOwner() == getID())
                food += e.properties.getInt("food");
        return food;
    }

    public void killPeople() {

        int food=properties.getInt("food");
        if(food>0)
            return;
        for (GEntity e : (List<GEntity>)game.entities.clone()) {
            if (e instanceof Human && ((MSPEntity) e).getOwner() == getID() && !(e instanceof King))
                if (food <0 ) {
                    food += e.properties.getInt("food");
                    game.removeEntity(e);
                } else
                    break;
        }
        properties.put("food", food);
    }


    public String getName() {
        return properties.getStr("name");
    }

    public void setName(String name) {
        properties.put("name", name);
    }

    public int getID() {
        return properties.getInt("ID");
    }

    public void setID(int ID) {
        properties.put("ID", ID);
    }

    public String getIP() {
        return properties.getStr("IP");
    }

    public void setIP(String IP) {
        properties.put("IP", IP);
    }

    public int getTeam() {
        return properties.getInt("team");
    }

    public void setTeam(int teamNu) {
        properties.put("tram", teamNu);
    }


    public int getWood() {
        return properties.getInt("wood");
    }
    
    public void setWood(int v) {
        properties.put("wood",v);
    }

    public int getFood() {
        return properties.getInt("food");
    }

    public void setFood(int v) {
        properties.put("food",v);
    }

    public Castle getCastle() {
        for(GEntity e: game.entities) {
            if(e instanceof Castle) {
                if(((Castle) e).getOwner()==getID())
                    return (Castle) e;
            }
        }
        return null;
    }

    public Pier getPier() {
        for(GEntity e: game.entities) {
            if(e instanceof Pier) {
                if(((Pier) e).getOwner()==getID())
                    return (Pier) e;
            }
        }
        return null;
}

    public void setLose() {
        properties.put("isLoosed",true);
    }


}
