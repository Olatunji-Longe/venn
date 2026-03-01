CREATE TABLE IF NOT EXISTS funds (
    id UUID DEFAULT random_uuid() PRIMARY KEY,
    load_id VARCHAR(255) NOT NULL,
    customer_id VARCHAR(255) NOT NULL,
    load_amount DECIMAL(19, 2) NOT NULL,
    time TIMESTAMP NOT NULL,
    UNIQUE (load_id, customer_id)
);
