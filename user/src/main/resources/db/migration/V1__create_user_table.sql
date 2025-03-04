CREATE TABLE users (
    uuid CHAR(36) NOT NULL primary key,
    username VARCHAR(50) not null,
    password VARCHAR(255) not null,
    role VARCHAR(50) NOT NULL,
    enabled boolean not null,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL
);

CREATE INDEX idx_users_deleted_at on users(uuid, deleted_at)