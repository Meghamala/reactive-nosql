
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NoSqlDbTest {

    NoSqlDb database;
    ArrayType arr1, arr2;
    ObjectType object1, object2;

    @BeforeEach
    public void setUp() throws ParseException {

        database = NoSqlDb.getDatabase();
        object1 = new ObjectType();
        object2 = new ObjectType();
        arr1 = new ArrayType();
        arr2 = new ArrayType();

        arr1.put("[2.3, \"at\", 1.67e3, [1, \"me\", {\"a\":1}], \"bat\"]");
        arr2.put(5);
        arr2.put("san diego");
        arr2.put(6.9999);
        arr2.put(new ArrayType());
        arr2.put(new ObjectType());

        object1.put("bar", arr2);
        object2.put("person", "{\"name\": \"Roger\", \"age\": 21}");

        eraseContents("commands.txt");
        eraseContents("dbSnapshot.txt");
    }

    public void eraseContents(String file) {
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.write("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        database.erase();
    }

    @Test
    void testPutAndGetIntoDb() {
        database.put("id", 1);
        Assertions.assertEquals(1, database.get("id"));

        database.put("firstname", "Megha");
        Assertions.assertEquals("Megha", database.getString("firstname"));

        database.put("gpa", 3.45);
        Assertions.assertEquals(3.45, database.getDouble("gpa"));
        Assertions.assertNull(database.get("lastname"));

        database.put("firstname", "Deepak");
        Assertions.assertEquals("Deepak", database.getString("firstname"));

        database.put("signedid", -234);
        assertThat(database.getInt("signedid"), instanceOf(int.class));

        // array operations
        database.put("str-array", arr1);
        database.put("array", arr2);
        assertThat(database.getArray("array"), instanceOf(ArrayType.class));

        ArrayType strArr = database.getArray("str-array");
        Assertions.assertEquals("[2.3,\"at\",1670.0,[1,\"me\",{\"a\":1}],\"bat\"]", strArr.toString());
        Assertions.assertEquals("[1,\"me\",{\"a\":1}]", arr1.getArray(3));

        ArrayType arr = database.getArray("array");
        Assertions.assertEquals("[5,\"san diego\",6.9999,[],{}]", arr.toString());
        Assertions.assertEquals(5, arr.length());
        Assertions.assertEquals("san diego", arr2.getString(1));

        arr2.remove(3);
        Assertions.assertEquals(4, arr.length());

        // object operations

        database.put("arr-object", object1);
        database.put("str-object", object2);
        assertThat(database.getObject("arr-object"), instanceOf(ObjectType.class));
        ObjectType obj = database.getObject("arr-object");
        Assertions.assertEquals("[5,\"san diego\",6.9999,[],{}]", obj.toString());
        Assertions.assertEquals(1, obj.length());
        object1.remove("bar");
        Assertions.assertEquals(0, object1.length());
    }

    @Test
    void testRemove(){
        testPutAndGetIntoDb();
        Assertions.assertEquals("Deepak", database.remove("firstname"));
        Assertions.assertNull(database.remove("address"));
    }

    @Test
    public void testUpdate() {
        testPutAndGetIntoDb();
        database.update("firstname", "megha");
        Assertions.assertEquals("megha", database.getString("firstname"));
    }

    @Test
    void testTransactionAbort(){
        Transaction t1 = database.createTransaction();
        try {
            t1.put("account-no", 577401);
            t1.put("name", "Megha");
            t1.put("amount", 10000);
            t1.put("Interest", 5.24);
            assertTrue(t1.isActive());
            t1.get("account-no");
            t1.remove("name");
            double interestRate = t1.getDouble("Interest");
            if (interestRate > 4.0) {
                t1.abort();
            } else t1.commit();
            Assertions.assertNull(database.get("account-no"));
            Assertions.assertNull(database.get("Interest"));
        } catch (java.lang.Exception e) {
            t1.abort();
        }
    }

    @Test
    void testTransactionCommit() {
        Transaction t2 = database.createTransaction();
        try {
            t2.put("account-no", 577402);
            t2.put("name", "Deepak");
            t2.put("amount", 20000);
            t2.put("Interest", 3.36);
            assertTrue(t2.isActive());
            t2.get("account-no");
            t2.remove("name");
            double interestRate = t2.getDouble("Interest");
            if (interestRate > 4.0) {
                t2.abort();
            } else{
                assertTrue(t2.isActive());
                t2.commit();
                assertFalse(t2.isActive());
            }
            Assertions.assertNull(database.getString("name"));
            Assertions.assertEquals(20000, database.getInt("amount"));
        } catch (java.lang.Exception e) {
            t2.abort();
        }
    }

    @Test
    void testSnapshotCreationAndRecovery() throws IOException {
        CareTaker dbCaretaker = new CareTaker();
        database.put("id", 1);
        database.put("gpa", 3.45);
        database.put("arr", arr2);
        database.remove("gpa");
        database.put("firstname", "Deepak");
        database.put("signedid", -234);
        dbCaretaker.createSnapshot(database);

        database.put("address", "Sirsi");
        database.put("State", "Karnataka");
        database.put("ph-number", "6693882599");
        dbCaretaker.createSnapshot(database);

        database.remove("signedid");
        database.put("lastname", "Joshi");

        NoSqlDb backupDb = dbCaretaker.recover(database);

        Assertions.assertNull(backupDb.getString("signedid"));
        Assertions.assertEquals("Sirsi", backupDb.getString("address"));
        Assertions.assertEquals("Joshi", backupDb.getString("lastname"));
        ArrayType arr = database.getArray("arr");
        Assertions.assertEquals("[5,\"san diego\",6.9999,[],{}]", arr.toString());
        eraseContents("dbSnapshot.txt");
    }

    @Test
    void testAddRemoveObserver(){
        Cursor c1 = database.getCursor("address");
        Observer o1 = new Observer();
        c1.addObserver(o1);
        Assertions.assertEquals(1, c1.getObservers());
        c1.removeObserver(o1);
        Assertions.assertEquals(0, c1.getObservers());
    }

    @Test
    void testNotifyObservers(){
        Cursor c1 = database.getCursor("address");
        Observer o1 = new Observer();
        c1.addObserver(o1);
        database.put("address", "San diego");
        database.put("zipcode", "2222");
        Assertions.assertEquals("San diego", c1.get());
        c1.removeObserver(o1);
        database.put("address", "monte vista");
        Assertions.assertEquals("monte vista", c1.get());
        database.remove("address");
        Assertions.assertNull(c1.get());
        c1 = database.getCursor("State");
        Observer o2 = new Observer();
        c1.addObserver(o2);
        database.put("State", "California");
        Assertions.assertEquals("California", c1.get());
    }
}