--drop table Role;
--drop table User;

CREATE TABLE IF NOT EXISTS Role(
      id BIGINT AUTO_INCREMENT primary key,
       name varchar(255));
CREATE TABLE IF NOT EXISTS User(id BIGINT primary key AUTO_INCREMENT, login varchar(255), password varchar(255),
      email varchar(255), firstname varchar(255), lastname varchar(255), birthday DATE, roleId BIGINT);