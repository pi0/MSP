package msp.game.entities;

import framework.GEntity;
import framework.GGame;
import framework.GPoint;
import framework.GProperty;
import framework.EntityFilter;
import msp.game.MSPEntity;
import msp.game.MSPMap;
import msp.game.Player;

public class Soldier extends Human {

    public Soldier(GProperty properties, int id, GGame game) {
        super(properties, id, game);
        properties.put("visibility", game.map.getWidth() / 10);
    }

    @Override
    protected void onThreadCycle(int delayMs) {
        super.onThreadCycle(delayMs);

        GPoint dst = properties.getPoint("dst");


        if(dst==null) {
            GEntity nearest = ((MSPMap) game.map).findNearestEntity(this, new EntityFilter() {
                @Override
                public boolean accepts(GEntity e) {
                    if (!(e instanceof Human))
                        return false;
                    if (((Human) e).getOwner() == Soldier.this.getOwner())
                        return false;
                    if (Soldier.this.getLocation().distanceTo(e.getLocation()) > Soldier.this.properties.getInt("visibility"))
                        return false;

                    Player p = game.getPlayerByID(((MSPEntity) e).getOwner());
                    Player Ps = game.getPlayerByID(Soldier.this.getOwner());
//                    if (p.getTeam() == Ps.getTeam())
//                        return false;

                    return true;
                }
            });
            if (nearest != null) {
                if (nearest.getRect().getCenter().distanceTo(Soldier.this.getRect().getCenter())<=
                        Soldier.this.properties.getInt("range")) {
                    properties.put("mode", "attacking");
                    properties.put("anim",true);
                    properties.remove("dst");
                    //Do attack !
                    int health = nearest.properties.getInt("health");
                    nearest.properties.put("health", health - Soldier.this.properties.getInt("power"));
                } else
                    properties.put("dst", nearest.getRect().getCenter());
            }
        }

        updateHuman();
    }
}
