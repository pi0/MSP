package framework;

import java.util.ArrayList;
import java.util.Map;

public class GGame {

    public ArrayList<GEntity> entities = new ArrayList();
    public GMap map = null;

    protected ArrayList<GEntityChangeEvent> changeEventArrayList =
            new ArrayList<GEntityChangeEvent>();
    ;

    public GGame() {

    }

    public GGame(GMap map) {
        this.map = map;

    }

    public void load(GProperty mapData) {
        entities.clear();
        //Load map entries
        ArrayList<Map> e = (ArrayList<Map>) mapData.get("entities");
        if (e != null)
            for (Map m : e)
                addEntity(GEntity.inflate(new GProperty(m), getEntityID(), this));

        map.loadMap(mapData);
    }

    //=======================================================================================
    //  Entity management
    //=======================================================================================

    public void addEntity(GEntity e) {
        e.setUpdateNeeded(true);
        entities.add(e);
        callEntityChanged(e);
    }

    public void removeEntity(GEntity entity) {
        //Clean it from map
        entity.properties.put("image", null);
        entity.stopWorkingThread();
        entity.setUpdateNeeded(true);
        map.drawEntities(true);
        //Remove
        entities.remove(entity);
        callEntityChanged(entity);
    }

    int lastEntityID = 100;

    public int getEntityID() {
        return lastEntityID++;
    }

    public GEntity getEntityByID(int i) {
        for (GEntity e : entities)
            if (e.getID() == i)
                return e;
        return null;
    }

    //=======================================================================================
    //  Events
    //=======================================================================================

    protected void callEntityChanged(GEntity e) {
        for (GEntityChangeEvent evnt : changeEventArrayList)
            evnt.onEntityChanged(e);
    }

    public void addEntitychangeListener(GEntityChangeEvent e) {
        changeEventArrayList.add(e);
    }

}