package morgan.support;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Config {
	public static Config MAIN_CONFIG_INST;

	public static final String MAIN_CONFIG = "./config/main.config";

	public final String DB_URL = null;

	public final String DB_USER = null;

	public final String DB_PASSWORD = null;

	static {
		fill();
	}

	protected static Map<String, String> read(String fileName) {
		Map<String, String> res = new HashMap<>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			while (in.ready()) {
				String s = in.readLine();
				if (s.charAt(0) != '$')
					continue;

				s = s.replace("$", "").replace(" ", "");
				int equal = s.indexOf("=");
				res.put(s.substring(0, equal), s.substring(equal + 1));
			}
			in.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	protected static void fillField(Field f, Config obj, String val) {
		try {
			f.setAccessible(true);
			var type = f.getType();
			if (type == Integer.class)
				f.set(obj, Integer.valueOf(val));
			else if (type == Long.class)
				f.set(obj, Long.valueOf(val));
			else if (type == Double.class)
				f.set(obj, Double.valueOf(val));
			else if (type == Float.class)
				f.set(obj, Float.valueOf(val));
			else if (type == Boolean.class)
				f.set(obj, Boolean.valueOf(val));
			else
				f.set(obj, val);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void fill() {
		Config inst = new Config();
		var pairs = read(MAIN_CONFIG);
		for (var f : Config.class.getDeclaredFields()) {
			if (pairs.containsKey(f.getName()))
				fillField(f, inst, pairs.get(f.getName()));
		}
		fillOverride();
		MAIN_CONFIG_INST = inst;
	}

	protected static void fillOverride() {
	}
}
