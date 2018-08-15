package dao;

import entity.Role;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcRoleDao extends AbstractJdbcDao implements RoleDao {

    private final String SQL_INSERT_QUERY = "INSERT INTO Role VALUES (?,?)";
    private final String SQL_UPDATE_QUERY = "UPDATE Role SET name=? WHERE id=?";
    private final String SQL_DELETE_QUERY = "DELETE FROM Role WHERE id=?";
    private final String SQL_SELECT_BY_NAME_QUERY = "SELECT * FROM Role WHERE name=?";
    private final String SQL_SELECT_ALL_QUERY = "SELECT * FROM Role";
    private final String SQL_SELECT_BY_ID = "SELECT * FROM Role WHERE id=?";

    public JdbcRoleDao(BasicDataSource basicDataSource, String dataSource) {
        super(basicDataSource, dataSource);
    }

    public JdbcRoleDao() {
    }

    @Override
    public void create(Role aRole) {
        Connection connection = null;
        try {
            connection = createConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT_QUERY);
            statement.setLong(1, aRole.getId());
            statement.setString(2, aRole.getName());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1.getSQLState(), e);
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
    public void update(Role aRole) {
        Connection connection = null;
        try {
            connection = createConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_QUERY);
            statement.setString(1, aRole.getName());
            statement.setLong(2, aRole.getId());
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1.getSQLState(), e);
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
    public void remove(Role aRole) {
        Connection connection = createConnection();
        try {
            PreparedStatement st = connection.prepareStatement(SQL_DELETE_QUERY);
            st.setLong(1, aRole.getId());
            st.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1.getSQLState(), e);
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
        Connection connection = null;
        try {
            connection = createConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_NAME_QUERY);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            Role aRole = null;
            if (resultSet.next()) {
                aRole = new Role();
                aRole.setId(resultSet.getLong("id"));
                aRole.setName(resultSet.getString("name"));
            }
            return aRole;
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

    public Role findById(java.lang.Long id) {
        Connection connection = null;
        try {
            connection = createConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_SELECT_BY_ID);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Role aRole = null;
            if (resultSet.next()) {
                aRole = new Role();
                aRole.setId(resultSet.getLong("id"));
                aRole.setName(resultSet.getString("name"));
            }
            return aRole;
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
        List<Role> aRoles = new ArrayList<>();
        Connection connection = createConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_QUERY);
            Role aRole;
            while (resultSet.next()) {
                aRole = new Role();
                aRole.setId(resultSet.getLong("id"));
                aRole.setName(resultSet.getString("name"));
                aRoles.add(aRole);
            }
            return aRoles;
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

}
