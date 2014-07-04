package msp.game.network;

import framework.GDB;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPConnector {

    Socket socket = null;
    DataOutputStream out;
    BufferedReader in;
    ArrayList<TCPConnectorListener> listeners = new ArrayList();

    private String host;
    private int port;

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public boolean connect(final Socket socket) {

        if (isConnected())
            return true;

        GDB.i("Connecting to " + socket.getRemoteSocketAddress().toString());

        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());

            //Start a thread to read commands

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isConnected()) {
                        String l = null;
                        try {
                            l = in.readLine();
                        } catch (Exception e) {
                            GDB.e("Connection error : " + e.getMessage());
                            disconnect();
                            reconnect();
                        }

                        if(l==null)
                            continue;

                        try {
                            String[] s = l.split("\t\t");
                            String[] ss = s[1].split("\t");
                            for (TCPConnectorListener la : (ArrayList<TCPConnectorListener>) listeners.clone())
                                la.onCommandReceived(s[0], ss);
                        }catch (Exception e){

                        }

                    }
                }
            }).start();

        } catch (IOException e) {
            return false;
        }
        return isConnected();
    }

    private void disconnect() {
        TCPConnector.this.socket = null;
    }

    public boolean connect(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            return connect(new Socket(host, port));
        } catch (IOException e) {
            return false;
        }
    }

    public void reconnect() {
        if (isConnected())
            return;

        if (host != null)
            connect(host, port);
    }

    private void send(String command) {
        //TODO : use sender thread instead !
        if (!isConnected())
            return;
        try {

            for (char c : command.toCharArray()) {
                out.write(c);
            }
            out.flush();
        } catch (Exception e) {
            GDB.e("Cannot send command !");
        }
    }

    public void sendCommand(String command, String... args) {
        StringBuilder g = new StringBuilder();
        g.append(command);
        g.append("\t\t");
        for (String arg : args) {
            g.append(arg);
            g.append("\t");
        }
        g.append("\r\n");
        send(g.toString());
    }


}
