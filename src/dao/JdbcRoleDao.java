package dao;

import entity.Role;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class JdbcRoleDao extends AbstractJdbcDao implements RoleDao {

    private final String SQL_INSERT_QUERY = "INSERT INTO Role VALUES (?,?)";
    private final String SQL_UPDATE_QUERY = "UPDATE Role SET name=? WHERE id=?";
    private final String SQL_DELETE_QUERY = "DELETE FROM Role WHERE id=?";
    private final String SQL_SELECT_BY_NAME_QUERY = "SELECT * FROM Role WHERE name=?";
    private final String SQL_SELECT_ALL_QUERY = "SELECT * FROM Role";
    private final String SQL_SELECT_BY_ID = "SELECT * FROM Role WHERE id=?";
    private final String SQL_CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS Role(" +
            "id BIGINT primary key, name varchar(255));";
    private final String SQL_DELETE_TABLE_QUERY = "DROP TABLE Role;";

    private String dataSource = "h2";
    private BasicDataSource basicDataSource = null;

    public JdbcRoleDao() {
        try {
            createTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public BasicDataSource getBasicDataSource() {
        return basicDataSource;
    }

    public void setBasicDataSource(BasicDataSource basicDataSource) {
        this.basicDataSource = basicDataSource;
    }

    @Override
    public void create(Role role) {
        if (role == null) {
            throw new NullPointerException();
        }
        Connection connection = null;
        try {
            connection = createConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT_QUERY);
            statement.setLong(1, role.getId());
            statement.setString(2, role.getName());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void update(Role role) {
        if (role == null) {
            throw new NullPointerException();
        }
        Connection connection = null;
        try {
            connection = createConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_QUERY);
            statement.setString(1, role.getName());
            statement.setLong(2, role.getId());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void remove(Role role) {
        if (role == null) {
            throw new NullPointerException();
        }
        Connection connection = createConnection();
        try {
            PreparedStatement st = connection.prepareStatement(SQL_DELETE_QUERY);
            st.setLong(1, role.getId());
            st.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e.getSQLState());
            }
        }
    }

    @Override
    public Role findByName(String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        Connection connection = null;
        try {
            connection = createConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_NAME_QUERY);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            Role role = null;
            if (resultSet.next()) {
                role = new Role();
                role.setId(resultSet.getLong("id"));
                role.setName(resultSet.getString("name"));
            }
            return role;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Role findById(Long id) {
        Connection connection = null;
        try {
            connection = createConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Role role = null;
            if (resultSet.next()) {
                role = new Role();
                role.setId(resultSet.getLong("id"));
                role.setName(resultSet.getString("name"));
            }
            return role;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        Connection connection = createConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_QUERY);
            Role role;
            while (resultSet.next()) {
                role = new Role();
                role.setId(resultSet.getLong("id"));
                role.setName(resultSet.getString("name"));
                roles.add(role);
            }
            return roles;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    Connection createConnection() {
        if (basicDataSource == null) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(dataSource);
            basicDataSource = new BasicDataSource();
            basicDataSource.setUrl(resourceBundle.getString("jdbc.url"));
            basicDataSource.setUsername(resourceBundle.getString("jdbc.username"));
            basicDataSource.setPassword(resourceBundle.getString("jdbc.password"));
        }
        Connection connection;
        try {
            connection = basicDataSource.getConnection();
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTable() throws SQLException {
        Connection connection = createConnection();
        try {
            Statement statement = connection.createStatement();
            statement.execute(SQL_CREATE_TABLE_QUERY);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.close();
        }
    }

    public void deleteTable() throws SQLException {
        Connection connection = createConnection();
        try {
            Statement statement = connection.createStatement();
            statement.execute(SQL_DELETE_TABLE_QUERY);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        } finally {
            connection.close();
        }
    }

}
