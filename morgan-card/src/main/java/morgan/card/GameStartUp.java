package morgan.card;

import morgan.DBDefs.TestDB;
import morgan.connection.ConnStarter;
import morgan.db.DBManager;
import morgan.db.DBWorker;
import morgan.db.Table;
import morgan.structure.Node;
import morgan.support.Factory;

import java.util.List;

public class GameStartUp {
	public static void main(String[] args){
		Node node = new Node("morgan", "tcp://127.0.0.1:3320");
		ConnStarter.startUp(node);
		Factory.designateConfigClass(ConfigImpl.class);
		DBManager.initDB(node);
		node.startUp();
		node.anyWorker().schedule(100, () -> {
//			var test = new TestDB();
//			test.setId(3211);
//			test.setKey_one(22);
//			test.setKey_two("key_two");
//			test.setKey_four(false);
//			test.setKey_five(Long.MAX_VALUE);
//			test.setKey_six(Long.MIN_VALUE);
//			test.save();

			var tableName = TestDB.class.getAnnotation(Table.class).tableName();
			DBWorker.selectById_(DBManager.getAssignedWorkerId(tableName), tableName, 3211);
			DBWorker.Listen_((r) -> {
				List<morgan.db.Record> resultSet = r.getResult("result");
				for (var re : resultSet) {
					var testQ = new TestDB(re);
					testQ.remove();
				}
			});
		});
	}
}
