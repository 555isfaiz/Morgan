package morgan.card;

import morgan.connection.ConnStarter;
import morgan.structure.Node;

public class GameStartUp {
	public static void main(String[] args){
		Node node = new Node("morgan", "tcp://127.0.0.1:3320");
		ConnStarter.startUp(node);
		node.startUp();
	}
}
