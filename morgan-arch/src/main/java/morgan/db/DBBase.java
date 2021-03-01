package morgan.db;

import morgan.support.Log;

import java.util.List;

public abstract class DBBase {

	private Record originalRecord_;
	private int assignedWorkerId;

	public DBBase(Record record) {
		originalRecord_ = record;
		assignedWorkerId = DBManager.getAssignedWorkerId(record.table);
	}

	public DBBase(String tableName) {
		int workerId = DBManager.getAssignedWorkerId(tableName);
		if (workerId == -99) {
			throw new UnsupportedOperationException("table " + tableName + " doesn't exist");
		}

		originalRecord_ = new Record(tableName);
		assignedWorkerId = workerId;
	}

	public void save() {
		if (originalRecord_.persisted)
			return;

		DBWorker.insert_(assignedWorkerId, originalRecord_.table, originalRecord_.values);
		DBWorker.Listen_((r) -> {
			int code = r.getResult("code");
			if (code != 0) {
				Log.db.error("error inserting item of table {}, item id:{}", originalRecord_.table, originalRecord_.values.get("id"));
				return;
			}

			originalRecord_.persisted = true;
		});
	}

	protected void onUpdate(String column, Object value) {
		originalRecord_.values.put(column, value);
		if (!originalRecord_.persisted)
			return;

		DBWorker.update_(assignedWorkerId, originalRecord_.table, (Integer) originalRecord_.values.get("id"), List.of(column), List.of(value));
	}

	public int getId() {
		return (int) originalRecord_.values.get("id");
	}

	public void setId(int id) {
		originalRecord_.values.put("id", id);
		onUpdate("id", id);
	}

	public void remove() {
		DBWorker.remove_(assignedWorkerId, originalRecord_.table, (Integer) originalRecord_.values.get("id"));
	}

	public void free() {
		DBWorker.free_(assignedWorkerId, originalRecord_.table, (Integer) originalRecord_.values.get("id"));
	}
}
