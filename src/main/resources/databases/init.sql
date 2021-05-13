create table if not exists t_user (
    id serial primary key,
    username varchar(100),
    email varchar(100),
    firstname varchar(100),
    lastname varchar(100),
    password varchar(100)
);
create table if not exists t_role (
    id serial primary key,
    name varchar(100)
);
create table if not exists t_tour (
    id serial primary key,
    start varchar(100),
    finish varchar(100),
    price integer,
    date varchar(100),
    count integer
);
create table if not exists t_description (
    id serial primary key,
    img varchar(256),
    text varchar(256)
);