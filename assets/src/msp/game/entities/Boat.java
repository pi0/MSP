package msp.game.entities;

import framework.GEntity;
import framework.GGame;
import framework.GPoint;
import framework.GProperty;
import framework.EntityFilter;
import msp.game.MSPMap;
import msp.game.Player;

import java.util.List;

public class Boat extends Human {

    public Boat(GProperty properties, int id, GGame game) {
        super(properties, id, game);
    }

    @Override
    public List<String> getInfo() {
        List<String> a = super.getInfo();

        a.add("Carry food :"+properties.getInt("carryFood"));

        return a;
    }

    GPoint lll;
    @Override
    public void updateHuman() {

        GPoint loc=getLocation();

        if(lll==null)
            lll=loc;

        int dx=loc.x-lll.x;
        int dy=lll.y-loc.y;

        int d;

        if(dx>0 && dy==0)
            d=0;
        else if(dx<0 && dy==0)
            d=1;
        else if(dx==0 && dy>0)
            d=2;
        else if(dx==0 && dy<0)
            d=3;
        else if(dx>0 && dy>0)
            d=4;
        else if(dx>0 && dy<0)
            d=5;
        else if(dx<0 && dy>0)
            d=6;
        else
            d=7;

        properties.put("imageFrame",d);

        lll=loc;
    }

    @Override
    protected  synchronized  void onThreadCycle(int delayMs) {
        super.onThreadCycle(delayMs);

        if (!properties.isDefined("dst")) {

            int bagSize = properties.getInt("bagSize");
            int food = properties.getInt("carryFood");
            int netFood = properties.getInt("netFood");

            Player p = game.getPlayerByID(getOwner());

            if (food >= bagSize) {
                Pier c=p.getPier();
                if(c.getRect().intersectsWith(this.getRect())) {
                    p.setFood(p.getFood()+food);
                    properties.remove("carryFood");

                }else
                    properties.put("dst", c.getRect().getCenter());
            }else {
                Fish nearest = (Fish) ((MSPMap) game.map).findNearestEntity(this, new EntityFilter() {
                    @Override
                    public boolean accepts(GEntity e) {
                        return e instanceof Fish;
                    }
                });
                if (nearest == null)
                    return;//WTF?!

                if (getRect().intersectsWith(nearest.getRect())) {

                    food += netFood;
                    if (food <= bagSize) {
                        properties.put("carryFood", food);
                    }

                }
                else
                    properties.put("dst",nearest.getRect().getCenter());

            }

        }


   //     updateHuman();

    }


}
