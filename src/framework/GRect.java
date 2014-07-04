package framework;

public class GRect {

    public GSize size;
    public GPoint location;

    public GRect(GPoint location, GSize size) {
        this.location = location;
        this.size = size;
    }

    public GRect(int x, int y, int w, int h) {
        location = new GPoint(x, y);
        size = new GSize(w, h);
    }


    public GRect(GPoint a, GPoint b) {
        GPoint s = new GPoint(Math.min(a.x, b.x), Math.min(a.y, b.y));
        GPoint e = new GPoint(Math.max(a.x, b.x), Math.max(a.y, b.y));

        this.location = s;
        this.size = new GSize(e.x - s.x, e.y - s.y);

    }

    public GPoint getCenter() {
        return new GPoint(location.x + size.width / 2,
                location.y + size.height / 2);
    }

    public GPoint getBottomRight() {
        return new GPoint(location.x+size.width,location.y+size.height);
    }

    public GPoint getBottomLeft() {
        return new GPoint(location.x,location.y+size.height);
    }

    public GPoint getTopLeft() {
        return new GPoint(location.x,location.y);
    }

    public GPoint getTopRight() {
        return new GPoint(location.x+size.width,location.y);
    }

    public void resize(GSize delta) {
        size.resize(delta);
        location.x -= delta.width / 2;
        location.y -= delta.height / 2;
    }


    public String toString() {
        return "Size:" + size.toString() +
                ",Location:" + location.toString();
    }

    public boolean intersectsWith(GRect rect) {

        boolean xOverlap = GUtils.valueInRange(rect.location.x, location.x, location.x + size.width) ||
                GUtils.valueInRange(location.x, rect.location.x, rect.location.x + rect.size.width);

        boolean yOverlap = GUtils.valueInRange(rect.location.y, location.y, location.y + size.height) ||
                GUtils.valueInRange(location.y, rect.location.y, rect.location.y + rect.size.height);

        return xOverlap && yOverlap;

    }

    public boolean contains(GPoint p) {

        boolean xRange = GUtils.valueInRange(p.x, location.x, location.x + size.width);
        boolean yRange = GUtils.valueInRange(p.y, location.y, location.y + size.height);

        return xRange && yRange;
    }
}
