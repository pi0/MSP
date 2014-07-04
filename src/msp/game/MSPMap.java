package msp.game;

import framework.*;
import framework.EntityFilter;
import msp.game.entities.Human;
import msp.game.entities.Tree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class MSPMap extends GMap {

    MSPGame game;

    public int[][] mapGridArea;

    double startTime;

    public MSPMap(MSPGame game) {
        super(game);
        this.game = game;
//        enableMiniMap=true;
        game.addEntitychangeListener(new GEntityChangeEvent() {
            @Override
            public void onEntityChanged(GEntity e) {
                if (isLoaded)
                    calculateGridAreas();
            }
        });
    }

    @Override
    public void loadMap(GProperty mapData) {
        super.loadMap(mapData);
        mapGridArea = new int[mapMatrixSize.width][mapMatrixSize.height];
        calculateGridAreas();
    }

    void calculateGridAreas() {
        for (int x = 0; x < mapMatrixSize.width; x++)
            for (int y = 0; y < mapMatrixSize.height; y++)
                mapGridArea[x][y] = map[x][y].areaCode;

        for (GEntity e : (List<GEntity>)game.entities.clone()) {

            int areaCode = e.properties.getInt("areaCode");
            if (areaCode == 0)
                continue;

            GRect entityRect=e.getRect();
            GPoint a = convertLocToGrid(entityRect.location);
            GPoint b= convertLocToGrid(entityRect.getBottomRight());
            for(int x=a.x;x<=b.x;x++)
                for(int y=a.y;y<=b.y;y++)
                    mapGridArea[x][y] = areaCode;
        }

    }

    @Override
    protected void drawUI(Graphics2D g) {
        super.drawUI(g);

        g.setColor(Color.red);

        GRect vr = getCameraViewRect();

        for(GEntity e: (List<GEntity>)game.entities) {
            if(e instanceof Human) {
                Human h= (Human) e;
                if(h.getOwner()==game.currentPlayer.getID()) {
                    GPoint l=h.getLocation();
                    g.setColor(new Color(0,0,0,100));
                    g.fillRect(l.x,l.y-10,30,5);
                    float p=(float)h.properties.getInt("health")/100;
                    g.setColor(new Color(1-p,1.0f,0.0f,.5f));
                    g.fillRect(l.x,l.y-10,(int)(30.0f*p),5);
                }
            }
        }

//        g.setColor(Color.blue);
//        if(isAltDown && isCtrlDown) {
//            for (int x = 0; x < gridSize.width; x++)
//                for (int y = 0; y < gridSize.height; y++)
//            //        if (vr.contains(new GPoint(gridSize.width * x, gridSize.height * y)))
//                        g.drawString(mapGridArea[x][y] + "AAAA", gridSize.width * x + gridSize.width / 2, gridSize.height * y + gridSize.height / 2);
//        }

        if(game.isNight) {
            g.setColor(new Color(0, 0, 0, 80));
            g.fillRect(vr.location.x, vr.location.y, vr.size.width, vr.size.height);
        }



    }

    @Override
    public Image getMiniMap() {

        BufferedImage view = GUtils.createCompatibleImage(mapMatrixSize.width*2,mapMatrixSize.height*2);
        Graphics2D g= (Graphics2D) view.getGraphics();

        for (int x = 0; x < mapMatrixSize.width ; x++) {
            for (int y = 0; y < mapMatrixSize.height; y++) {
                Color color;
                GMapBlock block = map[x][y];
                if (block.areaCode == 2)
                    color = Color.blue;
                else
                    color = (new Color(12, 117, 4,100));
                g.setColor(color);
                g.fillRect(x *2,y*2,2,2);
            }
        }

        for (GEntity e : game.entities) {
            GPoint en = e.getGridLocation();

            if ((e instanceof Human))
                g.setColor(Color.black);
            else if ((e instanceof Tree)) {


                g.fillRect(en.x * 2 + 10, en.y * 2 + 20, 2, 2);
            } else if ((e.properties.getStr("type").equals("building")))
                g.setColor(new Color(255, 0, 0, 100));
                g.fillRect(en.x * 2, en.y * 2, 2, 2);


        }

        return view;
    }

    @Override
    protected void onWindowPaint(Graphics2D g, int w, int h) {
        super.onWindowPaint(g, w, h);
        g.setColor(Color.yellow);

        int sx=10;
        int sy=200;
        for(String s:game.chat) {
            g.drawString(s,sx,sy);
            sy+=20;
        }
    }

    public GEntity findNearestEntity(GEntity to , EntityFilter filter) {
        int minDis=-1;
        GEntity min=null;
        GPoint p=to.getRect().getCenter();
        for(GEntity e:(List<GEntity>)game.entities.clone()) {
            if(e==to ||!filter.accepts(e))
                continue;
            int d=p.distanceTo(e.getRect().getCenter());
            if(minDis==-1 || d<minDis) {
                min=e;
                minDis=d;
            }
        }
        return min;
    }


    @Override
    protected boolean isEntityMovable(GEntity entity) {
        return false;
    }

    @Override
    protected boolean isEntitySelectable(GEntity entity) {
        int o=entity.properties.getInt("owner");
        return entity.properties.getBool("isSelectable") && (game.currentPlayer==null ||
                (o==0 || game.currentPlayer.getID()==o));
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        GPoint mouseLoc = getMouseLocationOnMap();

        if (SwingUtilities.isRightMouseButton(e))
            for (GEntity e2 : selectedEntities) {
                MSPEntity ee = (MSPEntity) e2;
                if (ee.getOwner() == game.currentPlayer.getID())
                    ee.properties.put("dst", mouseLoc);
            }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        if(e.getKeyChar()=='c') {
            String chat=JOptionPane.showInputDialog("Input text to chat :");
            game.sendChat(chat,game.currentPlayer.getName());
        }
    }
}
