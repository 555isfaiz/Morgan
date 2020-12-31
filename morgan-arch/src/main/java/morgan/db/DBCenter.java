package morgan.db;

import morgan.structure.Node;
import morgan.structure.Worker;
import morgan.support.Config;
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

public class DBCenter {
    /*key: table name, value: DBWorker name*/
    private static Map<String, String> dbworkers_ = new HashMap<>();

    public static void init(Node node) {
        // assign tables to serveral dbworkers
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(Config.MAIN_CONFIG_INST.DB_URL, Config.MAIN_CONFIG_INST.DB_USER, Config.MAIN_CONFIG_INST.DB_PASSWORD);
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"});
            Map<Integer, List<String>> assign = new HashMap<>();
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                int hash = Math.abs(tableName.hashCode());
                int index = hash % Config.MAIN_CONFIG_INST.DB_WORKER_NUM;
                dbworkers_.put(tableName, "DBWorker" + index);
                assign.compute(index, (i, l) -> {
                    if (l == null)
                        l = new ArrayList<>();
                    l.add(tableName);
                    return l;
                });
            }

            for (var e : assign.entrySet()) {
                DBWorker w = new DBWorker(node, "DBWorker" + e.getKey());
                w.init(e.getValue());
                node.addWorkerStandAlone(w);
            }

            conn.close();
        } catch (Exception e) {
            Log.db.error("init DB center failed, e:{}", e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getAssignedWorker(String table) {
        String worker = dbworkers_.get(table);
        if (worker != null)
            return worker;
        return "";
    }

    public static int getAssignedWorkerId(String table) {
		String worker = dbworkers_.get(table);
		if (worker != null)
			return Integer.parseInt(worker.split("-")[1]);
		return -99;
	}
}
