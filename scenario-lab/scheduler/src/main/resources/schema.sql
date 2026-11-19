DROP ALL OBJECTS;

CREATE TABLE job_run(id BIGINT PRIMARY KEY, owner VARCHAR(80), lease_until BIGINT, token BIGINT, done BOOLEAN DEFAULT FALSE);
CREATE TABLE protected_resource(id BIGINT PRIMARY KEY, last_token BIGINT, payload VARCHAR(80));
INSERT INTO job_run(id,lease_until,token) VALUES(1,0,0);
INSERT INTO protected_resource VALUES(1,0,'initial');
