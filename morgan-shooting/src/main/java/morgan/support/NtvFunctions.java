package morgan.support;

import morgan.logic.Game;
import morgan.natives.INtvFunctions;
import morgan.natives.NtvGame;

public class NtvFunctions implements INtvFunctions {

    @Override
    public void log(int type, String msg, String file, int line){
        switch (type){
            case 0:
                Log.ntvLog.info(msg + " from={}:{}", file, line);
                break;

            case 1:
                Log.ntvLog.debug(msg + " from={}:{}", file, line);
                break;

            case 2:
                Log.ntvLog.warn(msg + " from={}:{}", file, line);
                break;

            case 3:
                Log.ntvLog.error(msg + " from={}:{}", file, line);
                break;
        }
    }

    @Override
    public void sendMsg(NtvGame ntvGame, int id, byte[] msg) {
        var game = (Game)ntvGame.getJvObj();
        game.sendMsgBytes(id, msg);
    }

    @Override
    public void sendMsgMulti(NtvGame ntvGame, int[] ids, byte[] msg) {
        var game = (Game)ntvGame.getJvObj();
//        InputStream input = new InputStream(msg);
//        MessageBase msg1 = input.read();
//        Log.node.info("msg sent from ntv, id:{}", msg1.msgId);
        game.sendMsgBytes(ids, msg);
    }
}
