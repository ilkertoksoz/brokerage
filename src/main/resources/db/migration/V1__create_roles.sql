CREATE TABLE IF NOT EXISTS roles (
                                     id BIGINT PRIMARY KEY,
                                     role_user VARCHAR(50) UNIQUE NOT NULL
    );

INSERT INTO roles (id, role_user) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, role_user) VALUES (2, 'ROLE_MODERATOR');
INSERT INTO roles (id, role_user) VALUES (3, 'ROLE_USER');