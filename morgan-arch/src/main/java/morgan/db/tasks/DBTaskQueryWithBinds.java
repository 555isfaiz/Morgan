package morgan.db.tasks;

import morgan.db.Record;
import morgan.support.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBTaskQueryWithBinds extends DBTask {

    public DBTaskQueryWithBinds() {
        taskType_ = TASK_QUERY;
    }

    @Override
    public void beforeProcess() {
        sql_ = "SELECT * FROM " + tableName_ + " WHERE ";
        sql_ += labels_.get(0) + " = '" + values_.get(0) + "';";
    }

    @Override
    public void process(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql_);
            List<Record> r = new ArrayList<>();
            while (result.next()) {
                r.add(new Record(result));
            }
            queryCallBack_.apply(r, queryCall_);
        } catch (Exception e) {
            Log.db.error("SELECT error, sql:{} , cid:{}", sql_, cid_);
        }
    }

    @Override
    public DBTask merge(DBTask task) {
        throw new UnsupportedOperationException("Task QueryWithBinds is unmergeable");
    }
}
