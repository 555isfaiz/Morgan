package morgan.db;

import morgan.structure.Node;
import morgan.structure.Worker;
import morgan.support.Log;
import morgan.support.functions.Function1;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBCenter extends Worker {
    /*key: table name, value: DBWorker name*/
    private Map<String, String> dbworkers_ = new HashMap<>();
    public DBCenter(Node node, String name) {
        super(node, name);
        init();
    }

    private void init() {
        // assign tables to serveral dbworkers
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String dburl_ = "jdbc:mysql://localhost:3306/morgan?useSSL=false&serverTimezone=UTC&nullCatalogMeansCurrent=true";
            String usr_ = "root";
            String psw_ = "123456";
            Connection conn = DriverManager.getConnection(dburl_, usr_, psw_);
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"});
            Map<Integer, List<String>> assign = new HashMap<>();
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                int hash = Math.abs(tableName.hashCode());
                int index = hash % 5;
                dbworkers_.put(tableName, "DBWorker" + index);
                assign.compute(index, (i, l) -> {
                    if (l == null)
                        l = new ArrayList<>();
                    l.add(tableName);
                    return l;
                });
            }

            for (var e : assign.entrySet()) {
                DBWorker w = new DBWorker(getNode(), "DBWorker" + e.getKey());
                w.init(e.getValue());
                _node.addWorkerStandAlone(w);
            }

            conn.close();
        } catch (Exception e) {
            Log.db.error("init dbcenter failed, e:{}", e.getMessage());
        }
    }

    public void getAssignedWorker(String table) {
        String worker = dbworkers_.get(table);
        if (worker != null)
            returns(worker);
        returns("result", -1);
    }

    @Override
    public void registMethods() {
        _methodManager.registMethod("getAssignedWorker", (Function1<String>)this::getAssignedWorker, String.class);
    }
}
