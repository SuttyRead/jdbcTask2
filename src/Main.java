import dao.JdbcRoleDao;
import dao.JdbcUserDao;
import entity.Role;
import entity.User;

import java.util.Arrays;
import java.util.Date;

public class Main {

    public static void main(String[] args) {
        JdbcRoleDao roleDao = new JdbcRoleDao();
        JdbcUserDao userDao = new JdbcUserDao();
        Role role = new Role(3L, "Guest");
        roleDao.create(role);
        userDao.create(new User(5L, "user5", "root", "user5@email.com", "Rich", "Green",
                new Date(System.currentTimeMillis()), role));
        userDao.create(new User(6L, "user6", "root", "user6@email.com", "Rich", "Black",
                new Date(System.currentTimeMillis()), role));
        System.out.println(Arrays.toString(userDao.findAll().toArray()));
        System.out.println(Arrays.toString(roleDao.findAll().toArray()));
        System.out.println("Ant work");
    }

}
