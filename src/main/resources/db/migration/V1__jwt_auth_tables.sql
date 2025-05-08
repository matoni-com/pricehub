CREATE TABLE users (
    id INT8 PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL
);

CREATE TABLE authorities (
    id INT8 PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id INT8 NOT NULL REFERENCES users(id),
    authority TEXT NOT NULL,
    UNIQUE(user_id, authority)
);