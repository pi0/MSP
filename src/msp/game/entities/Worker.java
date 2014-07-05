package msp.game.entities;

import framework.GEntity;
import framework.GGame;
import framework.GProperty;
import msp.game.MSPEntity;

import java.util.ArrayList;
import java.util.List;

public class Worker extends Human {
    public Worker(GProperty properties, int id, GGame game) {
        super(properties, id, game);

    }

    @Override
    public List<String> getInfo() {
        ArrayList<String> a = (ArrayList<String>) super.getInfo();
        a.add("Power :" + properties.getStr("power"));
        return a;
    }

    @Override
    public List<String> getButtons() {
        ArrayList<String> a = new ArrayList();

        a.add("human.woodCutter.icon;0;Make a woodCutter;WoodCutter");
        a.add("human.soldier.icon;0;Make a soldier;soldier");
        a.add("human.archer.icon;0;Make a archer;archer");
        return a;
    }

    @Override
    public void onAction(String command) {
        if (command.equals("WoodCutter")) {
            game.changeEntity(this, (MSPEntity) GEntity.inflate(GEntity.entityDefaultProperties.get("woodCutter"), game));
        }
        if (command.equals("soldier")) {
            game.changeEntity(this, (MSPEntity) GEntity.inflate(GEntity.entityDefaultProperties.get("soldier"), game));
        }
        if (command.equals("archer")) {
            game.changeEntity(this, (MSPEntity) GEntity.inflate(GEntity.entityDefaultProperties.get("archer"), game));
        }
    }
}



