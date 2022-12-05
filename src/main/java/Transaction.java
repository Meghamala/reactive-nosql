import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Stack;

public class Transaction {

    private boolean active;
    private NoSqlDb db;
    Stack<DbCommand> commandStack = new Stack<>();

    public Transaction(NoSqlDb db) {
        this.db = db;
        this.active = true;
    }

    public NoSqlDb put(String key, Object value) {
        try {
            PutCommand pcmd = new PutCommand(key,value);
            pcmd.execute(db);
            commandStack.push(pcmd);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return db;
    }

    public Object get(String key) {
        Object val = null;
        try{
            val = db.get(key);
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return val;
    }

    public int getInt(String key) {
        Object intValue = null;
        try{
            intValue = db.get(key);
            if (!(intValue instanceof Integer)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return (int)intValue;
    }

    public double getDouble(String key) {
        Object doubleValue = null;
        try{
            doubleValue = db.get(key);
            if (!(doubleValue instanceof Double)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return (double) doubleValue;
    }

    public String getString(String key) {
        Object strValue = null;
        try{
            strValue = db.get(key);
            if (!(strValue instanceof String)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return (String) strValue;
    }

    public ArrayType getArray(String key) {
        Object arrValue = null;
        try{
            arrValue = db.get(key);
            if (!(arrValue instanceof ArrayType)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return (ArrayType) arrValue;
    }

    public ObjectType getObject(String key) {
        Object objValue = null;
        try{
            objValue = db.get(key);
            if (!(objValue instanceof ObjectType)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return (ObjectType) objValue;
    }

    public NoSqlDb remove(String key) {
        Object value = null;
        try{
            RemoveCommand rcmd = new RemoveCommand(key);
            rcmd.execute(db);
            commandStack.push(rcmd);
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return db;
    }

    public boolean isActive() {
        return active;
    }

    public void abort() {
        try {
            while (!commandStack.isEmpty()) {
                commandStack.pop().undo(db);
            }
        } catch (NullPointerException e) {
            active = false;
        }
        this.kill();
    }

    public void commit() {
        if(active){
            commandStack = null;
        }
        active = false;
        this.kill();
    }

    private void kill() {
        this.db = null;
    }
}
