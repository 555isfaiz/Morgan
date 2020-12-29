package morgan.logic;

import morgan.connection.ConnStarter;
import morgan.messages.ConstMessage;
import morgan.natives.NtvManager;
import morgan.structure.Node;
import morgan.structure.Worker;
import morgan.support.ConstDistrClass;
import morgan.support.Factory;
import morgan.support.NtvFunctions;

public class GameStart {
    static {
        System.load("E:/work/java/Morgan/morgan-shooting/src/main/resources/nativeshooting.dll");
//        System.load("E:/work/java/Morgan/shooting/build/libnativeshooting.dll");
    }
    public static void main(String[] args){
        Node node = new Node("morgan", "tcp://127.0.0.1:3320");
        Worker gamemanager = new GameManager(node);
        node.addWorker(gamemanager);
        Worker lobby = new Lobby(node);
        node.addWorker(lobby);
        Worker global = new GlobalPlayerManager(node);
        node.addWorker(global);
		Factory.designateConstMessage(ConstMessage.class);
		Factory.designateConstDistrClass(ConstDistrClass.class);
		Factory.designateConnectionClass(Connection.class);
        ConnStarter.startUp(node);
        NtvManager.setNtvFunctions(new NtvFunctions());
        NtvManager.loadMeshes("./objs/Player.obj");
        node.startUp();
        lobby.schdule(1000, () -> GlobalPlayerManager.playerLogout_(10));
    }
}
