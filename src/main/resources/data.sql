DROP TABLE IF EXISTS books;

CREATE TABLE books (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       price NUMERIC(10,2) NOT NULL CHECK (price > 0)
);

INSERT INTO books (title, author, price) VALUES
                                             ('Effective Java', 'Joshua Bloch', 45.0),
                                             ('Clean Code', 'Robert C. Martin', 40.0)
    ON CONFLICT DO NOTHING;
