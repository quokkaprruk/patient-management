CREATE TABLE IF NOT EXISTS billing
(
    id              UUID PRIMARY KEY,
    patient_id      UUID  NOT NULL UNIQUE,
    first_name      VARCHAR(100)        NOT NULL,
    last_name       VARCHAR(100)        NOT NULL,
    email           VARCHAR(255)        NOT NULL,
    status          VARCHAR(20)         NOT NULL,
    created_at      TIMESTAMP           NOT NULL
);

