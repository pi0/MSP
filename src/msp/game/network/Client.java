package msp.game.network;

import com.sun.deploy.util.SessionState;
import framework.GDB;
import framework.GProperty;
import framework.GUtils;
import msp.game.MSPGameWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class Client extends JFrame {

    TCPConnector server;

    JList<String> serverList;
    ArrayList<ServerInfo> discoveredServers = new ArrayList();
    MulticastSocket broadcastListener;

    String playerName = "Player1";

    boolean gameStarted=false;

    public Client() throws IOException {

        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setTitle("MSP - Join game");

        setLayout(new BorderLayout());
        Container p = getContentPane();


        serverList = new JList<String>(new String[0]);
        serverList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);


        p.add(serverList, BorderLayout.CENTER);

        final JPanel btns = new JPanel(new GridBagLayout());
        p.add(btns, BorderLayout.SOUTH);
        JButton join = new JButton("Join");


        join.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ServerInfo i = new ServerInfo(serverList.getSelectedValue());
                server = new TCPConnector();

                server.listeners.add(new TCPConnectorListener() {
                    @Override
                    public void onCommandReceived(String command, String[] args) {
                        Client.this.onCommandReceived(command, args);
                    }
                });
                server.connect(i.IP,31007);
                doHandShake();
                //Wait for start
                Client.this.setVisible(false);
                JOptionPane.showMessageDialog(Client.this,"Waiting for server ...");
            }
        });

        btns.add(join);

        broadcastListener = new MulticastSocket(31006);
        broadcastListener.joinGroup(InetAddress.getByName("225.255.255.0"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buff = new byte[1000];
                DatagramPacket p = new DatagramPacket(buff, buff.length);
                while (!gameStarted) {
                    try {
                        broadcastListener.receive(p);
                        String s = new String(buff, 0, GUtils.countUsableBytes(buff) + 1, "UTF8");
                        if (!s.startsWith("{") || !s.endsWith("}")) {
                            GDB.e("Invalid server info!");
                        } else {
                            ServerInfo info = new ServerInfo(s.substring(1, s.length() - 1));

                            boolean shouldUpdate = true;
                            for (ServerInfo i : discoveredServers) {
                                if (i.hash.equals(info.hash)) {
                                    shouldUpdate = false;
                                    break;
                                } else if (i.name.equals(info.name)) {
                                    discoveredServers.remove(i);
                                    break;
                                }
                            }
                            if (!shouldUpdate)
                                continue;
                            discoveredServers.add(info);
                            String[] list = new String[discoveredServers.size()];
                            for (int i = 0; i < list.length; i++) {
                                ServerInfo ii = discoveredServers.get(i);
                                list[i] = ii.hash;
                            }
                            serverList.setListData(list);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        setVisible(true);
    }


    void onCommandReceived(String command, String[] args) {
        if(command.equals("start")) {//start id mapdata
            MSPGameWindow w=new MSPGameWindow(GProperty.restoreFromJson(args[1]),Integer.parseInt(args[0]),playerName);
            w.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    Client.this.dispose();
                }
            });
            w.setVisible(true);
            setVisible(false);
            w.game.setNetworkModule(new NetworkModule(w.game,server));
            gameStarted=true;
        }
    }

    void doHandShake() {
        server.sendCommand("handshake", playerName);
    }

    @Override
    public void dispose() {
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
            new Client();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
