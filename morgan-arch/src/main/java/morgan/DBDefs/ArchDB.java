package morgan.DBDefs;

import morgan.db.Column;
import morgan.db.Table;

@Table(tableName = "m_arch")
public class ArchDB {

	@Column(comments = "start time of the system")
	private long system_start_time;
}
