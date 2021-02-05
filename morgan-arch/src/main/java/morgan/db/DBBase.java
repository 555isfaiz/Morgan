package morgan.db;

public abstract class DBBase {

	private Record originalRecord_ = new Record();

	public void save() {
		if (originalRecord_.persisted)
			return;
	}

	public void onUpdate(String column, Object value) {
		if (!originalRecord_.persisted)
			return;
	}

	public void remove() {
		DBWorker.free_(DBManager.getAssignedWorkerId(originalRecord_.table), originalRecord_.table, (Integer) originalRecord_.values.get("id"));
	}
}
