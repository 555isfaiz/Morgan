package morgan.db.tasks;

import morgan.support.Log;

import java.sql.Connection;
import java.sql.Statement;

public class DBTaskRemoveItem extends DBTask {

    public DBTaskRemoveItem() {
        taskType_ = TASK_REMOVE;
    }

    @Override
    public void beforeProcess() {
        sql_ = "DELETE FROM " + tableName_ + " WHERE ID = " + cid_ + ";";
    }

    @Override
    public void process(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql_);
        } catch (Exception e) {
            Log.db.error("REMOVE error, sql:{} , cid:{}", sql_, cid_);
        }
    }

    @Override
    public DBTask merge(DBTask task) {
        //when merging with INSERT, convert it to UPDATE
        if (task.taskType_ == TASK_INSERT) {
            DBTaskUpdate t = new DBTaskUpdate();
            t.labels_ = task.labels_;
            t.values_ = task.values_;
            t.cid_ = (int)task.values_.get(0);
            t.tableName_ = task.tableName_;
            return t;
        }

        return this;
    }
}
