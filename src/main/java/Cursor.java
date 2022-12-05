import java.util.ArrayList;

public class Cursor implements CursorInterface{

    private NoSqlDb db;
    private String key;
    private Object value;
    ArrayList<ObserverInterface> observers;

    private Cursor(NoSqlDb db, String key, Object value){
        this.db = db;
        this.key = key;
        this.value = value;
        observers = new ArrayList<>();
    }

    public Cursor() {
        // return empty cursor
    }

    public static Cursor getCursor(NoSqlDb noSqlDb, String key, Object value) {
        return new Cursor(noSqlDb, key, value);
    }

    public static Cursor getCursor() {
        return new Cursor();
    }

    public Object get(){
        return db.get(key);
    }

    public int getInt() {
        return db.getInt(key);
    }

    public double getDouble() {
        return db.getDouble(key);
    }

    public String getString() {
        return db.getString(key);
    }

    @Override
    public void addObserver(ObserverInterface observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(ObserverInterface observer) {
        observers.remove(observer);
    }

    @Override
    public int getObservers(){
        return observers.size();
    }

    // if database update is for a different key than the key held by cursor, then
    // observers are not updated
    @Override
    public void notifyObservers(String key, Object value) {
        if(this.key == key) {
            this.value = value;
            for(ObserverInterface o:observers){
                o.update(key,value);
            }
        }
    }
}