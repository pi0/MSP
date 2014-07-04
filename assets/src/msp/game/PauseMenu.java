package msp.game;

import framework.GMap;
import framework.GResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PauseMenu extends JPanel{

    MSPGame mspGame ;
    JButton saveButton,loadButton,exitButton ,cancelButton ;
    public PauseMenu(MSPGame mspGame) {
        this.mspGame = mspGame;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        GMap map = mspGame.map ;



        //
        saveButton = new JButton(){
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.drawImage(GResource.getInstance().getImage("ui.tools.button").getImage()
                        ,200,100,getWidth()/2,getHeight()-100,null);

            }
        };





        g.drawImage(GResource.instance.getImage("ui.tool.pausePanel").getImage(),getX()/2
                ,getY()/2,map.getWidth(),map.getHeight(),null);






    }
}
