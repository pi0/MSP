package msp.game.network;

import framework.GDB;
import framework.GEntity;
import framework.GResource;
import framework.GUtils;
import msp.game.MSPGame;
import msp.game.MSPGameWindow;
import msp.game.entities.Castle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server extends JFrame{

    MulticastSocket broadcaster ;
    ServerSocket server;


    String serverName="MSP Server";
    String serverIP="127.0.0.1";
    String mapName="default";

    MSPGame game;

    boolean running=true;

    boolean gameStarted=false;

    int connectedClients=0;
    JLabel connectedClientsLabel;

    List<ServerClient> clients=new ArrayList();

    public Server () throws Exception{

        setTitle("MSP - Create a new server");
        setSize(400,200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //TMP
        setLayout(null);
        JButton btn=new JButton("Start game!");
        btn.setSize(100, 30);
        btn.setLocation(145, 120);
        getContentPane().add(btn);
        btn.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();

            }
        });


        //get ip
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        serverIP = addr.getHostName();


        connectedClientsLabel=new JLabel("Ready to connect ("+serverIP+")");
        connectedClientsLabel.setSize(400,30);
        connectedClientsLabel.setLocation(10,80);
        getContentPane().add(connectedClientsLabel);



        broadcaster=new MulticastSocket();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!gameStarted && running) {
                    try {
                        String msg="{"+serverName+";"+serverIP+";"+mapName+"}";
                        byte[] msgB=msg.getBytes("UTF8");
                        broadcaster.send(new DatagramPacket(msgB,msgB.length, InetAddress.getByName("225.255.255.0"),31006));
                    }catch (Exception e) {

                    }
                    GUtils.sleep(500);
                }
            }
        }).start();


        server=new ServerSocket(31007);

        new Thread(new Runnable() {

            @Override
            public void run() {
                while(running) {
                    try {
                        Socket s=server.accept();
                        ServerClient c=new ServerClient(s,Server.this);
                        clients.add(c);
                        new Thread(c).start();
                        GDB.i("New connection");
                        connectedClientsLabel.setText(connectedClients+++1+" Clients connected !");

                    }catch (Exception e) {

                    }
                }
            }
        }).start();



        setVisible(true);
    }

    private void startGame() {

        setVisible(false);

        for(ServerClient c:clients)
            c.requestJoin();

        MSPGameWindow w=new MSPGameWindow(GResource.instance.getMap(mapName),getPlayerID(),"Host");
        w.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                Server.this.dispose();
            }
        });
        w.setVisible(true);


        List<TCPConnector> clientConnectors=new ArrayList();
        for(ServerClient client:clients) {
            clientConnectors.add(client.connector);
        }

        w.game.setNetworkModule(new NetworkModule(w.game,clientConnectors,clientConnectors));

        game=w.game;

        gameStarted=true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (gameStarted) {
                    GUtils.sleep(3000);
                    sendUpdate();
                }
            }
        }).start();

    }

    private void sendUpdate() {
        for(GEntity e:(List<GEntity>)game.entities.clone()) {
            if(e.properties.getBool("passive"))
                continue;
          //  game.getNetworkModule().sendFullUpdate(e);
        }
    }


    @Override



    public void dispose() {
        running=false;
        gameStarted=false;
        super.dispose();
    }

    public static void main(String[] args) {


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            new Server();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    int lastID=10;
    public int getPlayerID() {
        return lastID++;
    }
}
