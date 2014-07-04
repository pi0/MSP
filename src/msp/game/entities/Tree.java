package msp.game.entities;

import framework.GImage;
import msp.game.MSPEntity;
import framework.GGame;
import framework.GProperty;

import java.util.ArrayList;
import java.util.List;

public class Tree extends MSPEntity {


    public Tree(GProperty properties, int id, GGame game) {
        super(properties, id, game);
    }

    @Override
    public GImage getImage() {
        return super.getImage();
    }

    public List<String> getInfo() {
        List<String> info = super.getInfo();

        info.add("Wood: " + properties.getInt("wood"));

        return info;
    }

    public List getButtons() {
        List<String> a = new ArrayList<String>();

        return a;
    }

}
