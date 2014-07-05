package msp.game.entities;

import framework.*;
import msp.game.MSPMap;
import msp.game.Player;

import java.util.List;

public class WoodCutter extends Human {

    public WoodCutter(GProperty properties, int id, GGame game) {
        super(properties, id, game);
    }

    @Override
    protected  synchronized  void onThreadCycle(int delayMs) {
        super.onThreadCycle(delayMs);

        GPoint dst = properties.getPoint("dst");

        if (dst == null) {

            int bagSize = properties.getInt("bagSize");
            int wood = properties.getInt("wood");
            int tabarWood = properties.getInt("tabarWood");

            Player p = game.getPlayerByID(getOwner());

            if (wood >= bagSize) {
                Castle c=p.getCastle();
                if(c==null)
                    return;
                if(c.getRect().intersectsWith(this.getRect())) {
                    p.setWood(p.getWood()+wood);
                    properties.remove("wood");

                }else
                    properties.put("dst", c.getRect().getCenter());
            }else {
                Tree nearest = (Tree) ((MSPMap) game.map).findNearestEntity(this, new EntityFilter() {
                    @Override
                    public boolean accepts(GEntity e) {
                        return e instanceof Tree;
                    }
                });
                if (nearest == null)
                    return;//WTF?!

                if (getRect().intersectsWith(nearest.getRect())) {
                    int treeWood = nearest.properties.getInt("wood");
                    if (treeWood <= 0) {
                        game.removeEntity(nearest);
                        onThreadCycle(delayMs);
                        return;
                    }

                    if (tabarWood > treeWood)
                        tabarWood = treeWood;
                    wood += tabarWood;
                    if (wood <= bagSize) {
                        properties.put("wood", wood);
                        nearest.properties.put("wood", treeWood - tabarWood);
                        properties.put("mode", "cutting");
                        properties.put("anim", true);
                    }

                }
                else
                    properties.put("dst",nearest.getRect().getCenter());

            }

        }


        updateHuman();

    }


    @Override
    public List<String> getInfo() {

        List<String> a = super.getInfo();
        a.add("wood :" + properties.getInt("wood") + "");
        return a;

    }
}
