CREATE TABLE IF NOT EXISTS PUBLIC.TAB_TODO
(
    tod_id          SERIAL NOT NULL,
    tod_title       VARCHAR(80) NOT NULL,
    tod_description VARCHAR(500),
    tod_duedate     TIMESTAMP NOT NULL,
    tod_done        BOOLEAN NOT NULL,
    PRIMARY KEY (tod_id)
);

ALTER SEQUENCE PUBLIC.TAB_TODO_TOD_ID_SEQ RESTART 1000;