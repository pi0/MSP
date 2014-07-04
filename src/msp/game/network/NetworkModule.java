package msp.game.network;

import framework.GDB;
import framework.GEntity;
import framework.GProperty;
import msp.game.MSPGame;

import java.util.ArrayList;
import java.util.List;


public class NetworkModule {

    MSPGame game;
    List<TCPConnector> to,from;

    public NetworkModule(MSPGame game, TCPConnector server) {
        this(game, toArray(server), toArray(server));
    }

    private static ArrayList<TCPConnector> toArray(TCPConnector c) {
        ArrayList<TCPConnector> a = new ArrayList<TCPConnector>();
        a.add(c);
        return a;
    }

    public NetworkModule(MSPGame game,  List<TCPConnector> from , List<TCPConnector> to) {
        this.game = game;
        this.to = to;
        this.from=from;

        for(TCPConnector c:from) {
            c.listeners.add(new TCPConnectorListener() {
                @Override
                public void onCommandReceived(String command, String[] args) {
                    NetworkModule.this.onCommandRecieved(command, args);
                }
            });
        }

    }

    void onCommandRecieved(String command, String[] args) {

//        GDB.i("Got command :"+command);

        if (command.equals("entity")) {
            int id = Integer.parseInt(args[0]);
        //    GDB.i("Entity update "+args[1]);
            for (GEntity e : (List<GEntity>)game.entities.clone()) {
                if (e.getID() == id) {
                    e.properties.putSilent(args[1], args[2]);//TODO json OR cast args[2]
                    // e.properties.putAllSilent(GProperty.restoreFromJson(args[3])); //TODOO
                    break;
                }
            }
        }else if(command.equals("chat")) {
            GDB.i("Got chat : "+args[0]);
            game.getChat(args[0]);
        }else if(command.equals("entityAdd")) {
            GDB.i("Entity added");
            game.addEntity(GEntity.inflate(GProperty.restoreFromJson(args[0]), -1, game), true);
        } else if(command.equals("entityRemove")) {
            game.removeEntity(game.getEntityByID(Integer.parseInt(args[0])),true);
        } else if(command.equals("fullUpdate")) {
            int id = Integer.parseInt(args[0]);
            for (GEntity e : (List<GEntity>) game.entities.clone()) {
                if (e.getID() == id) {
                    e.properties.putAllSilent(GProperty.restoreFromJson(args[1]));
                    break;
                }
            }
        }


    }

    public void sendEntityChange(int id, String key, Object value,GEntity entity) {

        if(key==null)
            return;

        //TODO : move filters to properties.json
        if (key.equals("x") || key.equals("y") || key.equals("needsUpdate") ||
                key.equals("anim") || key.equals("mode") ||
                key.equals("image") || key.equals("imageFrame") || key.equals("moveDir"))
            return;

        GDB.i("Sending update for Entity : "+id+" , "+key+" = "+value.toString());

        for (TCPConnector to : this.to)
                to.sendCommand("entity", id + "", key, value.toString()/*,entity.properties.toJson()*/);

    }




    public void sendChat(String iMessage) {
        for (TCPConnector to : this.to)
            to.sendCommand("chat", iMessage);
    }

    public void sendEntityAdd(GEntity e) {
        String json=e.properties.toJson();
        for (TCPConnector to : this.to)
            to.sendCommand("entityAdd",json );
    }

    public void sendEntityRemove(GEntity e) {
        for (TCPConnector to : this.to)
            to.sendCommand("entityRemove",e.getID()+"" );
    }

    public void sendFullUpdate(GEntity e) {
        String json=e.properties.toJson();
        for (TCPConnector to : this.to)
            to.sendCommand("fullUpdate",e.getID()+"",json );
    }
}
