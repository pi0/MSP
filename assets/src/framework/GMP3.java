package framework;

import javazoom.jl.decoder.JavaLayerException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class GMP3 {

    javazoom.jl.player.Player player=null;

    public GMP3(String name) {

        try {
            player=new javazoom.jl.player.Player(new BufferedInputStream(
                    new FileInputStream(name)));
        } catch (Exception e) {
            GDB.e("Unable to load MP3 music !");
        }
    }

    public void play() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(player!=null)
                    try {
                        player.play();
                    } catch (JavaLayerException e) {
                        GDB.e("Unable to play MP3 !");
                    }
            }
        }).start();
    }

}
