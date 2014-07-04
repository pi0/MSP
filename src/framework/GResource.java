package framework;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GResource {

    private Map<String, GImage> images = new HashMap();

    private Map<String, GProperty> properties = new HashMap();

    public static GResource instance = getInstance();

    public Map<String, GSound> sounds = new HashMap();

    private GResource(File source) {
        //Load all resources from source directory
        GDB.i("Loading resources ...");
        loadSources(source);
        GDB.i("Finished loading resources!");

    }

    public static GResource getInstance() {
        if (instance == null)
            instance = new GResource(new File("assets"));
        return instance;
    }

    public GImage getImage(String name) {
        return images.get(name);
    }

    public GProperty getProperty(String name) {
        return properties.get(name);
    }

    public GProperty getMap(String name) {
        return new GProperty((HashMap) ((ArrayList) properties.get
                (name + ".map").get("root")).get(0));
    }

    private void loadSources(File source) {

        for (File file : source.listFiles()) {
            if (!file.isFile())
                continue;

            String fileName = GUtils.getFilenameWithoutExtention(file);

            switch (GUtils.getFileType(file)) {
                case RESOURCE_TYPE_IMAGE:
                    try {

                        BufferedImage data = ImageIO.read(file);

                        String[] nameData = fileName.split("_");
                        String name = nameData[0];
                        GSize size = null;
                        int animMs = 0;
                        if (nameData.length > 1)
                            size = new GSize(nameData[1]);
                        if (nameData.length > 2)
                            animMs = Integer.parseInt(nameData[2]);

                        GImage i;
                        if (size != null)
                            i = new GImage(name, data, size, animMs);
                        else
                            i = new GImage(name, data);
                        images.put(name, i);
                    } catch (IOException e) {
                        GDB.e("Unable to load image : " + file.getName());
                    }
                    break;
                case RESOURCE_TYPE_PROPERTY:
                    properties.put(fileName,
                            new GProperty(GUtils.readAllFile(file)));
                    break;
                case RESOURCE_TYPE_AUDIO:

                    GSound s = new GSound(file);
                    sounds.put(fileName, s);

                    break;
            }

        }

    }


}
