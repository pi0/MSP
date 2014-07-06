package msp.game.entities;

import framework.*;
import msp.game.MSPEntity;
import msp.game.MSPMap;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public abstract class Human extends MSPEntity {

    GPoint dst = null;
    GPoint[][] path;
    public boolean attack ;
    int lastHelath = properties.getInt("health");
    
    public Human(GProperty properties, int id, GGame game) {
        super(properties, id, game);
        enableWorkingThread();
        updateHuman();
    }

    @Override
    public List<String> getInfo() {
        ArrayList<String> a = (ArrayList<String>) super.getInfo();
        a.add("Power : " + properties.getStr("power"));
        a.add("Health : " + properties.getInt("health"));
        a.add("Food : " + properties.getInt("food"));
        return a;
    }

    public void updateHuman() {
        properties.put("image", properties.getStr("type") + "." +
                properties.getStr("name") + "." + properties.getStr("mode")
                + "." + properties.getInt("moveDir"));
    }


    //private final static int[][] d = {{0,+1},{+1,+1},{+1,0},{+1,-1},{0,-1},{-1,-1},{-1,0},{-1,+1}};
    private final static int[][] d = {{0,+1},{+1,0},{0,-1},{-1,0},{+1,+1},{-1,-1},{-1,+1},{+1,-1}};

    public void findPath(GPoint dst, boolean otherHumans) {
        try{
            findPathUnsafe(dst,otherHumans);
        }catch (Exception e){

        }
    }

    private void findPathUnsafe(GPoint dst, boolean otherHumans) {
        this.dst = dst;
        if (dst == null)
            return;
        GSize size = game.map.mapMatrixSize;
        path = new GPoint[size.width][size.height];


        int[][] areaCodes = ((MSPMap) game.map).mapGridArea;
        GPoint currLoc = getGridLocation();
        Queue<GPoint> queue = new LinkedList();
        queue.add(dst);
        path[dst.x][dst.y] = dst;
//        boolean uStarted = false;

        while (!queue.isEmpty()) {
            GPoint c = queue.remove();
            if (c.equals(currLoc))
                break;
            for (int i = 0; i < d.length; i++) {
                GPoint np = new GPoint(c.x + d[i][0], c.y + d[i][1]);
                if (np.x < 0 || np.y < 0 || np.x >= size.width || np.y >= size.height)
                    continue;
                if (path[np.x][np.y] != null)
                    continue;

                boolean r = isAreaReachable(areaCodes[np.x][np.y]);

                if(!r && otherHumans) {
                    GRect rect = new GRect(currLoc, getSize());
                    for (GEntity e : (List<GEntity>)game.entities.clone()) {
                        if (!(e instanceof Human) || e == this)
                            continue;
                        if (rect.intersectsWith(e.getRect())) {
                            r=false;
                            break;
                        }
                    }
                }

//                if (!uStarted && r)
//                    uStarted = true;
//                if (uStarted && !r)
//                    continue;

                  if(!r)
                      continue;

                path[np.x][np.y] = c;
                queue.add(np);
            }
        }
    }


    public boolean isAreaReachable(int code) {
        boolean a = properties.getStr("reachableAreas")
                .contains(new Integer(code).toString());
        return a;
    }

    public boolean isAreaReachable(GPoint location) {
        GPoint grid=game.map.convertLocToGrid(location);
        return isAreaReachable(((MSPMap) game.map).mapGridArea[grid.x][grid.y]);
    }

    @Override
    protected void onThreadCycle(int delayMs) {
        super.onThreadCycle(delayMs);
        
        if(properties.getInt("health")<lastHelath)
        	attack=true ;
        else
        	attack = false ;
        
        lastHelath = properties.getInt("health");
        	
        
        if (properties.getInt("health") <= 0)
            game.removeEntity(this);

        boolean isIdle = true;
        GPoint dst=null;
        try {
            dst = game.map.convertLocToGrid(properties.getPoint("dst"));
        }catch (Exception e){
            e.printStackTrace();
        }
        if (dst != null) {
            if (!dst.equals(this.dst))
                findPath(dst, false);
            isIdle = !nextMove();
        }
        if (isIdle) {
            properties.put("mode", "walking");
            properties.put("anim", false);

        } else {
            properties.put("mode", "walking");
            properties.put("anim", true);
        }

        updateHuman();
    }


    GPoint lastLoc = null, lastLoc2 = null, lastLoc3 = null;

    protected boolean nextMove() {
        GPoint currLoc = getLocation();
        GPoint currGLoc = getGridLocation();
        GPoint nextGrid = path[currGLoc.x][currGLoc.y];

        if (nextGrid == null || currGLoc.equals(dst)) {
            //It seems we have reached !
            properties.remove("dst");
            return false;
        }
        int dX = nextGrid.x - currGLoc.x;
        int dY = nextGrid.y - currGLoc.y;
        if (dX > 0)
            dX = +1;
        else if (dX < 0)
            dX = -1;
        if (dY > 0)
            dY = +1;
        else if (dY < 0)
            dY = -1;
        if (currLoc.equals(lastLoc3))
            findPath(dst, false);
        lastLoc3 = lastLoc2;
        lastLoc2 = lastLoc;
        lastLoc = currLoc;

        int step = properties.getInt("step");

        if(dX!=0 && dY!=0) {
            if(GUtils.random.nextBoolean())
                currLoc.x += dX * step;
            else
                currLoc.y += dY * step;
        }else {
            currLoc.x += dX * step;
            currLoc.y += dY * step;
        }
        //check for other humans!
        GRect r = new GRect(currLoc, getSize());
        GPoint c=r.getCenter();
        for (GEntity e : (ArrayList<GEntity>)game.entities.clone()) {
            if (!(e instanceof Human) || e == this)
                continue;
            if (c.distanceTo(e.getRect().getCenter())<20) {
                boolean isBetter = getID() > e.getID() &&
                        properties.getStr("name").equals(e.properties.getStr("name"));
                boolean isMoving = e.properties.isDefined("dst");
                if (isBetter) {
                    if(!isMoving) {
                        findPath(dst,true);
                        //properties.remove("dst");
                    }
                    return false;
                }
            }
        }

        if(!isAreaReachable(currLoc)) {
            findPath(properties.getPoint("dst"),false);
            return false;
        }

        properties.put("moveDir", GUtils.getDir(getLocation(), currLoc));
        setLocation(currLoc);
        return true;
    }
    
}
