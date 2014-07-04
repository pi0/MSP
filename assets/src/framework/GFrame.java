package framework;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GFrame extends JPanel implements MouseListener, KeyListener, MouseMotionListener, MouseWheelListener, ComponentListener {

    private Point mousePt;
    public GPoint mouseLocation = new GPoint(0, 0);
    protected boolean isCtrlDown;
    protected boolean isShiftDown;
    protected boolean isAltDown;

    public GFrame() {
        setFocusable(true);
        //requestFocus();
        //requestFocusInWindow();

        //Add listeners
        addMouseListener(this);
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addComponentListener(this);
    }

    //=======================================================================================
    //  Drawing
    //=======================================================================================
    Thread renderThread = null;

    protected void render() {
        if (renderThread == null || !renderThread.isAlive()) {
            renderThread = new Thread(new Runnable() {
                public void run() {
                    repaint();
                }
            });
            renderThread.start();
        }
    }

    //=======================================================================================
    //  Events
    //=======================================================================================

    protected void onMouseUpdate(MouseEvent e) {

    }

    protected void onMouseDragged(int dx, int dy, MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        isCtrlDown = e.isControlDown();
        isShiftDown = e.isShiftDown();
        isAltDown = e.isAltDown();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        isCtrlDown = isShiftDown = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        onMouseUpdate(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePt = e.getPoint();
        onMouseUpdate(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        onMouseUpdate(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        onMouseUpdate(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        onMouseUpdate(e);
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        onMouseDragged(e.getX() - mousePt.x, e.getY() - mousePt.y, e);


        mousePt = e.getPoint();
        onMouseUpdate(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseLocation.x = e.getX();
        mouseLocation.y = e.getY();
        onMouseUpdate(e);

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }

    @Override
    public void componentResized(ComponentEvent e) {

    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
