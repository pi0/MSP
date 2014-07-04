package msp.game.network;

public class ServerInfo {
    String name;
    String IP;
    String map;

    String hash;

    public ServerInfo(String s) {

        if(s==null)return;

        String[] sp=s.split(";");
        name=sp[0];
        IP=sp[1];
        map=sp[2];

        hash=s;

    }
}
