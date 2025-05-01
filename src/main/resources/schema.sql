CREATE TABLE IF NOT EXISTS task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255),
    status VARCHAR(50),
    priority INT
);
