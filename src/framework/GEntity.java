package framework;

import msp.game.MSPGame;

import java.awt.*;
import java.util.Map;

public class GEntity implements Cloneable, Comparable<GEntity> {

    //a static map of entity names to their default properties
    public final static Map<String, GProperty> entityDefaultProperties = GResource.instance.getProperty("properties")
            .getProperty("entities").getSubProperties();

    public final GProperty properties;
    public final GGame game;

    //=======================================================================================
    //  Class related
    //=======================================================================================

    public GEntity(GProperty properties, int id, GGame game) {
        this.properties = properties;
        this.game = game;
        if(id>0)
            properties.put("id", id);

    }

    public static GEntity inflate(String base, GGame game) {
        return inflate(base, 0, game);
    }

    public static GEntity inflate(String base, int id, GGame game) {
        GProperty properties = new GProperty();
        properties.put("instanceof", base);
        return inflate(properties, id, game);
    }

    public static GEntity inflate(GProperty property, MSPGame game) {
        return inflate(property, game.getEntityID(), game);

    }

    public static GEntity inflate(GProperty properties, int id, GGame game) {

        GProperty entityProperties = properties.clone();

        String instance = properties.getStr("instanceof");
        if (instance != null)
            entityProperties.addAdapter(entityDefaultProperties.get(instance));

        entityProperties.addAdapter(game.map.properties);

        String type = entityProperties.getStr("type");
        if (type != null) {
            entityProperties.addAdapter(GResource.instance.getProperty("properties").getProperty("types").getProperty(type));
        }

        //Make entity

        GEntity entity = null;

        String bindClass = entityProperties.getStr("class");
        if (bindClass != null) {
            try {
                entity = (GEntity) Class.forName(bindClass)
                        .getConstructor(GProperty.class, int.class, GGame.class)
                        .newInstance(entityProperties, id, game);
            } catch (Exception e) {
                e.printStackTrace();
                GDB.e("Unable to load class :" + bindClass);
            }
        }

        //If no class is defined or an error occurred during class construction :
        if (entity == null)
            entity = new GEntity(entityProperties, id, game);

        entity.properties.put("needsUpdate", true);

        return entity;
    }

    @Override
    public int compareTo(GEntity o) {
       // return new Integer(getID()).compareTo(o.getID());
        return new Integer(getLocation().y).compareTo(o.getLocation().y);
    }

    public GEntity clone(int id) {
        return inflate(properties, id, game);
    }

    //=======================================================================================
    //  Drawing
    //=======================================================================================

    public GImage getImage() {
        String imgRName = properties.getStr("image");
        if (isEnvironmentObject())
            imgRName += "." + game.map.properties.getStr("session");
        return GResource.instance.getImage(imgRName);
    }

    public void draw(Graphics2D g) {
        GImage i=getImage();
        if (i == null)
            return;

        Image image;
        if (isAnimated() && i.isAnimated())
            image = i.getImage();
        else
            image = i.getImage(properties.getInt("imageFrame"));

        if (properties.isDefined("alpha"))
            GUtils.drawImage(g, image, getRect(), properties.getFloat("alpha"));
        else
            GUtils.drawImage(g, image, getRect());

    }

    public boolean offerUpdate() {
        if (isUpdateNeeded()) {
            setUpdateNeeded(true);
            return true;
        }
        return false;
    }

    public void nextFrame(int c) {
        properties.put("imageFrame", properties.getInt("imageFrame") + c);
    }

    //=======================================================================================
    //  Helpers
    //=======================================================================================

    public boolean intersectsWithOnLayout(GEntity e) {
        if (getLayer() != e.getLayer())
            return false;
        return getRect().intersectsWith(e.getRect());
    }

    boolean isThreadEnabled = false;

    protected void enableWorkingThread() {
        if (isThreadEnabled)
            return;
        isThreadEnabled = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isThreadEnabled) {
                    try {
                        onThreadCycle(100);
                    }catch (Exception e) {
                        GDB.e("Entity thread error : "+e.getMessage());
                        e.printStackTrace();
                    }
                    GUtils.sleep(100);
                }
            }
        }).start();
    }


    public void stopWorkingThread() {
        isThreadEnabled=false;
    }

    protected void onThreadCycle(int delayMs) {}

    //=======================================================================================
    //  Getter and setters
    //=======================================================================================

    public int getID() {
        return properties.getInt("id");
    }

    public boolean isAnimated() {
        return properties.getBool("anim");
    }

    public GSize getSize() {
        return new GSize(properties.getInt("width"), properties.getInt("height"));
    }

    public int getLayer() {
        return properties.getInt("layer");
    }

    public GPoint getLocation() {
        return new GPoint(properties.getInt("x"), properties.getInt("y"));
    }

    public void setLocation(GPoint p) {
        properties.put("x", p.x);
        properties.put("y", p.y);
        setUpdateNeeded(true);
    }

    public GRect getRect() {
        return new GRect(getLocation(), getSize());
    }

    public GRect getUpdateRect() {
        GRect r = getRect();
        r.resize(new GSize(r.size.width, r.size.height));
        return r;
    }

    public boolean isUpdateNeeded() {
        return properties.getBool("needsUpdate");
    }

    public void setUpdateNeeded(boolean b) {
        properties.put("needsUpdate", b);
    }

    public boolean isEnvironmentObject() {
        return properties.getBool("env");
    }

    public GPoint getGridLocation() {
        return game.map.convertLocToGrid(getLocation());
    }

}
