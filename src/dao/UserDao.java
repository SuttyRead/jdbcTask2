package dao;

import entity.User;

import java.util.List;

public interface UserDao {

    void create(User user);

    void update(User role);

    void remove(User role);

    List<User> findAll();

    User findByLogin(String login);

    User findByEmail(String email);

}
