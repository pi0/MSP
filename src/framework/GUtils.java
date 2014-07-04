package framework;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class GUtils {


    public static String getTimeStr(float time) {
        int sec= (int) (time*60*60*12);
        int hour=sec%(60*60);
        if(true)
            return hour+"";

        sec/=60*60;
        int min=sec%60;
        sec/=60;

        return String.format("%d:%d:%d",hour,min,sec);
    }

    public static long getRamUsage() {
        return Runtime.getRuntime().freeMemory();
    }

    public static String byteToHumanreadable(long b) {
        return (b/1048576) + " MB";
    }

    public enum ResourceType {
        RESOURCE_TYPE_IMAGE,
        RESOURCE_TYPE_AUDIO,
        RESOURCE_TYPE_PROPERTY,
        RESOURCE_TYPE_INVALID
    }

    private final static HashMap<String, ResourceType> resourceTypes;

    static {
        resourceTypes = new HashMap<String, ResourceType>();
        resourceTypes.put("png", ResourceType.RESOURCE_TYPE_IMAGE);
        resourceTypes.put("jpg", ResourceType.RESOURCE_TYPE_IMAGE);
        resourceTypes.put("json", ResourceType.RESOURCE_TYPE_PROPERTY);
        resourceTypes.put("wav", ResourceType.RESOURCE_TYPE_AUDIO);
        resourceTypes.put("midi", ResourceType.RESOURCE_TYPE_AUDIO);
    }

    public static Random random = new Random();


    public static GraphicsConfiguration graphicsConfiguration
            = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDefaultConfiguration();


    public static String stripExtension(String str) {
        if (str == null) return null;
        int pos = str.lastIndexOf(".");
        if (pos == -1) return str;
        return str.substring(0, pos);
    }

    public static String getFilenameWithoutExtention(File file) {
        return stripExtension(file.getName());
    }

    public static String getFileExtention(File file) {
        String str = file.getName();
        if (str == null) return null;
        int pos = str.lastIndexOf(".");
        if (pos == -1) return "";
        return str.substring(pos + 1);
    }

    public static ResourceType getFileType(File file) {
        String ext = getFileExtention(file);
        ResourceType t = resourceTypes.get(ext);
        if (t == null)
            t = ResourceType.RESOURCE_TYPE_INVALID;
        return t;
    }

    public static boolean hasFlag(int id, int mask) {
        return (id & mask) != 0;
    }

    public static GPoint getMouseLocation(Component c) {
        Point a = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(a, c);
        return new GPoint((int) a.getX(), (int) a.getY());
    }

    public static String readAllFile(File file) {
        try {
            FileInputStream i = new FileInputStream(file);
            byte[] r = new byte[(int) file.length()];
            i.read(r);
            return new String(r);
        } catch (Exception e) {
            return null;
        }
    }

    public static void writeAllFile(File file, String data) throws IOException {
        FileOutputStream o = new FileOutputStream(file);
        o.write(data.getBytes("UTF-8"));
        o.close();
    }

    public static void sleep(int ms) {
        if (ms <= 0)
            return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean valueInRange(int val, int min, int max) {
        return val >= min && val <= max;
    }

    public static void tweakGraphics(Graphics2D g) {
        HashMap<RenderingHints.Key, Object> hints = new HashMap<RenderingHints.Key, Object>();

        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g.setRenderingHints(new RenderingHints(hints));
    }

    public static void drawImage(Graphics2D g, Image image, GRect r) {
        g.drawImage(image, r.location.x, r.location.y, r.size.width, r.size.height, null);
    }

    public static int getDir(GPoint src, GPoint dst) {
        int dx = dst.x - src.x;
        int dy = dst.y - src.y;

        if (dx == 0 && dy == 0)
            return 1;
        if (dx >= 0 && dy >= 0)
            return 1;
        if (dx >= 0 && dy <= 0)
            return 0;
        if (dx <= 0 && dy >= 0)
            return 3;
        if (dx <= 0 && dy <= 0)
            return 2;


        return -1;
    }

    public static VolatileImage createCompatibleVolatileImage(int w,int h) {
        return graphicsConfiguration.createCompatibleVolatileImage(w,h);
    }

    public static BufferedImage createCompatibleImage(int w,int h) {
        return graphicsConfiguration.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
    }

    public static VolatileImage convertBufferedimageToValitile(BufferedImage src) {
        VolatileImage image=createCompatibleVolatileImage(src.getWidth(),src.getHeight());
        Graphics2D g= (Graphics2D) image.getGraphics();
        tweakGraphics(g);
        g.drawImage(src,0,0,null);
        return image;
    }

    public static BufferedImage createCompatibleImage(BufferedImage data) {
        BufferedImage i=createCompatibleImage(data.getWidth(),data.getHeight());
        Graphics2D g= (Graphics2D) i.getGraphics();
        g.drawImage(data,0,0,null);
        return i;
    }

    public static Image resizeImage(Image img,int w,int h) {
        BufferedImage i=createCompatibleImage(w,h);
        Graphics2D g= (Graphics2D) i.getGraphics();
        g.drawImage(img,0,0,w,h,null);
        return i;
    }

    public static String getRandomText(int maxLen){
        int l=1+random.nextInt(maxLen);
        StringBuilder b=new StringBuilder();
        while(l--!=0) {
            b.append((char)('a'+random.nextInt('z'-'a'-1)));
        }
        return b.toString();
    }


    public static int countUsableBytes(byte[] b) {
        int e=b.length-1;
        while(e>=0 && b[e]==0)
            e--;
        return e;
    }


    public static void drawImage(Graphics2D g, Image image, GRect r, float alpha) {
        drawImage(g, image, r.location.x, r.location.y, r.size.width, r.size.height, alpha);
    }

    public static void drawImage(Graphics2D g, Image image, int x, int y, int w, int h, float alpha) {
        Composite c = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.drawImage(image, x, y, w, h, null);
        g.setComposite(c);
    }


    public static void transClearGraphics(Graphics2D g, GRect r) {
        transClearGraphics(g, r.location.x, r.location.y, r.size.width, r.size.height);
    }


    public static void transClearGraphics(Graphics2D g, int x, int y, int w, int h) {
        Composite c = g.getComposite();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(x, y, w, h);
        g.setComposite(c);
    }


}


