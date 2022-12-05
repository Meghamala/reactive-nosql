import org.json.JSONObject;

public class UpdateCommand implements DbCommand{

    private String key;
    private Object value;
    private static final String cmdName = "UPDATE";

    public UpdateCommand(String key, Object value){
        this.key = key;
        this.value = value;
    }

    @Override
    public void execute(NoSqlDb db) {
        db.update(key,value);
    }

    @Override
    public void undo(NoSqlDb db) {
        db.remove(key);
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