package morgan.card;

import morgan.support.Config;

public class ConfigImpl extends Config {

	public static final String CARD_CONFIG = "./morgan-card/config/card.config";

	@Override
	protected void fillOverride() {
		var pairs = read(CARD_CONFIG);
		for (var f : ConfigImpl.class.getFields()) {
			if (pairs.containsKey(f.getName()))
				fillField(f, pairs.get(f.getName()));
		}
	}
}
