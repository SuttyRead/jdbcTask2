CREATE TABLE IF NOT EXISTS Role(
      id BIGINT primary key,
       name varchar(255));
CREATE TABLE IF NOT EXISTS User(id BIGINT primary key, login varchar(255), password varchar(255),
      email varchar(255), firstname varchar(255), lastname varchar(255), birthday DATE, roleId BIGINT);