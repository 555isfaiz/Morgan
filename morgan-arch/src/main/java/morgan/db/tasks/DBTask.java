package morgan.db.tasks;

import morgan.db.DBTable;
import morgan.db.Record;
import morgan.structure.Call;
import morgan.support.functions.Function2;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public abstract class DBTask {

    public static int TASK_INSERT = 1;

    public static int TASK_REMOVE = 2;

    public static int TASK_UPDATE = 3;

    public static int TASK_QUERY = 4;

    public String tableName_ = null;

    public DBTable table_;

    public long cid_;

    public List<String> labels_ = new ArrayList<>();

    public List<Object> values_ = new ArrayList<>();

    public List<Integer> affectedIndexs_ = new ArrayList<>();

    public String sql_ = null;

    public int taskType_;

    public Call queryCall_;

    public Function2<List<Record>, Call> queryCallBack_;

    public abstract void beforeProcess(DBTable table);

    public abstract void process(Connection conn);

    public abstract DBTask merge(DBTask task);

}
