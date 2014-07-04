package msp.game;

import framework.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ManagePanel extends JPanel {

    MSPGame game;
    GResource resources = GResource.getInstance();

    JPanel infoPanel;
    JPanel selectPanel;
    JPanel miniMap;

    MSPEntity selectedEntity = null;

    final static int miniMapMargin = 30;


    public ManagePanel(final MSPGame game) {

        this.game = game;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(100, 174));

        game.addEntitychangeListener(new GEntityChangeEvent() {
            @Override
            public void onEntityChanged(GEntity e) {
                if (selectedEntity == e)
                    selectedEntity = null;
                updateSelectPanel();
            }
        });

        //Info panel

        infoPanel = new JPanel(null, true) {
            protected void paintComponent(Graphics g) {
                drawInfoPanel((Graphics2D) g);
            }
        };
        infoPanel.setBackground(Color.green);
        add(infoPanel, BorderLayout.CENTER);

        //Select panel

        selectPanel = new JPanel(null, true) {
            protected void paintComponent(Graphics g) {
                g.drawImage(resources.getImage("ui.tool.items").getImage(),
                        0, 0, selectPanel.getWidth(), selectPanel.getHeight(), null);
            }
        };
        selectPanel.setLayout(null);
        selectPanel.setPreferredSize(new Dimension(277, 0));
        add(selectPanel, BorderLayout.WEST);

        //mini map

        miniMap = new JPanel(null, true) {
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(resources.getImage("ui.tool.minimap").getImage(),
                        0, 0, miniMap.getWidth(), miniMap.getHeight(), null);

                g.drawImage(game.map.miniMap, miniMapMargin, miniMapMargin,218,118,miniMap);
            }
        };
        miniMap.setBackground(Color.black);
        miniMap.setPreferredSize(new Dimension(277, 0));
        add(miniMap, BorderLayout.EAST);

        miniMap.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                //MiniMap move

                Point p = e.getPoint();
                int x=p.x-miniMapMargin;
                int y=p.y-miniMapMargin;
                if(x>0 && y>0 && x<miniMap.getWidth()-miniMapMargin && y<miniMap.getHeight()-miniMapMargin) {
                    game.map.cameraCenter = new GPoint((x*game.map.mapSize.width)/(miniMap.getWidth()-2*miniMapMargin),
                            (y*game.map.mapSize.height)/(miniMap.getHeight()-2*miniMapMargin));
                }

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    repaint();
                    GUtils.sleep(100);
                }
            }
        }).start();

    }

    void updateSelectPanel() {

        selectPanel.removeAll();

        if (selectedEntity == null)
            return;

        List<String> buttons = selectedEntity.getButtons();
        if (buttons == null)
            return;

        int btnSize = 50;
        int btnY = 30;
        int btnX = 35;
        for (String b : buttons) {

            final String[] s = b.split(";");
            GImage i = resources.getImage(s[0]);
            if (i == null) {
                GDB.e(s[0]);
                continue;
            }
            JButton btn = new JButton(i.getImageIcon(Integer.parseInt(s[1])));

            btn.setToolTipText(s[2]);

            btn.setSize(btnSize, btnSize);
            if (btnX + btnSize >= selectPanel.getWidth() - 35) {
                btnY += btnSize;
                btnX = 35;
            }

            btn.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (selectedEntity != null)
                        selectedEntity.onAction(s[3]);
                }
            });

            btn.setLocation(btnX, btnY);
            btnX += btnSize;
            selectPanel.add(btn);
        }


    }

    private void drawInfoPanel(Graphics2D g) {

        g.drawImage(resources.getImage("ui.tool.infopanel").getImage(), 0, 0,
                infoPanel.getWidth(), infoPanel.getHeight(), null);

        GUtils.tweakGraphics(g);

        ArrayList<GEntity> selectedEntities = game.map.getSelectedEntities();

        if (selectedEntities.size() == 0 || !game.entities.contains(game.entities.get(0))) {
            selectedEntity = null;
            updateSelectPanel();
            return;
        }


        if (selectedEntities.size() >= 1) {

            if (selectedEntities.get(0) != selectedEntity) {
                selectedEntity = (MSPEntity) selectedEntities.get(0);
                updateSelectPanel();
            }

            //Selected entity image
            GImage i = selectedEntities.get(0).getImage();
            if (i != null)
                g.drawImage(i.getImage(), 25, 50, 100, 100, null);

            //Selected entity Info
            int sx = 150, sy = 50;
            MSPEntity entity = (MSPEntity) selectedEntities.get(0);

            g.setColor(Color.BLACK);

            g.setFont(g.getFont().deriveFont(Font.BOLD));

            g.setFont(g.getFont().deriveFont(30f));
            g.drawString(entity.properties.getStr("name"), sx, sy);
            sy += 30;

            g.setFont(g.getFont().deriveFont(15f));

            for (String s : entity.getInfo()) {
                g.drawString(s, sx, sy);
                sy += 20;
            }


        }


    }

}

