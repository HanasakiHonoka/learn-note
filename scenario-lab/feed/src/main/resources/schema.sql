DROP ALL OBJECTS;

CREATE TABLE post(id BIGINT PRIMARY KEY, author_id BIGINT NOT NULL, created_at BIGINT NOT NULL, celebrity BOOLEAN NOT NULL, deleted BOOLEAN DEFAULT FALSE);
CREATE INDEX post_author_time ON post(author_id,created_at,id);
CREATE TABLE friendship(reader_id BIGINT, author_id BIGINT, PRIMARY KEY(reader_id,author_id));
CREATE TABLE inbox(reader_id BIGINT, post_id BIGINT, PRIMARY KEY(reader_id,post_id));
INSERT INTO friendship VALUES(10,1),(10,2);
