package morgan.db;

import morgan.db.tasks.DBTask;
import morgan.db.tasks.DBTaskQueryWithBinds;
import morgan.db.tasks.DBTaskQueryWithId;
import morgan.structure.Call;
import morgan.support.Config;
import morgan.support.Factory;
import morgan.support.Log;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class DBTable {
    private Map<Integer, DBItem> items_ = new HashMap<>();
    private Map<String, DBItemTypes> columns_ = new LinkedHashMap<>(); //keep order
    private List<Integer> uniqueIndexs_ = new ArrayList<>();
    private Map<Integer, LinkedBlockingQueue<DBTask>> tasks_ = new HashMap<>();
    private String name_;
    private DBWorker worker_;

    public DBTable(DBWorker worker, String tableName, ResultSet tableInfo, ResultSet columns) {
        worker_ = worker;
        try {
            ResultSetMetaData meta = columns.getMetaData();

            //set labels and types
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String name = meta.getColumnName(i).intern();
                columns_.put(name, DBItemTypes.getEnumFromType(meta.getColumnType(i)));
            }

            //unique index
            while (tableInfo.next()) {
                String indexName = tableInfo.getString("COLUMN_NAME");
                uniqueIndexs_.add(getIndexByLabel(indexName));
            }

            //load items
//            loadItems(columns);

            name_ = tableName;

        } catch (Exception e) {
            Log.db.error("error creating table! name:{}, e:{}", tableName, e);
        }
        Log.db.info("Table inited! tableName:{}, columns:{}, uniqueIndexs:{}, itemNum:{}", name_, columns_, uniqueIndexs_, items_.size());
    }

    public void pulse() {
        mergeAndCommit();
    }

//    public void loadItems(ResultSet rs) throws SQLException {
//        while (rs.next()) {
//            DBItem item = new DBItem();
//            int id = 0;
//            for (var e : columns_.entrySet()) {
//                if (e.getValue() == DBItemTypes.INT){
//                    int intVal = rs.getInt(e.getKey());
//                    item.addColumn(intVal);
//                    if (id == 0)
//                        // record id
//                        id = intVal;
//                }
//
//                else if (e.getValue() == DBItemTypes.DOUBLE)
//                    item.addColumn(rs.getDouble(e.getKey()));
//
//                else if (e.getValue() == DBItemTypes.FLOAT)
//                    item.addColumn(rs.getFloat(e.getKey()));
//
//                else if (e.getValue() == DBItemTypes.BIGINT)
//                    item.addColumn(rs.getLong(e.getKey()));
//
//                else if (e.getValue() == DBItemTypes.VARCHAR)
//                    item.addColumn(rs.getString(e.getKey()));
//
//                //...fill this in the future
//
//                else
//                    Log.db.error("unknown column type:{}, label:{}", e.getValue(), e.getKey());
//            }
//            item.table(this);
//            items_.put(id, item);
//        }
//    }

	public void addItem(DBItem item) {
    	items_.put((Integer) item.getColumn(0), item);
	}

    public int insert(Map<String, Object> values) {
        for (var e : columns_.entrySet()) {
            if (values.containsKey(e.getKey())){
                int index = getIndexByLabel(e.getKey());
                if (uniqueIndexs_.contains(index)) {
                    for (var i : items_.values()) {
                        if (i.getColumn(index).equals(values.get(e.getKey()))) {
                            Log.db.warn("item unique column collied! item:{}", values);
                            return -1;
                        }
                    }
                }
            } else {
                values.put(e.getKey(), getDefaultValueByType(e.getValue()));
            }
        }

        DBItem i = new DBItem();
        for (var v : values.values()) {
            i.addColumn(v);
        }
        i.table(this);
        int cid = (Integer)i.getColumn(0);
        items_.put(cid, i);

        addTask(cid, i.onInsert());

        Log.db.info("item inserted! table:{}, item:{}", name_, values);

        return 0;
    }

    public void update(int cid, List<String> labels, List<Object> values) {
        var item = items_.get(cid);
        if (item == null)
            return;
        List<String> allLabel = new ArrayList<>(columns_.keySet());
        List<Integer> index = new ArrayList<>();
        for (var l : labels) {
            index.add(allLabel.indexOf(l));
        }

        addTask(cid, item.onUpdate(index, values));
    }

    public void remove(int cid) {
        var i = items_.remove(cid);
        if (i == null)
            return;

        addTask(cid, i.onRemove());

        Log.db.info("item removed! table:{}, cid:{}", name_, cid);
    }

    public void query(Call queryCall, int cid) {
        var item = items_.get(cid);
        if (item == null) {
            DBTask task = new DBTaskQueryWithBinds();
			task.queryCall_ = queryCall;
			task.labels_.add("id");
			task.values_.add(cid);
			task.tableName_ = name_;
            addTask(cid, task);
            return;
        }

        addTask(cid, item.onQuery(queryCall));
    }

    public void query(Call queryCall, String label, Object value) {
        //should wait for all operations done, then execute sql directly
        mergeAndCommit();

        DBTask taks = new DBTaskQueryWithBinds();
        taks.queryCall_ = queryCall;
        taks.labels_.add(label);
        taks.values_.add(value);
        taks.tableName_ = name_;
        worker_.addTask(name_, taks);
    }

    public void free(int cid) {
		if (!tasks_.isEmpty())
			mergeAndCommit();

		items_.remove(cid);
	}

    public void addTask(int cid, DBTask task) {
        task.tableName_ = name_;
        //set labels
        List<String> labels = new ArrayList<>(columns_.keySet());
        for (var i : task.affectedIndexs_) {
            task.labels_.add(labels.get(i));
        }

        if (tasks_.containsKey(cid))
            tasks_.get(cid).add(task);
        else {
            var q = new LinkedBlockingQueue<DBTask>();
            q.add(task);
            tasks_.put(cid, q);
        }
    }

    private void flushCache() {

    }

    private void mergeAndCommit() {
        for (var e : tasks_.entrySet()) {
            var q = e.getValue();
//            if (q.size() >= 5)
//                System.out.println("now");
            if(q.size() >= 1) {
                DBTask t = q.poll();
                int size = Math.min(q.size(), Factory.configInstance().DB_MERGE_LIMIT);
                for (int i = 0; i < size; i++) {
                    try {
                        t = t.merge(q.peek());
                    } catch (UnsupportedOperationException ex) {
                        break;
                    }
                    q.poll();
                }
                if (t == null)
                    continue;
                t.beforeProcess(this);
                worker_.addTask(name_, t);
            }
        }
    }

    private Object getDefaultValueByType(DBItemTypes type) {
        switch (type) {
            case FLOAT:
                return 0.0f;

            case DOUBLE:
                return 0.0d;

            case VARCHAR:
            case BLOB:
                return "";

            case BIGINT:
                return 0L;

            case INT:
            case TINYINT:
            case SMALLINT:
            default:
                return 0;
        }
    }

    public DBItemTypes getColumnTypeByIndex(int index) {
        return new ArrayList<>(columns_.values()).get(index);
    }

    public int getIndexByLabel(String label) {
        return new ArrayList<>(columns_.keySet()).indexOf(label);
    }

    public String getLabelByIndex(int index) {
        return new ArrayList<>(columns_.keySet()).get(index);
    }

    public void name(String name) {
        name_ = name;
    }

    public String name() {
        return name_;
    }

    public void worker(DBWorker worker) {
        worker_ = worker;
    }

    public DBWorker worker() {
        return worker_;
    }
}
