package framework;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.Map;
import java.util.TreeMap;

public class GLayerManager {

    private Map<Integer, BufferedImage> layers;
    private GSize layerSize;

    public GLayerManager(GSize layerSize) {
        layers = new TreeMap();
        this.layerSize = layerSize;
    }

    public Image getView(GRect cam) {
       VolatileImage combine = GUtils.createCompatibleVolatileImage(cam.size.width,cam.size.height);
//        BufferedImage combine = new BufferedImage(cam.size.width, cam.size.height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D cg = (Graphics2D) combine.getGraphics();
        GUtils.tweakGraphics(cg);

        try {
            for (BufferedImage i : layers.values())
                cg.drawImage(getSubImage(i, cam), 0 , 0, null);
        } catch (Exception e) {
            //Layer modified ....
            return getView(cam);
        }
        return combine;
    }

    private BufferedImage getSubImage(BufferedImage i, GRect r) {
        int sx = r.location.x < 0 ? 0 : r.location.x;
        int sy = r.location.y < 0 ? 0 : r.location.y;

        int width = sx + r.size.width >= i.getWidth() - 1 ?
                i.getWidth() - sx - 2 : r.size.width;
        int height = sy + r.size.height >= i.getHeight() - 1 ?
                i.getHeight() - sy - 2 : r.size.height;

        return i.getSubimage(sx, sy, width, height);
    }

    public BufferedImage getLayer(int layerNum) {
        BufferedImage img = layers.get(layerNum);
        if (img == null) {
            img = makeLayer();
            layers.put(layerNum, img);
        }
        return img;
    }

    private BufferedImage makeLayer() {
        return GUtils.createCompatibleImage(layerSize.width, layerSize.height);
    }

    public Graphics2D getGraphics(int layerNum, boolean clear) {
        BufferedImage l = getLayer(layerNum);
        Graphics2D g = (Graphics2D) l.getGraphics();
        GUtils.tweakGraphics(g);

        if (clear)
            GUtils.transClearGraphics(g, 0, 0, l.getWidth(), l.getHeight());

        return g;
    }

}
