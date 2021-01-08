package morgan.DBDefs;

import morgan.db.Column;
import morgan.db.Table;

@Table(tableName = "table_one")
public class TestDB {
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
}
