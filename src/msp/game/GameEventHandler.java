package msp.game;

public interface GameEventHandler {

    public void onGameEvent(GameEvent e);

    enum GameEvent {
        GAME_EVENT_GAMEOVER
    }

}
