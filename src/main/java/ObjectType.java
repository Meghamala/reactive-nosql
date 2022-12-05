import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.NoSuchElementException;

public class ObjectType{

    JSONObject obj;
    
    public ObjectType(){
        obj = new JSONObject();
    }

    public void put(String key, Object value) {
        obj.put(key,value);
    }

    public Object get(String key) {
        Object val = null;
        try{
            val = obj.get(key);
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return val;
    }

    public int getInt(String key) {
        Object intValue = null;
        try{
            intValue = obj.get(key);
            if (!(intValue instanceof Integer)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return (int)intValue;
    }

    public String getString(String key) {
        Object strValue = null;
        try{
            strValue = obj.get(key);
            if (!(strValue instanceof String)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return (String)strValue;
    }

    public Double getDouble(String key) {
        Object doubleValue = null;
        try{
            doubleValue = obj.get(key);
            if (!(doubleValue instanceof Double)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return (double)doubleValue;
    }

    public JSONArray getArray(String key) {
        Object arrValue = null;
        try{
            arrValue = obj.get(key);
            if (!(arrValue instanceof JSONArray)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return (JSONArray)arrValue;
    }

    public JSONObject getObject(String key) {
        Object objValue = null;
        try{
            objValue = obj.get(key);
            if (!(objValue instanceof org.json.simple.JSONObject)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return (JSONObject)objValue;
    }

    public int length(){
        return obj.length();
    }

    public Object remove(String key){
        Object value = null;
        try{
            value =  obj.remove(key);
        }
        catch (java.lang.Exception e) {
            throw new NoSuchElementException(e);
        }
        return value;
    }

    private void fromString(String items) throws ParseException {
        obj = (JSONObject) new JSONParser().parse(items);
    }

    public String toString() {
        return obj.toString();
    }
}