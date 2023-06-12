-- Liquibase formatted SQL

-- changeset author: Tilo Alexander

CREATE TABLE IF NOT EXISTS quotes (
    id varchar(30) PRIMARY KEY,
    content varchar(500) NOT NULL,
    author varchar(50) NOT NULL,
    tags varchar(100),
    authorSlug varchar(50) NOT NULL,
    length integer NOT NULL,
    dateadded timestamp without time zone NOT NULL,
    datemodified timestamp without time zone NOT NULL
);
