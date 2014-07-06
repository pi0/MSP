package msp.game.entities;

import framework.*;
import msp.game.MSPEntity;

import java.util.List;


public class Castle extends MSPEntity {


    public Castle(GProperty properties, int id, GGame game) {
        super(properties, id, game);

    }

    @Override
    public List<String> getInfo() {
        List<String> a = super.getInfo();

        a.add("Health :" + properties.getStr("health"));

        return a;
    }

    @Override
    public List<String> getButtons() {
        List<String> a = super.getButtons();

        a.add("human.worker.icon;0;Make a worker;make");

        return a;
    }

    @Override
    public void onAction(String command) {

        if ("make".equals(command)) {
        	int food = game.currentPlayer.getFood() ;
        	
        	if(food>100){
            Worker w = (Worker) GEntity.inflate(GEntity.entityDefaultProperties.get("worker"), game);
            w.setOwner(getOwner());

            GRect r=getRect();
            GPoint a=r.getBottomRight();
            a.x+=10;
            a.y+=10;
            w.setLocation(r.location);
            w.properties.put("dst",r.getBottomRight());
            
            game.currentPlayer.setFood(food-20);
            game.addEntity(w);
        	}
        	

           
        }

    }
}
