package com.ua.sutty.jdbc.repository.impl;

import com.ua.sutty.jdbc.domain.Role;
import com.ua.sutty.jdbc.domain.User;
import com.ua.sutty.jdbc.repository.AbstractJdbcDao;
import com.ua.sutty.jdbc.repository.RoleDao;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcRoleDao extends AbstractJdbcDao implements RoleDao {

    private Connection connection;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    private final String SQL_SELECT_ALL_QUERY = "SELECT * FROM Role";
    private final String SQL_SELECT_BY_ID = "SELECT * FROM Role WHERE id=?";

    private static final String INSERT_ROLE = String.format("INSERT INTO Role(%s)" +
        " VALUES (?);", Role.NAME);

    private static final String DELETE_ROLE = String.format("DELETE FROM Role WHERE %s = ?;", Role.ID);

    private static final String DELETE_USER_WITH_ROLE = String.format("DELETE FROM User WHERE %s = ?;", User.ROLE_ID);

    private static final String GET_ROLE_BY_NAME = "SELECT * FROM Role WHERE name = ?";

    private static final String UPDATE_ROLE = String.format("UPDATE Role SET %s = ?" +
        "WHERE id = ?", Role.NAME);

    public JdbcRoleDao(BasicDataSource basicDataSource, String dataSource) {
        super(basicDataSource, dataSource);
    }

    public JdbcRoleDao() {
    }

    @Override
    public void create(Role role) {
        if (role == null){
            throw new NullPointerException();
        }
        try {
            Role roleInBase = findByName(role.getName());
            connection = super.createConnection();
            if (roleInBase.getName() != null) {
                if (roleInBase.getName().equals(role.getName())) {
                    System.out.println("This role already exist");
                    return;
                }
            }
            PreparedStatement pst = connection.prepareStatement(INSERT_ROLE);
            pst.setString(1, role.getName());
            pst.execute();
            connection.commit();
        } catch (SQLException e) {
            rollBackTransactional(connection);
            e.printStackTrace();
        } finally {
            super.closePreparedStatement(pst);
            super.closeConnection(connection);
        }
    }

    @Override
    public void update(Role role) {
        if (role == null){
            throw new NullPointerException();
        }
        try {
            connection = super.createConnection();
            pst = connection.prepareStatement(UPDATE_ROLE);
            pst.setString(1, role.getName());
            pst.setLong(2, role.getId());
            pst.execute();
            connection.commit();
        } catch (SQLException e) {
            rollBackTransactional(connection);
            e.printStackTrace();
        } finally {
            super.closePreparedStatement(pst);
            super.closeConnection(connection);
        }
    }

    @Override
    public void remove(Role role) {
        if (role == null){
            throw new NullPointerException();
        }
        if (role.getId() == null){
            throw new IllegalArgumentException();
        }
        try {
            connection = super.createConnection();
            pst = connection.prepareStatement(DELETE_USER_WITH_ROLE);
            pst.setLong(1, role.getId());
            pst.execute();
            pst = connection.prepareStatement(DELETE_ROLE);

            pst.setLong(1, role.getId());
            int result = pst.executeUpdate();
            if (result == 0) {
                connection.rollback();
                throw new IllegalArgumentException();
            }
            connection.commit();
        } catch (SQLException e) {
            rollBackTransactional(connection);
            e.printStackTrace();
        } finally {
            super.closePreparedStatement(pst);
            super.closeConnection(connection);
        }
    }

    @Override
    public Role findByName(String name) {
        if (name == null){
            throw new NullPointerException();
        }
        Role role = new Role();
        try {
            connection = super.createConnection();
            pst = connection.prepareStatement(GET_ROLE_BY_NAME);
            pst.setString(1, name);
            rs = pst.executeQuery();

            while (rs.next()) {
                role.setId(rs.getLong(Role.ID));
                role.setName(rs.getString(Role.NAME));
            }
            connection.commit();
        } catch (SQLException e) {
            rollBackTransactional(connection);
            e.printStackTrace();
        } finally {
            super.closeResultSet(rs);
            super.closePreparedStatement(pst);
            super.closeConnection(connection);
        }
        return role;
    }

    public Role findById(Long id) {
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

    private void rollBackTransactional(Connection connection) {
        synchronized (this) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

}
