package framework;

import com.json.generators.JsonGeneratorFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

public abstract class GMap extends GFrame {


    //GGame
    protected GGame game;

    //Properties
    public GProperty properties;

    //Background
    public GSize mapMatrixSize;
    protected GMapBlock map[][];/**/

    //Layers
    public GSize mapSize;

    public GSize gridSize;
    protected GLayerManager layers;

    //Camera stuff
    public double cameraZoomFactor = .75;

    public GPoint cameraCenter;
    protected double cameraCurrentMoveSpeed = .2;
    protected final static int cameraMoveSpeedMaxStep = 20;
    //Mouse hot areas
    protected int hotAreaBorderLength = 70;//-1 for disable state

    protected int hotAreas = 0;
    protected boolean isMouseOnMap;
    //Mouse dragging
    protected boolean isMapDragging;

    public ArrayList<GEntity> getSelectedEntities() {
        return selectedEntities;
    }

    //Select
    protected ArrayList<GEntity> selectedEntities = new ArrayList();
    protected GPoint selectRectStart = null;
    protected GRect selectRect = null;
    boolean isMultiSelectEnabled = true;

    //miniMap
    public Image miniMap;
    protected GSize miniMapSize=new GSize(200,150);

    //Reserved layers
    public final static int LAYER_BG = 0;
    private static final int LAYER_UI_BACKGROUND = 1;
    public final static int LAYER_MAX_RESERVED = 4;
    public final static int LAYER_ENTITY_START = LAYER_MAX_RESERVED + 1;
    public final static int LAYER_UI = 500;

    protected boolean isLoaded = false;

    public GMap(GGame game) {
        super();

        this.game = game;
        game.map = this;

        //Start a background thread
        new Thread(new Runnable() {
            public void run() {
                while (true) {

                    if (isLoaded)
                        try {
                            onCycle();
                        } catch (Exception e) {
                        }

                    GUtils.sleep(50);
                }
            }
        }).start();
    }


    public void loadMap(GProperty mapData) {

        isLoaded = false;

        GDB.i("Initializing map ...");

        //init properties
        properties = mapData;

        //init Layers with current map size IN PIXELS
        int size = mapData.getInt("size");
        mapMatrixSize = new GSize(size, size);
        int gridSize = mapData.getInt("gridSize");
        this.gridSize = new GSize(gridSize, gridSize);

        mapSize = new GSize(mapMatrixSize.width * this.gridSize.width,
                mapMatrixSize.height * this.gridSize.height);

        layers = new GLayerManager(mapSize);

        //Move camera to center of map
        this.cameraCenter = new GPoint(mapSize.width / 2, mapSize.height / 2);

        //init and load map data
        String map = properties.getStr("map");
        String defaultMap = properties.getStr("defaultMap");
        if (map == null)
            map = "";
        String[] blockData = map.split(";");

        int c = 0;
        this.map = new GMapBlock[mapMatrixSize.width][mapMatrixSize.height];
        for (int y = 0; y < mapMatrixSize.height; y++)
            for (int x = 0; x < mapMatrixSize.width; x++) {
                if (blockData.length > 1 && c < blockData.length)
                    this.map[x][y] = new GMapBlock(blockData[c++], x, y);
                else
                    this.map[x][y] = new GMapBlock(defaultMap, x, y);
            }
        drawBackground(true);
        drawEntities(true);

        GDB.i("Map initialized !");
        isLoaded = true;


    }

    //=======================================================================================
    //  Drawing
    //=======================================================================================

    public void drawBackground(boolean forceUpdate) {

        Graphics2D g = layers.getGraphics(LAYER_BG, forceUpdate);
        GRect cam = getCameraViewRect();

        for (int y = 0; y < mapMatrixSize.height; y++)
            for (int x = 0; x < mapMatrixSize.width; x++) {
                GMapBlock block = map[x][y];

                GPoint blockPos = new GPoint(x * gridSize.width, y * gridSize.height);
                if (!forceUpdate && !cam.contains(blockPos))
                    continue;
                GImage gImg = block.getImage(this);
                if (gImg == null)
                    continue;
                Image image;
                if (gImg.isAnimated())
                    image = gImg.getAnimImage(block.animSeed);
                else
                    image = gImg.getImage(block.imageFrame);

                if (forceUpdate || gImg.isAnimated())
                    g.drawImage(image, blockPos.x, blockPos.y, gridSize.width, gridSize.height, null);
            }
    }

    public void drawEntities(boolean forceDraw) {

        //Find updates--------------------------------------------------

        Map<Integer, ArrayList<GEntity>> updates = new TreeMap();

        GRect cam = getCameraViewRect();

        for (GEntity e : (List<GEntity>) game.entities.clone()) {

            if (!cam.contains(e.getRect().getCenter()))
                continue;

            if (e.offerUpdate() || e.isAnimated() || forceDraw) {
                int layer = LAYER_ENTITY_START + e.properties.getInt("layer");
                ArrayList<GEntity> layerUpdates = updates.get(layer);
                if (layerUpdates == null)
                    updates.put(layer, (layerUpdates = new ArrayList()));
                layerUpdates.add(e);
                recursiveAddEntityDrawUpdates(e, layerUpdates);
            }
        }

        //Do updates--------------------------------------------------
        final boolean temp = forceDraw;
        updates.forEach(new BiConsumer<Integer, ArrayList<GEntity>>() {
            @Override
            public void accept(Integer layer, ArrayList<GEntity> gEntities) {

                Graphics2D g = layers.getGraphics(layer, temp);

                //Sort list
                Collections.sort(gEntities);

                //First clear all update regions in layer
                for (GEntity e : gEntities)
                    GUtils.transClearGraphics(g, e.getUpdateRect());

                //Then draw new images
                for (GEntity e : gEntities)
                    // if(selectedEntities==null || !selectedEntities.contains(e))
                    e.draw(g);
//
//                if(selectedEntities!=null)
//                    for (GEntity e : selectedEntities)
//                        e.draw(g);

            }
        });

    }

    public void updateMiniMap() {

        miniMap = getMiniMap();

        if(miniMap==null)
            return;

        Graphics2D viewG = (Graphics2D) miniMap.getGraphics();
        viewG.setColor(Color.white);
        viewG.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
        GRect r = getCameraViewRect();
        viewG.fillRect( (r.location.x*miniMap.getWidth(this))/mapSize.width , (r.location.y*miniMap.getHeight(this))/mapSize.height,
                (r.size.width*miniMap.getWidth(this))/mapSize.width , (r.size.height*miniMap.getHeight(this))/mapSize.height);

    }

    protected Image getMiniMap() {
          return null;//TODO
    }

    public void drawUI() {
        drawUI(layers.getGraphics(LAYER_UI, true));
        drawBackgroundUI(layers.getGraphics(LAYER_UI_BACKGROUND, true));
    }

    protected void drawUI(Graphics2D g) {

        //Draw selection rect
        if (isMultiSelectEnabled && selectRect != null) {
            g.setColor(new Color(0, 128, 255, 30));
            g.fillRect(selectRect.location.x, selectRect.location.y,
                    selectRect.size.width, selectRect.size.height);
        }

    }

    protected void drawBackgroundUI(Graphics2D g) {

        //highlight selected entities
        for (GEntity e : selectedEntities) {
            GRect r = e.getRect();
            g.setColor(new Color(255, 255, 255, 40));
            int hh = (int) ((1.0 * r.size.height) * .7);
            int ww = 0;//(int) ((1.0 * r.size.width) * .2);
            g.fillOval(r.location.x + ww, r.location.y + hh, r.size.width - ww, r.size.height - hh);
        }

    }

    protected void onWindowPaint(Graphics2D g, int w, int h) {

    }

    long lastDrawMS;

    @Override
    public void paintComponent(Graphics gg) {

        super.paintComponent(gg);

        //Get and tweak graphics

        Graphics2D g = (Graphics2D) gg;
        GUtils.tweakGraphics(g);

        //Fill map with gray

        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        //Fully draw current viewport

        g.drawImage(layers.getView(getCameraViewRect()),
                0, 0, getWidth(), getHeight(), this);

        //Draw debug info

        int dx = 10;
        int dh = 15, dhl = 0;
        g.setColor(Color.WHITE);
        //     g.drawString("Mouse location : " + getMouseLocationOnMap(), dx, dh * (++dhl));
        g.drawString("Zoom :" + ((int) (cameraZoomFactor * 100.0)), dx, dh * (++dhl));
        long currentMS = System.currentTimeMillis();
        g.drawString("FPS :" + 1000 / (currentMS - lastDrawMS + 1), dx, dh * (++dhl));
        lastDrawMS = currentMS;

        //g.drawString("Ram :" + GUtils.byteToHumanreadable(GUtils.getRamUsage()), dx, dh * (++dhl));


        onWindowPaint(g, getWidth(), getHeight());

    }


    //=======================================================================================
    //  Helper functions
    //=======================================================================================

    public GPoint convertLocToGrid(GPoint loc) {
        if (loc == null)
            return null;
        return new GPoint((int) (loc.x / gridSize.width + 0.5), (int) (loc.y / gridSize.height + 0.5));
    }

    public GPoint convertGridToLoc(GPoint grid) {
        if (grid == null)
            return null;
        return new GPoint((int) (grid.x * gridSize.width), (int) (grid.y * gridSize.height));
    }

    protected abstract boolean isEntitySelectable(GEntity entity);

    protected abstract boolean isEntityMovable(GEntity entity);

    void recursiveAddEntityDrawUpdates(GEntity e, ArrayList<GEntity> gEntities) {
        for (GEntity e2 : (List<GEntity>) game.entities.clone()) {
            if (e2 == e || gEntities.contains(e2))
                continue;
            if (e2.intersectsWithOnLayout(e)) {
                gEntities.add(e2);
                recursiveAddEntityDrawUpdates(e2, gEntities);
            }
        }
    }

    void commitMapChanges() {
        StringBuilder mapStr = new StringBuilder();
        for (int y = 0; y < mapMatrixSize.height; y++)
            for (int x = 0; x < mapMatrixSize.width; x++)
                mapStr.append(map[x][y].toString() + ";");
        properties.put("map", mapStr.toString());
    }

    void commitEntityChanges() {
        ArrayList<Map> entityProps = new ArrayList();
        for (GEntity entity : (List<GEntity>) game.entities.clone())
            entityProps.add(new HashMap(entity.properties));
        properties.put("entities", entityProps);
    }

    public String toJson() {
        commitMapChanges();
        commitEntityChanges();
        return JsonGeneratorFactory.getInstance()
                .newJsonGenerator()
                .generateJson(properties);
    }

    protected void moveMap(int directionFlags) {

        if (cameraCurrentMoveSpeed < 1)
            cameraCurrentMoveSpeed += .1;

        int step = (int) (cameraMoveSpeedMaxStep * cameraCurrentMoveSpeed);

        if (GUtils.hasFlag(directionFlags, GConstants.DIRECTION_LEFT))
            cameraCenter.x -= step;
        else if (GUtils.hasFlag(directionFlags, GConstants.DIRECTION_RIGHT))
            cameraCenter.x += step;

        if (GUtils.hasFlag(directionFlags, GConstants.DIRECTION_DOWN))
            cameraCenter.y += step;
        else if (GUtils.hasFlag(directionFlags, GConstants.DIRECTION_UP))
            cameraCenter.y -= step;
    }

    protected GRect getCameraViewRect() {

        GSize size = new GSize((int) (getWidth() * cameraZoomFactor), (int) (getHeight() * cameraZoomFactor));

        if (cameraCenter.x - size.width / 2 < 1)
            cameraCenter.x = size.width / 2;
        else if (cameraCenter.x + size.width / 2 >= mapSize.width - 1)
            cameraCenter.x = mapSize.width - size.width / 2 - 1;

        if (cameraCenter.y - size.height / 2 < 1)
            cameraCenter.y = size.height / 2;
        else if (cameraCenter.y + size.height / 2 >= mapSize.height - 1)
            cameraCenter.y = mapSize.height - size.height / 2 - 1;


        GPoint start = new GPoint(cameraCenter.x - size.width / 2, cameraCenter.y - size.height / 2);
        return new GRect(start, size);

    }

    public GPoint framePointToMap(GPoint p) {
        GRect cam = getCameraViewRect();
        int xReal = (int) (cam.location.x + (p.x * cameraZoomFactor));
        int yReal = (int) (cam.location.y + (p.y * cameraZoomFactor));
        return new GPoint(xReal, yReal);
    }

    public GPoint getMouseLocationOnMap() {
        return framePointToMap(GUtils.getMouseLocation(this));
    }

    protected void checkHotAreas() {
        checkHotAreas(GUtils.getMouseLocation(this));
    }

    protected void checkHotAreas(GPoint location) {
        if (hotAreaBorderLength < 0)
            return;//Hot areas are disabled

        hotAreas = 0;

        if (!isMouseOnMap || isMapDragging)
            return;

        if (location.x < hotAreaBorderLength)
            hotAreas |= GConstants.DIRECTION_LEFT;
        if (location.y < hotAreaBorderLength)
            hotAreas |= GConstants.DIRECTION_UP;
        if (location.x > getWidth() - hotAreaBorderLength)
            hotAreas |= GConstants.DIRECTION_RIGHT;
        if (location.y > getHeight() - hotAreaBorderLength)
            hotAreas |= GConstants.DIRECTION_DOWN;
    }

    //=======================================================================================
    //  Events
    //=======================================================================================

    int cycleCount;
    boolean bigCycle = true;

    protected void onCycle() {

        cycleCount++;
        if (cycleCount > properties.getInt("bigCycle")) {
            cycleCount = 0;
            bigCycle = true;
        } else
            bigCycle = false;

        //HotAreas
        checkHotAreas();
        if (hotAreas != 0)
            moveMap(hotAreas);
        else
            cameraCurrentMoveSpeed = .5;

        drawBackground(false);
        drawEntities(bigCycle);

        drawUI();
        if (bigCycle)
            updateMiniMap();


        //Request focus !
        requestFocus();
        //requestFocusInWindow();

        //Always update !
        render();
    }


    @Override
    protected void onMouseDragged(int dx, int dy, MouseEvent e) {
        super.onMouseDragged(dx, dy, e);

        if (SwingUtilities.isMiddleMouseButton(e)) {

            cameraCenter.x -= dx;
            cameraCenter.y -= dy;
            isMapDragging = true;
            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        } else if (SwingUtilities.isLeftMouseButton(e)) {

            GPoint mouseLocation = getMouseLocationOnMap();

            //Select or move ?

            boolean select = true;
            for (GEntity entity : selectedEntities)
                if (entity.getRect().contains(mouseLocation)) {
                    select = false;
                    break;
                }

            if (select) {
                if (!isMultiSelectEnabled && !isCtrlDown && !isShiftDown)
                    selectedEntities.clear();
                selectRect = new GRect(selectRectStart, getMouseLocationOnMap());
                for (GEntity entity : (List<GEntity>) game.entities.clone())
                    if (isEntitySelectable(entity))
                        if (selectRect.contains(entity.getRect().getCenter())) {
                            selectedEntities.add(entity);
                        }


            } else {
                //move
                if (selectedEntities.size() != 1) {
                    selectedEntities.clear();
                } else {
                    GEntity selected = selectedEntities.get(0);
                    if (isEntityMovable(selected))
                        selected.setLocation(mouseLocation.centerWith(selected.getSize()));
                }
            }
        }
    }


    @Override
    public void mousePressed(MouseEvent e) {

        super.mousePressed(e);

        //Select
        selectRectStart = getMouseLocationOnMap();

        if (!isCtrlDown && e.getButton() == 1)
            selectedEntities.clear();

        boolean isMouseOnEntry = false;

        GPoint p = getMouseLocationOnMap();

        for (GEntity entity : (List<GEntity>) game.entities.clone())
            if (entity.getRect().contains(p)) {
                isMouseOnEntry = true;
                if (isEntitySelectable(entity)) {
                    selectedEntities.add(entity);
                    if (entity.properties.getBool("singleSelect")) {
                        selectedEntities.clear();
                        selectedEntities.add(entity);
                        break;
                    }
                }
            }


        //Check for map grid selection

        if (!isMouseOnEntry) {
            GPoint m = convertLocToGrid(selectRectStart);
            onBackgroundClicked(m.x, m.y, e.getButton());
        }

    }

    protected void onBackgroundClicked(int gx, int gy, int mouseButton) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        isMouseOnMap = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        isMapDragging = false;
        setCursor(Cursor.getDefaultCursor());
        selectRect = null;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

        if (e.getWheelRotation() < 0)
            cameraZoomFactor -= .1;
        else
            cameraZoomFactor += .1;
        if (cameraZoomFactor <= .2)
            cameraZoomFactor = .2;

        if (cameraZoomFactor > .8)
            cameraZoomFactor = .8;

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        isMouseOnMap = false;
    }


    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        int camMoves = 0;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                camMoves |= GConstants.DIRECTION_LEFT;
                break;
            case KeyEvent.VK_RIGHT:
                camMoves |= GConstants.DIRECTION_RIGHT;
                break;
            case KeyEvent.VK_UP:
                camMoves |= GConstants.DIRECTION_UP;
                break;
            case KeyEvent.VK_DOWN:
                camMoves |= GConstants.DIRECTION_DOWN;
                break;
            case KeyEvent.VK_ESCAPE:
                selectedEntities.clear();
                break;
        }
        if (camMoves != 0)
            moveMap(camMoves);
    }


}
