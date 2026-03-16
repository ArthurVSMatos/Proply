CREATE TABLE properties (
    id UUID PRIMARY KEY ,
    title VARCHAR(255) NOT NULL,
    price NUMERIC(15,2),
    descripton TEXT,

    company_id UUID NOT NULL,

    created_at TIMESTAMP,

    CONSTRAINT fk_properties_company
        FOREIGN KEY (company_id)
        REFERENCES  companies(id)
)