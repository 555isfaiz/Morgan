package morgan.messages;

import java.util.HashMap;
import java.util.Map;

public class ConstMessage implements IConstMessage {

    private static final Map<Class<? extends MessageBase>, Integer> classToId = new HashMap<>();

    static {
        initIdToClass();
    }

    private static void initIdToClass() {
        classToId.put(BVector2.class, 102);
        classToId.put(BVector3.class, 103);
        classToId.put(BPlayer.class, 101);
        classToId.put(CSLogin.class, 1001);
        classToId.put(SCJoinGame.class, 1003);
        classToId.put(SCLogin.class, 1004);
        classToId.put(SCGameSync.class, 1005);
        classToId.put(CSMove.class, 2001);
        classToId.put(SCMove.class, 2002);
        classToId.put(CSJump.class, 2003);
        classToId.put(SCJump.class, 2004);
        classToId.put(CSDash.class, 2005);
        classToId.put(SCDashStart.class, 2006);
        classToId.put(SCDashStop.class, 2007);

    }

    public MessageBase getEmptyMessageById(int id) {
        switch (id){
            case 102:
                return new BVector2();
            case 103:
                return new BVector3();
            case 101:
                return new BPlayer();
            case 1001:
                return new CSLogin();
            case 1003:
                return new SCJoinGame();
            case 1004:
                return new SCLogin();
            case 1005:
                return new SCGameSync();
            case 2001:
                return new CSMove();
            case 2002:
                return new SCMove();
            case 2003:
                return new CSJump();
            case 2004:
                return new SCJump();
            case 2005:
                return new CSDash();
            case 2006:
                return new SCDashStart();
            case 2007:
                return new SCDashStop();

        }
        return null;
    }

    public int getMessageId(MessageBase m) {
        return classToId.get(m.getClass());
    }
}