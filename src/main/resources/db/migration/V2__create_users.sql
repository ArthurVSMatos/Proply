CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       company_id UUID NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_company
                           FOREIGN KEY(company_id)
                               REFERENCES companies(id)
                               ON DELETE CASCADE
);