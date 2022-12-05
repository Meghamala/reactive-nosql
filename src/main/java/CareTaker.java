import java.io.IOException;

//caretaker stores snapshot memento and is used to call recover using latest memento
public class CareTaker{

    private NoSqlDb.Snapshot backup;

    public void createSnapshot(NoSqlDb db) throws IOException {
        backup = db.snapshot();
    }

    public NoSqlDb recover(NoSqlDb db){
            if(backup != null) {
                db = backup.recover();
            }
        return db;
    }
}