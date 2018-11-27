package com.ua.sutty.jdbc;

import com.ua.sutty.jdbc.domain.Role;
import com.ua.sutty.jdbc.repository.impl.JdbcRoleDao;

public class Main {

    public static void main(String[] args) {

        JdbcRoleDao jdbcRoleDao = new JdbcRoleDao();
        Role role = new Role("USER");
        jdbcRoleDao.create(role);
        System.out.println(jdbcRoleDao.findByName("USER"));
    }

}
