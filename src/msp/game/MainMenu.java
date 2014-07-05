package msp.game;

import framework.GResource;
import framework.GWindow;
import msp.editor.Editor;
import msp.game.network.Client;
import msp.game.network.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainMenu extends GWindow {

    JPanel c = new JPanel(new GridLayout(5,1,1,50));

    JButton startgame;

    JButton joingame;
    JButton mapEditor;
    JButton loadGame;
    JButton exit;

    MainMenu() {


        enableFullscreenMode();
        setContentPane(new JPanel(new GridBagLayout(),true){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(GResource.instance.getImage("ui.tool.mainMenu").getImage(),0,0,getWidth(),getHeight(),this);
            }
        });

        startgame = new JButton();

        MenuButton start= new  MenuButton("start");
        c.add(start);

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                startGame();
            }


        });

        joingame = new JButton();
        MenuButton join= new  MenuButton("join");
        c.add(join);
        join.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                joinGame();
            }


        });


        mapEditor = new JButton();
        MenuButton map= new  MenuButton("map");
        map.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                map();
            }
        });
        c.add(map);


        loadGame = new JButton();
        MenuButton about= new  MenuButton("about");
        about.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainMenu.this,"MSP By : Pooya parsa - sepehr sabour - mahdi rabiyeganeh");
            }
        });
        c.add(about);

        exit = new JButton();
        MenuButton exit= new  MenuButton("exit");
        exit.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        c.add(exit);


        c.setBackground(new Color(0,0,0,0));
        c.setPreferredSize(new Dimension(222,350));
        getContentPane().add(c);

        setVisible(true);

    }

    private void map() {
        Editor e=new Editor(){
            @Override
            public void dispose() {
                super.dispose();
                setVisible(true);
            }
        };
        setVisible(false);
        e.setVisible(true);
    }

    private void joinGame() {
        setVisible(false);
        try {
            new Client(){
                @Override
                public void dispose() {
                    super.dispose();
                    MainMenu.this.setVisible(true);
                }
            };
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startGame() {
        setVisible(false);
        try {
            new Server() {
                @Override
                public void dispose() {
                    super.dispose();
                    MainMenu.this.setVisible(true);
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        new MainMenu();
    }
}
