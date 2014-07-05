package framework;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Vector;

public class GSound {

    private byte[] samples;
    private Vector<Clip> clips=new Vector();

    GSound(File file){
        try {
            samples = new byte[(int) file.length()];
            DataInput is = new DataInputStream(new FileInputStream(file));
            is.readFully(samples);
        }catch (Exception e) {
            ;
        }
    }

    public void playSound()
    {
        Clip a=getClip();
        a.loop(999);
        clips.add(a);
    }
    public void playSoundOnce() {
        stopAll();
        getClip().start();
    }

    private Clip getClip() {
        Clip c = null;
        try {
            AudioInputStream ais=AudioSystem.getAudioInputStream(
                    new ByteInputStream(samples,samples.length));
            c = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
            c.open(ais);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public synchronized void stopAll() {
        for(Clip c:clips)
            if(c.isRunning())
                c.stop();
    }


}


//class LoopingByteInputStream extends ByteArrayInputStream {
//
//    private boolean closed;
//
//    public LoopingByteInputStream(byte[] buffer) {
//        super(buffer);
//        closed = false;
//    }
//
//    public int read(byte[] buffer, int offset, int length) {
//        if (closed) {
//            return -1;
//        }
//        int totalBytesRead = 0;
//
//        while (totalBytesRead < length) {
//            int numBytesRead = super.read(buffer,
//                    offset + totalBytesRead,
//                    length - totalBytesRead);
//
//            if (numBytesRead > 0) {
//                totalBytesRead += numBytesRead;
//            }
//            else {
//                reset();
//            }
//        }
//        return totalBytesRead;
//    }
//
//    public void close() throws IOException {
//        super.close();
//        closed = true;
//    }
//
//
//}
