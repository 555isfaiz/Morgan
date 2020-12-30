package morgan.messages;

import java.util.HashMap;
import java.util.Map;

public class ConstMessage implements IConstMessage {

    private static final Map<Class<? extends MessageBase>, Integer> classToId = new HashMap<>();

    static {
        initIdToClass();
    }

    private static void initIdToClass() {

    }

    public MessageBase getEmptyMessageById(int id) {
        switch (id){

        }
        return null;
    }

    public int getMessageId(MessageBase m) {
        return classToId.get(m.getClass());
    }
}