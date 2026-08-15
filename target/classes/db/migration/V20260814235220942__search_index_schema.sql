CREATE TABLE epidemiological_materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    pathogen_type VARCHAR(100) NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_epidemiological_materials_pathogen_type ON epidemiological_materials (pathogen_type);
CREATE INDEX idx_epidemiological_materials_title ON epidemiological_materials (title);
