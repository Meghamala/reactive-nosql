import org.json.JSONObject;

public class RemoveCommand implements DbCommand{

    private String key;
    private Object value;
    private static final String cmdName = "REMOVE";

    public RemoveCommand(String key) {
        this.key = key;
    }

    public RemoveCommand(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public void execute(NoSqlDb db) {
        this.getValue(db.remove(key));
    }

    private void getValue(Object value) {
        this.value = value.toString();
    }

    @Override
    public void undo(NoSqlDb db) {
        db.put(key,value);
    }

    public String toString() {
        JSONObject obj = new JSONObject().put(cmdName, new JSONObject().put(key,value));
        return obj.toString();
    }

    @Override
    public String getName() {
        return cmdName;
    }
}