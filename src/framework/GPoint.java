package framework;

public class GPoint {
    public int x, y;

    public GPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public GPoint(String c) {
        String[] s = c.split(",");
        this.x = Integer.parseInt(s[0]);
        this.y = Integer.parseInt(s[1]);
    }

    public GPoint centerWith(GSize size) {
        return new GPoint(x - size.width / 2, y - size.height / 2);
    }

    public String toString() {
        return x + "," + y;
    }

    public boolean equals(GPoint p) {
        if (p == null || !(p instanceof GPoint))
            return false;
        return x == p.x && y == p.y;
    }

    public int distanceTo(GPoint location) {
        int a=location.x-x;a*=a;
        int b=location.y-y;b*=b;
        return (int)Math.sqrt(a+b);
    }
}
