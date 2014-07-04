package framework;

public class GMapBlock {

    private String imageName;
    public int imageFrame;
    public int areaCode;
    public int x, y;

    public  int animSeed=GUtils.random.nextInt(100);

    public GMapBlock(String c, int x, int y) {
        if (c == null)
            return;
        String[] s = c.split(",");
        setImage(s[0]);
        imageFrame = Integer.parseInt(s[1]);
        this.x = x;
        this.y = y;
    }

    public GMapBlock(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setImage(String name) {
        this.imageName = name;
        areaCode = GResource.getInstance().getProperty("properties").
                getProperty("backgroundAreas").getInt(name);
    }

    public GImage getImage(GMap map) {
        String res_name = imageName + "." + map.properties.get("session");

        GImage i=GResource.instance.getImage(res_name);

        if(i!=null) {
            //Dirty hack ;)
            imageFrame %= i.getFrameCount();
        }

        return i;
    }

    @Override
    public String toString() {
        return imageName + "," + imageFrame;
    }


    public GMapBlock clone(int x, int y) {
        return new GMapBlock(this.toString(), x, y);
    }

    public String getImageName() {
        return imageName;
    }
}
