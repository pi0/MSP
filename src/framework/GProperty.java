package framework;

import com.json.generators.JsonGeneratorFactory;
import com.json.parsers.JsonParserFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GProperty extends HashMap<String, Object> {


    private ArrayList<GProperty> adapters = new ArrayList<GProperty>();
    private ArrayList<GPropertyChangeListener> changeListeners = new ArrayList<GPropertyChangeListener>();

    //=======================================================================================
    //  Class related
    //=======================================================================================

    public GProperty(Map map) {
        super(map);
    }


    public GProperty() {

    }

    public GProperty(String json) {
        this(JsonParserFactory
                .getInstance()
                .newJsonParser()
                .parseJson(json));
    }



    public static GProperty restoreFromJson(String json) {
        return new GProperty((HashMap) ((ArrayList)new GProperty(json).get("root")).get(0));
    }

    public GProperty clone() {
        GProperty p = new GProperty(this);
        p.adapters.addAll(adapters);
        return p;
    }


    public String toJson() {
        return JsonGeneratorFactory.getInstance()
                .newJsonGenerator()
                .generateJson(new HashMap(this));
    }



    //=======================================================================================
    //  Helpers
    //=======================================================================================

    public void addAdapter(GProperty p) {
        if (p == null)
            return;
        adapters.add(p);
        //Listen to adapter's change events
/*        p.changeListeners.add(new GPropertyChangeListener() {
            @Override
            public void onPropertyChange(String key, Object value) {
//                this.onPropertyChange(key,value);

            }
        });*/
    }

    //=======================================================================================
    //  Getters
    //=======================================================================================

    public boolean isDefined(String key) {
        return get(key) != null;
    }

    @Override
    public Object get(Object key) {
        if (key == null)
            return null;
        Object o = super.get(key);
        if (o != null)
            return o;
        for (GProperty p : adapters) {
            o = p.get(key);
            if (o != null)
                return o;
        }
        return null;
    }

    public GProperty getProperty(String key) {
        Object o = get(key);
        if (o instanceof GProperty)
            return (GProperty) o;
        else if (o instanceof Map) {
            GProperty p = new GProperty((Map) o);
            put(key, p);
            return p;
        } else
            return null;
    }

    public ArrayList getArray(String key) {
        return (ArrayList) get(key);
    }

    public int getInt(String key) {
        try {
            Object o = get(key);
            if (o instanceof Integer)
                return (Integer) o;
            else if (o == null)
                return 0;
            else
                return Integer.parseInt(o.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    public float getFloat(String key) {
        try {
            Object o = get(key);
            if (o instanceof Float)
                return (Float) o;
            else if (o == null)
                return 0;
            else
                return Float.parseFloat(o.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    public GPoint getPoint(String key) {
        Object o = get(key);
        if (o == null)
            return null;

        if (o instanceof GPoint)
            return (GPoint) o;
        else if(o instanceof  HashMap) {
            Object[] a=((HashMap) o).values().toArray();
            put(key, new GPoint(Integer.parseInt(a[0].toString()),Integer.parseInt(a[1].toString())));
            return getPoint(key);
        }
        else
            return new GPoint(o.toString());
    }

    public String getStr(String key) {
        return (String) get(key);
    }

    public boolean getBool(String key) {
        Object o = get(key);
        if (o instanceof Boolean)
            return (Boolean) o;
        else if (o instanceof String)
            return ((String) o).matches("true");
        else
            return false;
    }

    public Map<String, GProperty> getSubProperties() {

        final Map<String, GProperty> l = new HashMap();

        keySet().forEach(new Consumer<String>() {
            @Override
            public void accept(String key) {
                GProperty val = getProperty(key);
                if (val != null)
                    l.put(key, val);
            }
        });


        return l;
    }

    //=======================================================================================
    //  Setters
    //=======================================================================================
    //All putters call putVal which is not accessible so we have to override all of them


    public Object putSilent(String key, Object value) {
        return super.put(key, value);
    }


    @Override
    public Object put(String key, Object value) {
        onChange(key, value);
        return super.put(key, value);
    }

    @Override
    public Object putIfAbsent(String key, Object value) {
        onChange(key, value);
        return super.putIfAbsent(key, value);
    }

    public void putAllSilent(Map<? extends String, ?> m) {
        super.putAll(m);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        m.forEach(new BiConsumer() {
            @Override
            public void accept(Object o, Object o2) {
                onChange((String) o, o2);
            }
        });
        super.putAll(m);
    }

    //=======================================================================================
    //  Events and listeners
    //=======================================================================================

    private void onChange(String key) {
        onChange(key, get(key));
    }

    private void onChange(String key, Object value) {
        for (GPropertyChangeListener listener : changeListeners)
            listener.onPropertyChange(key, value);
    }

    public void addChangeListener(GPropertyChangeListener l) {
        changeListeners.add(l);
    }

    public interface GPropertyChangeListener {
        public void onPropertyChange(String key, Object value);
    }

}
