package morgan.messages;

import java.util.HashMap;
import java.util.Map;

public interface IConstMessage {

	public MessageBase getEmptyMessageById(int id);

	public int getMessageId(MessageBase m);
}
