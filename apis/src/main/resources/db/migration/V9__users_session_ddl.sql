ALTER TABLE users
ADD sessionid varchar(255);

ALTER TABLE users
ADD CONSTRAINT UC_sessionid UNIQUE (sessionid);