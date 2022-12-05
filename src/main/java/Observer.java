public class Observer implements ObserverInterface{

    @Override
    public void update(String key, Object value) {
        System.out.println("New value for key" + key + " is " + value);
    }
}