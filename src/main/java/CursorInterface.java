public interface CursorInterface{
    void addObserver(ObserverInterface observer);
    void removeObserver(ObserverInterface observer);
    int getObservers();
    void notifyObservers(String key, Object value);
}