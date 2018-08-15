package dao;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public abstract class AbstractJdbcDao {

    private BasicDataSource basicDataSource = null;
    private String dataSource = "h2";

    public AbstractJdbcDao(BasicDataSource basicDataSource, String dataSource) {
        this.basicDataSource = basicDataSource;
        this.dataSource = dataSource;
    }

    public AbstractJdbcDao() {
    }

    public BasicDataSource getBasicDataSource() {
        return basicDataSource;
    }

    public void setBasicDataSource(BasicDataSource basicDataSource) {
        this.basicDataSource = basicDataSource;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Connection createConnection() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(dataSource);
        if (basicDataSource == null) {
            basicDataSource = new BasicDataSource();
            basicDataSource.setUrl(resourceBundle.getString("jdbc.url"));
            basicDataSource.setUsername(resourceBundle.getString("jdbc.username"));
            basicDataSource.setPassword(resourceBundle.getString("jdbc.password"));
        }
        Connection connection;
        try {
            Class.forName(resourceBundle.getString("jdbc.driver-class-name")).newInstance();
            connection = basicDataSource.getConnection();
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState());
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
