CREATE TABLE IF NOT EXISTS hits
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    uri       varchar                     NOT NULL,
    app       varchar                     NOT NULL,
    ip        varchar                     NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);