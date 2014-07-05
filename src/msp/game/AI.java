package msp.game;


import framework.GEntity;
import framework.GUtils;
import msp.game.entities.Human;
import msp.game.entities.King;
import msp.game.entities.Worker;

import java.util.ArrayList;
import java.util.List;

public class AI {

    Player player;
    MSPGame game;
    boolean working = true;

    public AI(int playerID, MSPGame game) {
        this.game = game;
        player = game.createPlayer(playerID, "CPU " + playerID);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (working) {
                    try {
                        onCycle();
                        GUtils.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public Player getPlayer() {
        return player;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    //ART HELPERS

    private King getKing() {
        for (GEntity e : game.entities)
            if (e instanceof King)
                if (((King) e).getOwner() == player.getID())
                    return (King) e;
        return null;
    }

    private List<Human> getHumans(boolean enamy) {
        List<Human> l = new ArrayList<Human>();
        for (GEntity e : game.entities)
            if (e instanceof Human)
                if ((((Human) e).getOwner() == player.getID()) ^ enamy)
                    l.add((Human) e);
        return l;
    }

    private List<Human> getEnemies() {
        return getHumans(true);
    }

    private List<Human> getCitizens() {
        return getHumans(false);
    }

    private void createWorker() {
        player.getCastle().onAction("make");
    }

    private void createBoat() {
        player.getPier().onAction("make");
    }


    private void createSoldier(Worker w) {
        w.onAction("soldier");
    }

    private void createWoodCutter(Worker w) {
        w.onAction("WoodCutter");
    }


    //Artist : you can also use player and game objects !

    ////////////////////////////////////////////////////////////////////////////////////////////
    //ART AREA :)
    int cycleCounter;

    private void onCycle() {
        cycleCounter++;

        List<Human> citizens = getCitizens();
        List<Human> enemies = getEnemies();

        if (cycleCounter == 5)
            game.sendChat("Hello every one !", player.getName());
        else if (cycleCounter % 5 == 0) {
            switch (GUtils.random.nextInt(2)) {
                case 0:
                    game.sendChat("boo a new worker !", player.getName());
                    createWorker();
                    break;
                case 1:
                    for (Human h : citizens) {
                        if (h instanceof Worker) {
                            createSoldier((Worker) h);
                            game.sendChat("boo a new soldier !", player.getName());
                            break;
                        }
                    }
                    break;
                case 2:
                    for (Human h : citizens) {
                        if (h instanceof Worker) {
                            createSoldier((Worker) h);
                            game.sendChat("boo a new soldier !", player.getName());
                            break;
                        }
                    }
            }
        }


    }

    ////////////////////////////////////////////////////////////////////////////////////////////
}
