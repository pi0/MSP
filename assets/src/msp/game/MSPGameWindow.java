package msp.game;

import framework.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MSPGameWindow extends GWindow {

    //Map
    public MSPMap map;
    public MSPGame game;

    Container c;

    ManagePanel managePanel;
    HeaderPanel headerPanel;
    String titleTag;

    public MSPGameWindow(String titleTag,String mapName) {
        this.titleTag=titleTag;

        game = new MSPGame();
        game.map = map = new MSPMap(game);
        game.load(GResource.instance.getMap(mapName));

        game.addEventHandler(new GameEventHandler() {
            @Override
            public void onGameEvent(GameEvent e) {
                switch (e) {
                    case GAME_EVENT_GAMEOVER:
                        close();
                        break;
                }
            }
        });

        setup();
        setupComponents();
    }

    public MSPGameWindow(GProperty mapData,int playerID,String playerName) {

        game = new MSPGame();
        game.map = map = new MSPMap(game);
        game.load(mapData);
        game.addPlayer(playerID,playerName,"");
        titleTag="Client "+playerID;

        setup();
        setupComponents();
    }


    private void setup() {
        setTitle("MSP Strategy - "+titleTag);
        c = getContentPane();

        enableFullscreenMode();
      //  setSize(640,480);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private void setupComponents() {

        c.setLayout(new BorderLayout());

        //GamePanel
        managePanel = new ManagePanel(game);

        //header
        headerPanel = new HeaderPanel(game);

        //map settings

        c.add(map, BorderLayout.CENTER);

        //manage panel setting

        managePanel.setBackground(Color.black);
        c.add(managePanel, BorderLayout.SOUTH);
        c.add(headerPanel,BorderLayout.NORTH);

        //Music!

        new GMP3("assets/music.mp3").play();

    }

    public static void main(String[] args) {
        new MSPGameWindow("TEST","default").setVisible(true);
    }


}
