package morgan.DBDefs;

import morgan.db.Column;
import morgan.db.DBBase;
import morgan.db.Record;
import morgan.db.Table;

@Table(tableName = "table_one")
public class TestDB extends DBBase {
	@Column
	private int key_one;

	@Column
	private String key_two;

	@Column(comments = "test2")
	private boolean key_four;

	@Column(comments = "test3")
	private long key_five;

	@Column(defaults = "11")
	private long key_six;

	public TestDB(Record record) {
		super(record);
		key_one = (int) record.values.get("key_one");
		key_two = (String) record.values.get("key_two");
		key_four = (boolean) record.values.get("key_four");
		key_five = (long) record.values.get("key_five");
		key_six = (long) record.values.get("key_six");
	}

	public TestDB() {
		super("table_one");
	}

	public int getKey_one() {
		return key_one;
	}

	public void setKey_one(int key_one) {
		this.key_one = key_one;
		onUpdate("key_one", key_one);
	}

	public String getKey_two() {
		return key_two;
	}

	public void setKey_two(String key_two) {
		this.key_two = key_two;
		onUpdate("key_two", key_two);
	}

	public boolean isKey_four() {
		return key_four;
	}

	public void setKey_four(boolean key_four) {
		this.key_four = key_four;
		onUpdate("key_four", key_four);
	}

	public long getKey_five() {
		return key_five;
	}

	public void setKey_five(long key_five) {
		this.key_five = key_five;
		onUpdate("key_five", key_five);
	}

	public long getKey_six() {
		return key_six;
	}

	public void setKey_six(long key_six) {
		this.key_six = key_six;
		onUpdate("key_six", key_six);
	}
}
