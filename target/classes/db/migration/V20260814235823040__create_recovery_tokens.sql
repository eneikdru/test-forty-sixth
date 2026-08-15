CREATE TABLE password_recovery_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    used_at TIMESTAMP NULL
);

CREATE INDEX idx_password_recovery_tokens_token ON password_recovery_tokens (token);
CREATE INDEX idx_password_recovery_tokens_email ON password_recovery_tokens (email);
