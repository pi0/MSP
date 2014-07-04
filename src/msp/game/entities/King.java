package msp.game.entities;

import framework.GGame;
import framework.GProperty;

import java.util.ArrayList;
import java.util.List;

public class King extends Human {

    public King(GProperty properties, int id, GGame game) {
        super(properties, id, game);
    }


    @Override
    public List<String> getInfo() {

        List<String> a = super.getInfo();


        return a;
    }

    @Override
    public List<String> getButtons() {
        List<String> a = new ArrayList<String>();

        return a;
    }

    @Override
    protected void onThreadCycle(int delayMs) {
        super.onThreadCycle(delayMs);

        super.onThreadCycle(delayMs);
        if(properties.getInt("health")<=0)
            game.losePlayer(getOwner());

    }
}
