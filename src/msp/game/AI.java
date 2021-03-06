package msp.game;

import framework.GEntity;
import framework.GPoint;
import framework.GUtils;
import msp.game.entities.*;

import java.util.ArrayList;
import java.util.List;

public class AI {

	Player player;
	MSPGame game;
	boolean working = true;
	List<Worker> worker = new ArrayList<Worker>();

	public AI(int playerID, final MSPGame game) {
		this.game = game;
		player = game.createPlayer(playerID, "CPU " + playerID);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (working) {
					try {
						if (player == null
								|| player.properties.getBool("disposed")) {
							game.losePlayer(player.getID());
							break;
						}
						onCycle();
						GUtils.sleep(1000);
					} catch (Exception e) {
						// break;
					}
				}
			}
		}).start();
	}

	public Player getPlayer() {
		return player;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////
	// ART HELPERS

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

	private void createArcher(Worker w) {
		w.onAction("archer");
	}

	private void createSoldier(Worker w) {
		w.onAction("soldier");
	}

	private void createWoodCutter(Worker w) {
		w.onAction("WoodCutter");
	}

	private void setArcherDest(GPoint location, List<Human> citizens) {
		for (MSPEntity e : citizens)
			if (e instanceof Archer)
				if (!((Human) e).attack)
					e.properties.put("dst", location);
				else
					e.properties.put("dst", null);

	}

	private void setSoldiersDest(GPoint location, List<Human> citizens) {
		for (MSPEntity e : citizens)
			if (e instanceof Soldier)
				if (!((Human) e).attack)
					e.properties.put("dst", location);
				else
					e.properties.put("dst", null);
	}

	// Artist : you can also use player and game objects !

	// //////////////////////////////////////////////////////////////////////////////////////////
	// ART AREA :)
	int cycleCounter;

	private void onCycle() {
		cycleCounter++;

		List<Human> citizens = getCitizens();
		List<Human> enemies = getEnemies();
		List<Human> workers = new ArrayList<Human>();

		int woodCutter = 0;
		int soldier = 0;
		int boat = 0;
		int worker = 0;
		int archer = 0;

		if (cycleCounter == 5)
			game.sendChat("Hello every one !", player.getName());

		// find humans
		if (GUtils.random.nextInt(5) == 0) {

			for (MSPEntity e : citizens) {
				if (e instanceof WoodCutter)
					woodCutter++;
				else if (e instanceof Boat)
					boat++;
				else if (e instanceof Archer)
					archer++;
				else if (e instanceof Soldier)
					soldier++;
				else if (e instanceof Worker)
					worker++;
			}

			// Rise number of workers to 20
			for (int c = 0; c < 20 - worker; c++) {
				createWorker();
				if (GUtils.random.nextBoolean())
					break;
			}
			// find workers
			for (MSPEntity w : citizens)
				if (w instanceof Worker)
					workers.add((Worker) w);
			// Rise number of wood cutters to 5
			while (woodCutter < 5 && getPlayer().getFood() > 10) {
				if (workers.size() > 0) {
					createWoodCutter((Worker) workers.get(0));
					workers.remove(0);
					woodCutter++;
					if (GUtils.random.nextBoolean())
						break;
				} else
					break;
			}
			// Rise number of soldiers to 10
			while (soldier < 10 && getPlayer().getFood() > 10) {
				if (workers.size() > 0) {
					createSoldier((Worker) workers.get(0));
					workers.remove(0);
					soldier++;
					if (GUtils.random.nextBoolean())
						break;
				} else
					break;
			}
			while (archer < 5 && getPlayer().getFood() > 10) {
				if (workers.size() > 0) {
					createArcher((Worker) workers.get(0));
					workers.remove(0);
					archer++;
					if (GUtils.random.nextBoolean())
						break;
				} else
					break;
			}
			// Rise number of boats to 5
			while (boat < 5 && getPlayer().getWood() > 300) {
				createBoat();
				boat++;
			}
			// find King and attack him
			if (soldier == 10)
				for (MSPEntity k : enemies)
					if (k instanceof King)
						setSoldiersDest(k.getLocation(), citizens);

			for (MSPEntity s : enemies)
				if (s instanceof Human)

					setArcherDest(s.getLocation(), citizens);

			
			
			
		}

	}

	// //////////////////////////////////////////////////////////////////////////////////////////
}
