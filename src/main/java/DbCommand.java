public interface DbCommand{
    void execute(NoSqlDb db);
    void undo(NoSqlDb db);
    String getName();
}