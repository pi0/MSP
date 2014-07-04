package msp.game.network;

public interface TCPConnectorListener {
    void onCommandReceived(String command, String[] args);
}
