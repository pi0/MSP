package msp.game.network;

import com.sun.org.apache.xpath.internal.SourceTree;
import framework.GDB;
import framework.GResource;

import java.net.Socket;

public class ServerClient implements Runnable {

    TCPConnector connector;
    Server server;



    public ServerClient(Socket socket,Server server) throws Exception{

        this.server=server;

        connector=new TCPConnector();
        connector.listeners.add(new TCPConnectorListener() {
            @Override
            public void onCommandReceived(String command, String[] args) {
                ServerClient.this.onCommandRecieved(command, args);
            }
        });
        connector.connect(socket);

    }

    void onCommandRecieved(String command, String[] args) {

        if (command.equals("handshake")) {
            GDB.i("Client handshake with name :"+args[0]);
        }
    }

    @Override
    public void run() {
        GDB.i("New client connected!");
    }

    public void requestJoin() {

        connector.sendCommand("start",server.getPlayerID()+"", GResource.instance.getMap(server.mapName).toJson());
    }
}
