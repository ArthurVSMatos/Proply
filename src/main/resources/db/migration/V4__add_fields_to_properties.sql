ALTER TABLE properties
ADD COLUMN address VARCHAR(255);

ALTER TABLE properties
    ADD COLUMN city VARCHAR(100);

ALTER TABLE properties
    ADD COLUMN state VARCHAR(100);

ALTER TABLE  properties
    ADD COLUMN  type VARCHAR(50);

ALTER TABLE properties
   ADD COLUMN  status VARCHAR(50);