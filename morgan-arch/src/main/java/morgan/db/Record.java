package morgan.db;

import morgan.structure.serialize.InputStream;
import morgan.structure.serialize.OutputStream;
import morgan.structure.serialize.Serializable;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedHashMap;
import java.util.Map;

public class Record implements Serializable {

    public Map<String, Object> values = new LinkedHashMap<>();
    public String table;

    public Record(DBItem item) {
        for (int i = 0; i < item.columnSize(); i++) {
            String label = item.table().getLabelByIndex(i);
            values.put(label, item.getColumn(i));
        }
    }

    public Record(ResultSet rs) {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String name = meta.getColumnName(i).intern();
                switch (DBItemTypes.getEnumFromType(meta.getColumnType(i))) {
                    case SMALLINT:
                    case TINYINT:
                    case INT:
                        values.put(name, rs.getInt(i));
                    case BIGINT:
                        values.put(name, rs.getLong(i));
                    case BLOB:
                        values.put(name, rs.getBytes(i));
                    case FLOAT:
                        values.put(name, rs.getFloat(i));
                    case DOUBLE:
                        values.put(name, rs.getDouble(i));
                    case VARCHAR:
                        values.put(name, rs.getString(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(values);
    }

    @Override
    public void readIn(InputStream in) throws IOException {
        values = in.read();
    }

    public String toString() {
        return values.toString();
    }

    public void free() {
		DBWorker.free_(DBManager.getAssignedWorkerId(table), table, (Integer) values.get("id"));
	}
}
