package framework;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;

public class GWindow extends JFrame {

    protected void enableFullscreenMode() {

       setUndecorated(true);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if(gd.isFullScreenSupported() && false) {
            gd.setFullScreenWindow(this);
            //requestFocusInWindow();
        } else {
            GDB.i("Fullscreen mode is not supported . trying to emulate it ...");
            setSize(getToolkit().getScreenSize());
            setLocation(0, 0);

           // requestFocusInWindow();
        }


        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setAlwaysOnTop(true);
                super.focusGained(e);
            }

            @Override
            public void focusLost(FocusEvent e) {
                setAlwaysOnTop(false);
                super.focusLost(e);
            }
        });
    }

    public void close() {
        WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
    }



}
