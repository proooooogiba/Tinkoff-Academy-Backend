CREATE TABLE users (
    id int PRIMARY KEY,
    name varchar(255) UNIQUE NOT NULL,
    password_hash varchar(255) NOT NULL
);
