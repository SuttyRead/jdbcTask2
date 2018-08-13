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
    public void create(Role role) {
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver").newInstance();
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
                throw new RuntimeException(e1.getSQLState(), e);
            }
            throw new RuntimeException(e);
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
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
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver").newInstance();
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
                throw new RuntimeException(e1.getSQLState(), e);
            }
            throw new RuntimeException(e);
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
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
        Connection connection = createConnection();
        try {
            Class.forName("org.h2.Driver").newInstance();
            PreparedStatement st = connection.prepareStatement(SQL_DELETE_QUERY);
            st.setLong(1, role.getId());
            st.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException(e1.getSQLState(), e);
            }
            throw new RuntimeException(e);
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
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
            Class.forName("org.h2.Driver").newInstance();
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
        } catch (SQLException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
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
            Class.forName("org.h2.Driver").newInstance();
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
        } catch (SQLException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
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
            Class.forName("org.h2.Driver").newInstance();
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
        } catch (SQLException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
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
