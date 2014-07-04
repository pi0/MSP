package msp.game;

import framework.GResource;
import framework.GUtils;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel{

    MSPGame game;

    public HeaderPanel(MSPGame game) {
        setLayout(null);
        setPreferredSize(new Dimension(0,26));
        this.game=game;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    repaint();
                    GUtils.sleep(500);
                }
            }
        }).start();

    }

    @Override
    public void paint(Graphics g) {
        //super.paintComponent(g);
        g.drawImage(GResource.instance.getImage("ui.tools.header").getImage(),0,0,getWidth(),getHeight(),null);

        g.drawImage(GResource.instance.getImage("ui.tools.header.foodwood").getImage(),0,0,null);

        g.setColor(Color.black);
        if(game.currentPlayer!=null) {
            g.drawString(game.currentPlayer.getWood() + "", 30, 17);
            g.drawString(game.currentPlayer.getFood() + "", 110, 17);
        }else {
            g.drawString("No player!", 30, 17);
        }

        g.setColor(Color.green);
        float time=game.getTime();
        g.fillRect(189,8, (int) (44*time),10);

    }
}
