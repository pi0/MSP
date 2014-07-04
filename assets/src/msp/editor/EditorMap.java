package msp.editor;

import framework.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class EditorMap extends GMap {

    GEntity newEntity;
    GMapBlock currentBG = null;

    Editor editor;

    final static int miniMapMargin = 10;

    public EditorMap(GGame game, Editor editor) {
        super(game);
        this.editor = editor;
    }


    //=======================================================================================
    //  Drawing
    //=======================================================================================

    @Override
    protected void drawUI(Graphics2D g) {
        super.drawUI(g);
        if (newEntity != null)
            newEntity.draw(g);
    }

    @Override
    protected void drawBackgroundUI(Graphics2D g) {
        super.drawBackgroundUI(g);
        if(currentBG!=null) {
            GPoint l=convertLocToGrid(getMouseLocationOnMap());
            GUtils.drawImage(g,currentBG.getImage(this).getImage(),l.x*gridSize.width,
                    l.y*gridSize.height,gridSize.width,gridSize.height,.5f);
            g.setColor(new Color(200,0,200,100));
            g.drawRect(l.x*gridSize.width,
                    l.y*gridSize.height,gridSize.width,gridSize.height);
        }
    }

    @Override
    protected Image getMiniMap() {
        return layers.getView(new GRect(0,0,mapSize.width,mapSize.height));
    }

    //=======================================================================================
    //  Helpers
    //=======================================================================================

    public void setEntity(GEntity entity) {
        this.newEntity = entity;
        this.currentBG=null;
        entity.properties.put("alpha", 0.6f);
    }

    //=======================================================================================
    //  Events
    //=======================================================================================

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);
        if (newEntity != null)
            newEntity.setLocation(getMouseLocationOnMap().centerWith(newEntity.getSize()));
    }

    @Override
    public void mouseReleased(MouseEvent e) {

        //Fill background
        boolean changed = false;
        GRect blockRect = new GRect(0, 0, gridSize.width, gridSize.height);
        if (selectRect != null /*&& selectedEntities.size() == 0*/ && newEntity == null) {
            if (selectRect != null && currentBG != null) {

                GPoint tl=convertLocToGrid(selectRect.getTopLeft());
                GPoint tr=convertLocToGrid(selectRect.getTopRight());
                GPoint bl=convertLocToGrid(selectRect.getBottomLeft());
                GPoint br=convertLocToGrid(selectRect.getBottomRight());

                for (int x = 0; x < mapMatrixSize.width; x++)
                    for (int y = 0; y < mapMatrixSize.height; y++) {
                        GMapBlock b = map[x][y];
                        blockRect.location.x = x * gridSize.width;
                        blockRect.location.y = y * gridSize.height;
                        if (selectRect.intersectsWith(blockRect)) {



                            if(!isShiftDown) {
                                b.setImage(currentBG.getImageName() + "");//clone!
                                b.imageFrame = isCtrlDown ? GUtils.random.nextInt(
                                        b.getImage(this).getFrameCount()) : currentBG.imageFrame;
                            }else {
                                //Auto border

                                if(currentBG.getImageName().equals(b.getImageName()))
                                    continue;

                                b.setImage(currentBG.getImageName() + "");//clone!

                                int borderNum=0;



                                if(y==tl.y) {
                                    if(x==tl.x)
                                        borderNum=8;
                                    else if(x==tr.x)
                                        borderNum=2;
                                    else borderNum=1;
                                }else if(y==bl.y) {
                                    if(x==bl.x)
                                        borderNum=6;
                                    else if(x==br.x)
                                        borderNum=4;
                                    else
                                        borderNum=5;
                                }else if(x==tl.x)
                                    borderNum=7;
                                else if(x==tr.x)
                                    borderNum=3;

                                if(borderNum!=0) {
                                    java.util.List<String> borders=GResource.instance.getProperty("properties").getProperty("backgroundBorders").getArray(b.getImageName());
                                    if(borders!=null)
                                        b.setImage(borders.get(borderNum-1)+"");
                                }

                            }
                            changed = true;
                        }
                    }
            }
            if (changed)
                drawBackground(true);
        }

        super.mouseReleased(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        if (SwingUtilities.isRightMouseButton(e)) {
            if (newEntity != null)
                newEntity.nextFrame(1);
            else {

            }
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            if (newEntity != null) {
                GEntity myEntity = newEntity.clone(game.getEntityID());
                myEntity.properties.remove("alpha");
                game.addEntity(myEntity);
            } else {

                //MiniMap move
                Point p = getMousePosition().getLocation();
                int x=p.x-getWidth()+miniMapSize.width;
                int y=p.y-getHeight()+miniMapSize.height;
                if(x>0 && y>0 && x<miniMapSize.width && y<miniMapSize.height) {
                    cameraCenter = new GPoint((x*mapSize.width)/miniMapSize.width,
                            (y*mapSize.height)/miniMapSize.height);
                }




            }
        }
    }


    @Override
    protected void onWindowPaint(Graphics2D g, int w, int h) {
        super.onWindowPaint(g, w, h);


        GUtils.drawImage(g, miniMap, getWidth() - miniMapSize.width - miniMapMargin, getHeight() - miniMapSize.height - miniMapMargin
                , miniMapSize.width, miniMapSize.height, .6f);

    }

    @Override
    protected void onBackgroundClicked(int gx, int gy, int mouseButton) {
        super.onBackgroundClicked(gx, gy, mouseButton);

        if (currentBG != null) {
            if (mouseButton == 3)//right click
            {
                currentBG.imageFrame++;
                map[gx][gy].imageFrame++;
            }
           else if (mouseButton ==1)  //left
                map[gx][gy] = currentBG.clone(gx, gy);
            drawBackground(true);
        }

    }


    @Override
    protected boolean isEntityMovable(GEntity entity) {
        return currentBG==null;
    }

    @Override
    protected boolean isEntitySelectable(GEntity entity) {
        return currentBG==null;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                newEntity = null;
                currentBG = null;
                break;
            case KeyEvent.VK_DELETE:

                //And remove them
                for (GEntity entity : selectedEntities)
                    game.removeEntity(entity);
                selectedEntities.clear();
                break;

        }
    }

}
