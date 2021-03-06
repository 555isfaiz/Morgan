package morgan.DBDefs;

import morgan.db.Column;
import morgan.db.DBBase;
import morgan.db.Record;
import morgan.db.Table;

@Table(tableName = "m_arch")
public class ArchDB extends DBBase {

	@Column(comments = "start time of the system")
	private long system_start_time;

	public ArchDB(Record record) {
		super(record);
		system_start_time = (long) record.values.get("system_start_time");
	}

	public ArchDB() {
		super("m_arch");
	}

	public long getSystem_start_time() {
		return system_start_time;
	}

	public void setSystem_start_time(long system_start_time) {
		this.system_start_time = system_start_time;
		onUpdate("system_start_time", system_start_time);
	}
}
