package morgan.db;

import morgan.structure.Node;
import morgan.structure.Worker;
import morgan.support.Config;
import morgan.support.Factory;
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
    private static final Map<String, String> dbworkers_ = new HashMap<>();

    public static void init(Node node) {
        // assign tables to serveral dbworkers
        try {
			Map<Integer, List<String>> assign = new HashMap<>();
			if (Factory.configInstance().DB_LOAD_FROM_META == 1)
				assign = loadFromDBMeta();
			else
				assign = loadFromDBConfig();
            for (var e : assign.entrySet()) {
                DBWorker w = new DBWorker(node, "DBWorker" + e.getKey());
                w.init(e.getValue());
                node.addWorkerStandAlone(w);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static Map<Integer, List<String>> loadFromDBMeta() {
    	Map<Integer, List<String>> assign = new HashMap<>();
    	try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection(Factory.configInstance().DB_URL, Factory.configInstance().DB_USER, Factory.configInstance().DB_PASSWORD);
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"});
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				int hash = Math.abs(tableName.hashCode());
				int index = hash % Factory.configInstance().DB_WORKER_NUM;
				dbworkers_.put(tableName, "DBWorker" + index);
				assign.compute(index, (i, l) -> {
					if (l == null)
						l = new ArrayList<>();
					l.add(tableName);
					return l;
				});
			}
			conn.close();
		} catch (Exception e) {
			Log.db.error("init DB center failed, e:{}", e.getMessage());
    		e.printStackTrace();
		}
		return assign;
	}

	public static Map<Integer, List<String>> loadFromDBConfig() {
		Map<Integer, List<String>> assign = new HashMap<>();
		try {
		} catch (Exception e) {
			Log.db.error("init DB center failed, e:{}", e.getMessage());
			e.printStackTrace();
		}
		return assign;
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
