DROP ALL OBJECTS;

CREATE TABLE packet(id BIGINT PRIMARY KEY, remaining BIGINT, pieces INT);
CREATE TABLE claim(packet_id BIGINT, user_id BIGINT, amount BIGINT, PRIMARY KEY(packet_id,user_id));
INSERT INTO packet VALUES(1,100,5);

CREATE TABLE prize(id BIGINT PRIMARY KEY, available INT CHECK(available>=0));
CREATE TABLE draw_result(request_id VARCHAR(80) PRIMARY KEY, prize_id BIGINT);
INSERT INTO prize VALUES(1,2);
