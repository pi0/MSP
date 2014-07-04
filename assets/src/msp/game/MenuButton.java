package msp.game;

import framework.GResource;

import javax.swing.*;
import java.awt.*;

public class MenuButton extends JButton{

    public MenuButton(String name) {
        setIcon(GResource.instance.getImage("ui.menu."+name).getImageIcon());
        setPressedIcon(GResource.instance.getImage("ui.menu."+name+".c").getImageIcon());

        setBorderPainted(false);
        setFocusPainted(false);

        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D gg= (Graphics2D) g;
        gg.setBackground(new Color(0,0,0,0));
        gg.clearRect(0,0,getWidth(),getHeight());
        super.paintComponent(g);
    }
}
