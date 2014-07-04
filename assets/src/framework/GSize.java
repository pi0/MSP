package framework;

public class GSize {
    public int width, height;

    public GSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public GSize(String s) {
        String[] d = s.split("[xX\\*]");
        this.width = Integer.parseInt(d[0]);
        this.height = Integer.parseInt(d[1]);
    }

    public int getArea() {
        return width * height;
    }

    public String toString() {
        return width + "X" + height;
    }

    public void resize(GSize delta) {
        width += delta.width;
        height += delta.height;
    }
}
