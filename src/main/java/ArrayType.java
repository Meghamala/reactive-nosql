import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ArrayType{

    JSONArray arrayObj;

    public ArrayType(){
        arrayObj = new JSONArray();
    }

    public void put(Object items) throws ParseException {
        if (items instanceof String) {
            // if there is string representation of array
            fromString(String.valueOf(items));
        } else {
            arrayObj.add(items);
        }
    }

    public Object get(int index) {
        Object val = null;
        try{
            val = arrayObj.get(index);
        } catch (java.lang.Exception e) {
            throw new IndexOutOfBoundsException(String.valueOf(e));
        }
        return val;
    }

    public int getInt(int index) {
        Object intValue = null;
        try{
            intValue = arrayObj.get(index);
            if (!(intValue instanceof Integer)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new IndexOutOfBoundsException(String.valueOf(e));
        }
        return (int)intValue;
    }

    public String getString(int index) {
        Object strValue = null;
        try{
            strValue = arrayObj.get(index);
            if (!(strValue instanceof String)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new IndexOutOfBoundsException(String.valueOf(e));
        }
        return (String)strValue;
    }

    public Double getDouble(int index) {
        Object doubleValue = null;
        try{
            doubleValue = arrayObj.get(index);
            if (!(doubleValue instanceof Double)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new IndexOutOfBoundsException(String.valueOf(e));
        }
        return (double)doubleValue;
    }

    public JSONArray getArray(int index) {
        Object arrValue = null;
        try{
            arrValue = arrayObj.get(index);
            if (!(arrValue instanceof JSONArray)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new IndexOutOfBoundsException(String.valueOf(e));
        }
        return (JSONArray)arrValue;
    }

    public JSONObject getObject(int index) {
        Object objValue = null;
        try{
            objValue = arrayObj.get(index);
            if (!(objValue instanceof JSONObject)) {
                throw new ClassCastException();
            }
        } catch (java.lang.Exception e) {
            throw new IndexOutOfBoundsException(String.valueOf(e));
        }
        return (JSONObject)objValue;
    }

    public int length(){
        return arrayObj.size();
    }

    public Object remove(int index){
        Object value = null;
        try{
            value =  arrayObj.remove(index);
        }
        catch (java.lang.Exception e) {
            throw new IndexOutOfBoundsException(String.valueOf(e));
        }
        return value;
    }

    private void fromString(String items) throws ParseException {
        arrayObj = (JSONArray) new JSONParser().parse(items);
    }

    public String toString() {
        return arrayObj.toString();
    }
}