package morgan.support;

import io.netty.channel.Channel;
import morgan.connection.AbstractConnection;
import morgan.messages.IConstMessage;
import morgan.structure.Node;
import morgan.structure.serialize.IConstDistrClass;

public class Factory {
	private static Class<? extends IConstMessage> messageMapClass_;

	private static Class<? extends IConstDistrClass> distrClassClz_;

	private static Class<? extends AbstractConnection> connectionClz_;

	private static Class<? extends Config> configClz_;

	private static IConstMessage messageMap_;

	private static IConstDistrClass distrClass_;

	private static Config config_;

	public static void designateConstMessage(Class<? extends IConstMessage> clazz) {
		Factory.messageMapClass_ = clazz;
	}

	public static void designateConstDistrClass(Class<? extends IConstDistrClass> clazz) {
		Factory.distrClassClz_ = clazz;
	}

	public static void designateConnectionClass(Class<? extends AbstractConnection> clazz) {
		Factory.connectionClz_ = clazz;
	}

	public static void designateConfigClass(Class<? extends Config> clazz) {
		Factory.configClz_ = clazz;
	}

	public static IConstMessage messageMapInstance(){
		if (messageMapClass_ == null) {
			throw new NullPointerException("message map not set");
		}

		if (messageMap_ == null) {
			synchronized (Factory.class) {
				if (messageMap_ == null) {
					try {
						messageMap_ = messageMapClass_.getConstructor().newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return messageMap_;
	}

	public static IConstDistrClass distrClassInstance(){
		if (distrClassClz_ == null) {
			throw new NullPointerException("distr class map not set");
		}

		if (distrClass_ == null) {
			synchronized (Factory.class) {
				if (distrClass_ == null) {
					try {
						distrClass_ = distrClassClz_.getConstructor().newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return distrClass_;
	}

	public static Config configInstance() {
		if (config_ == null) {
			synchronized (Factory.class) {
				if (config_ == null) {
					try {
						if (configClz_ == null)
							config_ = new Config() {
								@Override
								protected void fillOverride() {}
							};
						else
							config_ = configClz_.getConstructor().newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
					config_.fill();
				}
			}
		}
		return config_;
	}

	public static AbstractConnection newConnectionInstance(Node node, Channel channel, int connId) {
		if (connectionClz_ == null) {
			throw new NullPointerException("connection class not set");
		}

		AbstractConnection connection = null;
		try {
			connection = connectionClz_.getConstructor(Node.class, Channel.class, int.class).newInstance(node, channel, connId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
}
