import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.Map;
import java.util.NoSuchElementException;

public class NoSqlDb{

    /* The NoSqlDb class defines the `getDatabase` method that lets
 clients access the same instance of a database connection
 throughout the program.*/

    private Map<String, Object> DbMap;
    private static String LOG_FILE_NAME = "commands.txt";
    private static String SNAPSHOT_FILE_NAME = "dbSnapshot.txt";
    Transaction dbTransaction;
    private static File logFile;
    private File snapshotFile;
    private final ReadWriteLock readWriteLock
            = new ReentrantReadWriteLock();
    private final Lock writeLock
            = readWriteLock.writeLock();
    private final Lock readLock = readWriteLock.readLock();
    Cursor cursor = getCursor();

    private NoSqlDb(){
        DbMap = new HashMap<>();
        logFile();
    }

    public NoSqlDb(Map<String, Object> newMap) {
        DbMap = new HashMap<>(newMap);
    }

    public static NoSqlDb getDatabase() {
        return new NoSqlDb();
    }

    public NoSqlDb put(String key, Object value) {
        writeLock.lock();
        try{
            writeLog(new PutCommand(key,value), this.logFile);
            DbMap.put(key,value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            writeLock.unlock();
        }
        this.notifyObservers(key,value);
        return this;
    }

    public Object get(String key) {
        Object val = null;
        readLock.lock();
        try{
            val = DbMap.get(key);
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        finally {
            readLock.unlock();
        }
        return val;
    }

    public int getInt(String key) {
        Object intValue = null;
        readLock.lock();
        try{
            intValue = DbMap.get(key);
            if (!(intValue instanceof Integer)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        finally {
            readLock.unlock();
        }
        return (int)intValue;
    }

    public double getDouble(String key) {
        Object doubleValue = null;
        readLock.lock();
        try{
            doubleValue = DbMap.get(key);
            if (!(doubleValue instanceof Double)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        finally {
            readLock.unlock();
        }
        return (double) doubleValue;
    }

    public String getString(String key) {
        Object strValue = null;
        readLock.lock();
        try {
            strValue = DbMap.get(key);
            if (!(strValue instanceof String)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        } finally {
            readLock.unlock();
            return (String) strValue;
        }
    }

    public ArrayType getArray(String key) {
        Object arrValue = null;
        readLock.lock();
        try{
            arrValue = DbMap.get(key);

            if (!(arrValue instanceof ArrayType)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        finally {
            readLock.unlock();
        }
        return (ArrayType) arrValue;
    }

    public ObjectType getObject(String key) {
        Object objValue = null;
        readLock.lock();
        try{
            objValue = DbMap.get(key);
            if (!(objValue instanceof ObjectType)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        finally {
            readLock.unlock();
        }
        return (ObjectType) objValue;
    }

    public Object remove(String key) {
        Object value = null;
        writeLock.lock();
        try{
            value = DbMap.remove(key);
            writeLog(new RemoveCommand(key,value), logFile);
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        finally {
            writeLock.unlock();
        }
        this.notifyObservers(key,null);
        return value;
    }

    public NoSqlDb update(String key, Object value) {
        writeLock.lock();
        if (value == null) {
            throw new NullPointerException();
        }
        try {
            DbMap.put(key, value);
            writeLog(new UpdateCommand(key,value), logFile);
        } finally {
            writeLock.unlock();
        }
        this.notifyObservers(key,value);
        return this;
    }

    // method to notify cursor object for the key-value changes
    private void notifyObservers(String key, Object value) {
        this.cursor.notifyObservers(key,value);
    }

    private void logFile() {
        logFile = new File(LOG_FILE_NAME);
        this.snapshotFile = new File(SNAPSHOT_FILE_NAME);
        writeStringToFile("", logFile);
    }

    private void writeLog(DbCommand command, File logFile) {
        writeStringToFile(command.toString() + "\n", logFile);
    }

    public Transaction createTransaction() {
        this.dbTransaction = new Transaction(this);
        return dbTransaction;
    }

    Snapshot snapshot() {
        try {
            eraseContents(snapshotFile);
            eraseContents(logFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String snapshotData = writeMapToString();
        writeStringToFile(snapshotData, snapshotFile);
        return new Snapshot(snapshotFile, this);
    }

    private void eraseContents(File logFile) throws FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(logFile)) {
            writer.write("");
            writer.flush();
        }
    }

    private void writeStringToFile(String snapshotData, File file) {
        BufferedWriter bf = null;
        try {
            // create new BufferedWriter for the output file
            bf = new BufferedWriter(new FileWriter(file, true));
            bf.write(snapshotData);
            bf.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String writeMapToString() {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        String snapshotData;
        try{
            snapshotData = jsonMapper.writeValueAsString(DbMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return snapshotData;
    }

    private static void readFromFile(File snapshotFile) {
        BufferedReader fileRead = null;
        try {
            fileRead = new BufferedReader(new FileReader(snapshotFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String str;
        while (true)
        {
            try {
                if ((str = fileRead.readLine()) == null)
                    break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(str);
        }
    }

    private static NoSqlDb recover(File dbSnapshotFile) {
        NoSqlDb newDb = recoverFromSnapshot(dbSnapshotFile);
        return recoverFromCommandLog(logFile,newDb);
    }

    private static NoSqlDb recoverFromCommandLog(File logFile, NoSqlDb newDb) {
        List<DbCommand> commandList = getCommandsFromLog(logFile);
        for(DbCommand c: commandList){
            c.execute(newDb);
        }
        return newDb;
    }

    private static List<DbCommand> getCommandsFromLog(File logFile) {
        List<DbCommand> cmdList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(logFile));
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject entry = new JSONObject(line);
                Iterator<String> names = entry.keys();
                String cmdName = names.next();
                JSONObject cmdVal = entry.getJSONObject(cmdName);
                Iterator<String> it = cmdVal.keys();
                String key = it.next();
                Object value = null;
                if (!(cmdVal.isNull(key))) {
                    value = cmdVal.get(key);
                }
                switch (cmdName) {
                    case "PUT" -> cmdList.add(new PutCommand(key, value));
                    case "REMOVE" -> cmdList.add(new RemoveCommand(key));
                    case "UPDATE" -> cmdList.add(new UpdateCommand(key, value));
                    default -> {
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cmdList;
    }

    private static NoSqlDb recoverFromSnapshot(File dbSnapshotFile) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        Map<String, Object> newMap;
        try {
            newMap = mapper.readValue(dbSnapshotFile, Map.class);

        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new NoSqlDb(newMap);
    }

    public void erase() {
        DbMap.clear();
    }

    public Cursor getCursor(String key){
        this.cursor = Cursor.getCursor(this, key, this.get(key));
        return cursor;
    }

    private Cursor getCursor() {
        this.cursor = Cursor.getCursor();
        return cursor;
    }

    // snapshot memento inner class
    static class Snapshot{
        private File dbSnapshotFile;
        private NoSqlDb db;

        public Snapshot(File dbSnapshotFile, NoSqlDb db){
            this.dbSnapshotFile = dbSnapshotFile;
            this.db = db;
        }

        public NoSqlDb recover() {
            return NoSqlDb.recover(this.dbSnapshotFile);
        }
    }
}