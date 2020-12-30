package morgan.connection;

import morgan.structure.Node;

public class ConnectionStartUp extends ConnStarter{
	public static void main(String[] args) {
		Node node = new Node("Conn", "tcp://127.0.0.1:3330");
		node.startUp();
		startUp(node);
	}
}
