package morgan.card;

import morgan.DBDefs.TestDB;
import morgan.connection.ConnStarter;
import morgan.db.DBManager;
import morgan.structure.Node;
import morgan.support.Factory;

public class GameStartUp {
	public static void main(String[] args){
		Node node = new Node("morgan", "tcp://127.0.0.1:3320");
		ConnStarter.startUp(node);
		Factory.designateConfigClass(ConfigImpl.class);
		DBManager.initDB(node);
		node.startUp();
		node.anyWorker().schedule(100, () -> {
			var test = new TestDB();
			test.setId(42123);
			test.setKey_one(111);
			test.setKey_two("this is key_two");
			test.setKey_four(true);
			test.setKey_five(Long.MAX_VALUE);
			test.setKey_six(Long.MIN_VALUE);
			test.save();
		});
	}
}
