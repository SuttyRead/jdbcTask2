package dao;

import java.sql.Connection;

public abstract class AbstractJdbcDao {

    abstract Connection createConnection();

}
