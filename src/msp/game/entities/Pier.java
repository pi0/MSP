package msp.game.entities;

import framework.*;
import msp.game.MSPEntity;
import msp.game.Player;

import java.util.List;

public class Pier extends MSPEntity{


    public Pier(GProperty properties, int id, GGame game) {
        super(properties, id, game);
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();



        return info;
    }

    @Override
    public List<String> getButtons() {
        List<String> buttons = super.getButtons();

        buttons.add("human.boat.icon;0;Make a boat;make");

        return buttons;
    }

    @Override
    public void onAction(String command) {
        super.onAction(command);

        if("make".equals(command)) {

            Player p = game.getPlayerByID(getOwner());
            if(p.getWood() >= 300 || true) {
                Boat b = (Boat) GEntity.inflate(GEntity.entityDefaultProperties.get("boat"), game);
                b.setOwner(getOwner());

                GRect r = getRect();
                GPoint a = r.getBottomRight();
                a.x += 10;
                a.y += 10;
                p.setWood(p.getWood()-300);
                b.setLocation(a);
                game.addEntity(b);

            }
//            w.setLocation(r.location);
//              w.properties.put("dst",r.getBottomRight());

        }

    }
}


