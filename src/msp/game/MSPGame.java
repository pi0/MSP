package msp.game;

import framework.GEntity;
import framework.GGame;
import framework.GProperty;
import framework.GUtils;
import msp.game.network.NetworkModule;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class MSPGame extends GGame {

    //Player management

    public Player currentPlayer;
    public List<Player> cpuPlayers=new ArrayList<Player>();


    boolean isNight;

    NetworkModule networkModule;

    List<String> chat=new ArrayList();

    Timer timer;
    float time=0;

    List<GameEventHandler> gameEventHandlers=new ArrayList();

    public MSPGame() {
        this(1);
    }
    public MSPGame(int startID) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        boolean i = getTime() > .8;
                        if (i && !isNight)
                            onNight();

                        isNight = i;
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    GUtils.sleep(100);
                }
            }
        }).start();

        timer=new Timer(500,new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                time+=.03f;
                if(time>=1f)
                    time=0;
            }
        });
        timer.start();

    }

    public void disableDayNight() {
        timer.stop();
    }

    public void losePlayer(int id) {
        getPlayerByID(id).setLose();
        for(GEntity e : entities)
            if(((MSPEntity)e).getOwner()==id)
                removeEntity(e);
        if(id==currentPlayer.getID()) {
            JOptionPane.showMessageDialog(map,"Game over","Game over!",JOptionPane.INFORMATION_MESSAGE);
            onEvent(GameEventHandler.GameEvent.GAME_EVENT_GAMEOVER);
        }
    }

    int nightCount=0;

    @Override
    public int getEntityID() {
        return super.getEntityID()*(currentPlayer!=null?currentPlayer.getID()*2000:0);
    }

    private void onNight() {
                if(currentPlayer==null)
                    return;

//                GDB.i("It's night!");

                int food=currentPlayer.getFood()-currentPlayer.usedFood();
                currentPlayer.setFood(food);
                if(food<0)
                    currentPlayer.killPeople();

                for(Player p:cpuPlayers) {
                    food=p.getFood()-p.usedFood();
                    p.setFood(food);
                    if(food<0)
                        p.killPeople();
                }


                if(nightCount++%10==0) {
//                    String session=map.properties.getStr("session");
//                    if(session.equals("summer"))
//                        map.properties.put("session","winter");
//                    else
//                        map.properties.put("session","summer");
//                    map.drawBackground(true);
                }

    }

    void sendChat(String message,String name) {

        String iMessage=name+" : "+message;
        if(networkModule==null)
            return;
        chat.add(iMessage);
        networkModule.sendChat(iMessage);
        if(chat.size()>5)
            chat.clear();
    }


    public void addEventHandler(GameEventHandler handler) {
        gameEventHandlers.add(handler);
    }

    public void onEvent(GameEventHandler.GameEvent event) {
        for(GameEventHandler handler:gameEventHandlers)
            handler.onGameEvent(event);
    }

    public void getChat(String arg) {

        if(chat.size()>5)
            chat.clear();

        chat.add(arg);
    }


    @Override
    public void load(GProperty mapData) {
        super.load(mapData);
    }

    public float getTime() {
        return time;
    }

    public NetworkModule getNetworkModule() {
        return networkModule;
    }

    public void setNetworkModule(NetworkModule networkModule) {
        this.networkModule = networkModule;
    }

    public Player getPlayerByID(int id) {
        return currentPlayer;//TODO ;)
    }


    public Player createPlayer(int id,String name) {

        Player p = new Player(this);
        p.setName(name);

        p.setID(id);

        p.setFood(500);
        p.setWood(400);

        return p;
    }

    public void addPlayer(int playerID, String name, String IP) {
        Player p=createPlayer(playerID,name);
        currentPlayer=p;

//        //Assign an empty castle to this player
//        Castle c = null;
//        for (GEntity e : entities)
//            if (e instanceof Castle) {
//                if (e.properties.getInt("owner") == 0) {
//                    c = (Castle) e;
//                    e.properties.put("owner", p.getID());
//
//                    King k = (King) GEntity.inflate(GEntity.entityDefaultProperties.get("king"), this);
//                    k.setOwner(p.getID());
//                    GPoint point = c.getRect().getBottomRight();
//                    k.setLocation(point);
//                    addEntity(k);
//
//                    break;
//                }
//            }
//        Pier pi = null;
//        for (GEntity e : entities)
//            if (e instanceof Pier) {
//                if (e.properties.getInt("owner") == 0) {
//                    pi = (Pier) e;
//                    e.properties.put("owner", p.getID());
//                    break;
//                }
//            }
//

        //players.add(p);


    }


    public void changeEntity(MSPEntity oldEntity, MSPEntity newEntity) {

        for (String v : oldEntity.getStaticVars())
            newEntity.properties.put(v, oldEntity.properties.get(v));

        addEntity(newEntity);
        removeEntity(oldEntity);

        map.getSelectedEntities().clear();

        map.drawUI();
        map.drawEntities(true);

    }

    @Override
    public void addEntity(final GEntity e) {
        addEntity(e,false);
    }

    public void addEntity(final GEntity e,boolean isSilent) {
        super.addEntity(e);

        if(!isSilent && networkModule!=null)
            networkModule.sendEntityAdd(e);

        e.properties.addChangeListener(new GProperty.GPropertyChangeListener() {
            @Override
            public void onPropertyChange(String key, Object value) {
                if(networkModule!=null)
                    networkModule.sendEntityChange(e.getID(),key,value,e);
            }
        });

    }

    @Override
    public void removeEntity(GEntity entity) {
        removeEntity(entity, false);
    }

    public void removeEntity(final GEntity e,boolean isSilent) {

        if(e==null)
            return;

        super.removeEntity(e);

        if(map.getSelectedEntities().contains(e))
            map.getSelectedEntities().remove(e);

        if(!isSilent && networkModule!=null)
            networkModule.sendEntityRemove(e);
    }


}

