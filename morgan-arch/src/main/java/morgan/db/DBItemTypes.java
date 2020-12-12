package morgan.db;

import java.sql.Types;

public enum DBItemTypes {
    TINYINT,
    SMALLINT,
    INT,
    BIGINT,
    FLOAT,
    DOUBLE,
    VARCHAR,
    BLOB,
    ;

    public static DBItemTypes getEnumFromType(int sqlType) {
        switch (sqlType) {
            case Types.TINYINT:
                return TINYINT;

            case Types.SMALLINT:
                return SMALLINT;

            case Types.BIGINT:
                return BIGINT;

            case Types.REAL:
                return FLOAT;

            case Types.FLOAT:
            case Types.DOUBLE:
                return DOUBLE;

            case Types.VARCHAR:
                return VARCHAR;

            case Types.BLOB:
                return BLOB;

            case Types.INTEGER:
            default:
                return INT;
        }
    }
}
