package framework;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GImage {

    BufferedImage OData;
    String name;
    GSize matrixSize;
    GSize frameSize;
    long lastAnimTime;
    int currentFrame;
    int animDelayMS;
    private int frameCount;
    boolean reverseAnim = false;

    //=======================================================================================
    //  Constructors
    //=======================================================================================

    public GImage(String name, BufferedImage data, GSize matrixSize, int animDelayMS) {

        this.OData = GUtils.createCompatibleImage(data);

        this.name = name;
        this.currentFrame = 0;

        if (animDelayMS < 0) {
            reverseAnim = true;
            animDelayMS *= -1;
        }

        this.animDelayMS = animDelayMS;
        this.matrixSize = matrixSize;

        frameCount = matrixSize.getArea();

        this.frameSize = new GSize(data.getWidth() / matrixSize.width,
                data.getHeight() / matrixSize.height);

        while (frameSize.width * matrixSize.width >= data.getWidth())
            frameSize.width--;
        while (frameSize.height * matrixSize.height >= data.getHeight())
            frameSize.height--;

        lastAnimTime = System.currentTimeMillis();
    }

    public GImage(String name, BufferedImage data, GSize matrixSize) {
        this(name, data, matrixSize, 0);
    }

    public GImage(String name, BufferedImage data) {
        this(name, data, new GSize(1, 1));
    }

    //=======================================================================================
    //  Getters
    //=======================================================================================

    int lastGetFrame=-1;
    BufferedImage lll;
    public Image getImage(int frame) {

        frame %= frameCount;
        int x = (frame % matrixSize.width) * frameSize.width;
        int y = (frame / matrixSize.width) * frameSize.height;


        if(lastGetFrame!=frame || lll==null)
            lll= OData.getSubimage(x, y, frameSize.width, frameSize.height);

        lastGetFrame=frame;
        return lll;
    }

    public Image getImage() {
        updateAnim();
        return getImage(currentFrame);
    }

    public Image getAnimImage(int time) {
        updateAnim();
        return getImage(currentFrame+time);
    }

    public ImageIcon getImageIcon(int frame) {
        return new ImageIcon(getImage(frame));
    }

    public ImageIcon getImageIcon() {
        return new ImageIcon(getImage());
    }


    //=======================================================================================
    //  Animation functions
    //=======================================================================================

    public boolean isAnimated() {
        return animDelayMS > 0;
    }

    private int getAnimUpdateCount() {
        return (int) ((System.currentTimeMillis() - lastAnimTime) / animDelayMS);
    }

    int step = +1;

    private void updateAnim() {
        if (isAnimated()) {
            int c = getAnimUpdateCount();
            if (c > 0) {

                if (reverseAnim) {
                    currentFrame += step;
                    if (currentFrame >= frameCount) {
                        step = -1;
                        currentFrame += step;
                    } else if (currentFrame <= 0) {
                        step = +1;
                        currentFrame += step;
                    }
                } else
                    nextFrame(c);
                lastAnimTime = System.currentTimeMillis();
            }
        }
    }

    public void nextFrame(int count) {
        currentFrame += count;
        currentFrame %= frameCount;
    }

    public int getFrameCount() {
        return frameCount;
    }
}
