package msp.game.entities;

import msp.game.MSPEntity;
import framework.*;

import java.util.List;


public class Castle extends MSPEntity {


    public Castle(GProperty properties, int id, GGame game) {
        super(properties, id, game);

    }

    @Override
    public List<String> getInfo() {
        List<String> a = super.getInfo();

        a.add("Health :" + properties.getStr("health"));

        return a;
    }

    @Override
    public List<String> getButtons() {
        List<String> a = super.getButtons();

        a.add("human.worker.icon;0;Make a worker;make");

        return a;
    }

    @Override
    public void onAction(String command) {

        if ("make".equals(command)) {

            Worker w = (Worker) GEntity.inflate(GEntity.entityDefaultProperties.get("worker"), game);
            w.setOwner(getOwner());

            GRect r=getRect();
            GPoint a=r.getBottomRight();
            a.x+=10;
            a.y+=10;
            w.setLocation(r.location);
          //  w.properties.put("dst",r.getBottomRight());

            game.addEntity(w);
        }

    }
}