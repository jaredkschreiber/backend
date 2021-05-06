-- column authors.name changes width to 128 characters
alter table authors
alter column name set data type varchar(128);

-- column genres.genre changes width to 128 characters
alter table genres
alter column genre set data type varchar(128);