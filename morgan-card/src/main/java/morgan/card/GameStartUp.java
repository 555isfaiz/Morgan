package morgan.card;

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
	}
}
