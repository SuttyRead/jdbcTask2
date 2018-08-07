package dao;

import entity.User;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class JdbcUserDao extends AbstractJdbcDao implements UserDao {

    private final String SQL_INSERT_QUERY = "INSERT INTO User VALUES (?,?,?,?,?,?,?,?)";
    private final String SQL_UPDATE_QUERY = "UPDATE User SET login=?, password=?, email=?, firstname=?," +
            " lastname=?, birthday=?, roleId=? WHERE id=?";
    private final String SQL_DELETE_QUERY = "DELETE FROM User WHERE id=?";
    private final String SQL_SELECT_ALL_QUERY = "SELECT * FROM User";
    private final String SQL_SELECT_BY_LOGIN_QUERY = "SELECT * FROM User WHERE login=?";
    private final String SQL_SELECT_BY_EMAIL_QUERY = "SELECT * FROM User WHERE email=?";
    private final String SQL_CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS User(id BIGINT primary key," +
            " login varchar(255), password varchar(255), email varchar(255), firstname varchar(255)," +
            " lastname varchar(255), birthday DATE, roleId BIGINT);";
    private final String SQL_DELETE_TABLE_QUERY = "DROP TABLE User;";

    private String dataSource = "h2";
    private BasicDataSource basicDatSource = null;

    public JdbcUserDao() {
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
        if (dataSource == null) {
            throw new NullPointerException();
        } else {
            this.dataSource = dataSource;
        }
    }

    public BasicDataSource getBasicDatSource() {
        return basicDatSource;
    }

    public void setBasicDatSource(BasicDataSource basicDatSource) {
        if (basicDatSource == null) {
            throw new NullPointerException();
        } else {
            this.basicDatSource = basicDatSource;
        }
    }

    @Override
    public void create(User user) {
        if (user == null) {
            throw new NullPointerException();
        }
        Connection connection = null;
        try {
            connection = createConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT_QUERY);
            statement.setLong(1, user.getId());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getFirstName());
            statement.setString(6, user.getLastName());
            statement.setDate(7, (Date) user.getBirthday());
            statement.setLong(8, user.getRole().getId());
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
    public void update(User user) {
        if (user == null) {
            throw new NullPointerException();
        }
        Connection connection = null;
        try {
            connection = createConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_QUERY);
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getFirstName());
            statement.setString(5, user.getLastName());
            statement.setDate(6, (Date) user.getBirthday());
            statement.setLong(7, user.getRole().getId());
            statement.setLong(8, user.getId());
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
    public void remove(User user) {
        if (user == null) {
            throw new NullPointerException();
        }
        Connection connection = null;
        try {
            connection = createConnection();
            PreparedStatement statement = connection.prepareStatement(SQL_DELETE_QUERY);
            statement.setLong(1, user.getId());
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
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        Connection connection = null;
        try {
            connection = createConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_QUERY);
            User user;
            JdbcRoleDao roleDao = new JdbcRoleDao();
            while (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getLong("id"));
                user.setLogin(resultSet.getString("login"));
                user.setPassword(resultSet.getString("password"));
                user.setEmail(resultSet.getString("email"));
                user.setFirstName(resultSet.getString("firstname"));
                user.setLastName(resultSet.getString("lastname"));
                user.setBirthday(resultSet.getDate("birthday"));
                user.setRole(roleDao.findById(resultSet.getLong("roleId")));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return users;
    }

    @Override
    public User findByLogin(String login) {
        if (login == null) {
            throw new NullPointerException();
        }
        return findBySomething(login, SQL_SELECT_BY_LOGIN_QUERY);
    }

    @Override
    public User findByEmail(String email) {
        if (email == null) {
            throw new NullPointerException();
        }
        return findBySomething(email, SQL_SELECT_BY_EMAIL_QUERY);
    }

    @Override
    Connection createConnection() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(dataSource);
        if (basicDatSource == null) {
            basicDatSource = new BasicDataSource();
            basicDatSource.setUrl(resourceBundle.getString("jdbc.url"));
            basicDatSource.setUsername(resourceBundle.getString("jdbc.username"));
            basicDatSource.setPassword(resourceBundle.getString("jdbc.password"));
        }
        Connection connection;
        try {
            connection = basicDatSource.getConnection();
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e.getSQLState());
        }
    }

    public void createTable() throws SQLException {
        Connection connection = null;
        try {
            connection = createConnection();
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
        Connection connection = null;
        try {
            connection = createConnection();
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

    private User findBySomething(String parameter, String query) {
        Connection connection = null;
        try {
            connection = createConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, parameter);
            ResultSet resultSet = statement.executeQuery();
            User user = null;
            JdbcRoleDao roleDao = new JdbcRoleDao();
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getLong("id"));
                user.setLogin(resultSet.getString("login"));
                user.setPassword(resultSet.getString("password"));
                user.setEmail(resultSet.getString("email"));
                user.setFirstName(resultSet.getString("firstname"));
                user.setLastName(resultSet.getString("lastname"));
                user.setBirthday(resultSet.getDate("birthday"));
                user.setRole(roleDao.findById(resultSet.getLong("roleId")));
            }
            return user;
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
