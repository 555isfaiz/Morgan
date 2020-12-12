package morgan.db.tasks;

import morgan.support.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBTaskUpdate extends DBTask {
    public DBTaskUpdate() {
        this.taskType_ = TASK_UPDATE;
    }

    @Override
    public void beforeProcess() {
        StringBuilder sql = new StringBuilder("UPDATE " + tableName_ + " SET ");
        for (int i = 0; i < labels_.size(); i++) {
            sql.append(labels_.get(i)).append(" = '").append(values_.get(i)).append("'");
            if (i != labels_.size() - 1)
                sql.append(",");
        }
        sql.append("WHERE id = ").append(cid_).append(";");
        sql_ = sql.toString();
    }

    @Override
    public void process(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql_);
        } catch (SQLException e) {
            Log.db.error("UPDATE error, sql:{}, e:{}", sql_, e);
        }
    }

    @Override
    public DBTask merge(DBTask task) {
        if (task.taskType_ == TASK_QUERY || task.taskType_ == TASK_INSERT)
            throw new UnsupportedOperationException("unmergeable");

        if (task.taskType_ == TASK_REMOVE)
            return task;

        if (task.taskType_ == TASK_UPDATE) {
            for (int i = 0; i < task.labels_.size(); i++) {
                var l = task.labels_.get(i);
                if (!this.labels_.contains(l)) {
                    labels_.add(task.labels_.get(i));
                    values_.add(task.values_.get(i));
                }

                int index = this.labels_.indexOf(l);
                this.values_.set(index, task.values_.get(i));
            }
        }

        return this;
    }
}
