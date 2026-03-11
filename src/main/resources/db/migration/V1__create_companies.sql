CREATE TABLE companies (
                           id UUID PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           slug VARCHAR(255) UNIQUE NOT NULL,
                           email VARCHAR(255) UNIQUE NOT NULL,
                           phone VARCHAR(20),
                           plan VARCHAR(50),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);