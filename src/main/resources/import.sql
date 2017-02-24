DROP TABLE USERS IF EXISTS
CREATE TABLE USERS(id SERIAL, name VARCHAR(255), email VARCHAR(255), regdt DATE )
INSERT INTO USERS (name, email, regdt) VALUES ('철수','cccc@naver.com',SYSDATE)
INSERT INTO USERS (name, email, regdt) VALUES ('영희','cccc@naver.com',SYSDATE)