package morgan.db.tasks;

import morgan.db.DBTable;
import morgan.support.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBTaskInsertItem extends DBTask {

    public DBTaskInsertItem() {
        taskType_ = TASK_INSERT;
    }

    @Override
    public void beforeProcess(DBTable table) {
		this.table_ = table;
        if (labels_.size() != values_.size() || labels_.size() == 0) {
            Log.db.error("invalid label count and value count, label count:{}, value count:{}", labels_.size(), values_.size());
            return;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName_).append(" (");
        for (int i = 0; i < labels_.size(); i++) {
            sql.append(labels_.get(i));
            if (i != labels_.size() - 1)
                sql.append(", ");
        }
        sql.append(") VALUES (");

        for (int i = 0; i < values_.size(); i++) {
            sql.append("'").append(values_.get(i)).append("'");
            if (i != values_.size() - 1)
                sql.append(", ");
        }
        sql.append(");");

        sql_ = sql.toString();
    }

    @Override
    public void process(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql_);
        } catch (SQLException e) {
            Log.db.error("INSERT error, sql:{}, e:{}", sql_, e);
        }
    }

    @Override
    public DBTask merge(DBTask task) {
        if (task.taskType_ == TASK_QUERY || task.taskType_ == TASK_INSERT)
            throw new UnsupportedOperationException("unmergeable");

        if (task.taskType_ == TASK_REMOVE)
            return null;

        if (task.taskType_ == TASK_UPDATE) {
            for (int i = 0; i < task.labels_.size(); i++) {
                var l = task.labels_.get(i);
                if (!this.labels_.contains(l)) {
                    continue;
                }

                int index = this.labels_.indexOf(l);
                this.values_.set(index, task.values_.get(i));
            }
        }

        return this;
    }
}
