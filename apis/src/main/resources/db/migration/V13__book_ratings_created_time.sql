-- Add created time column to book ratings table
-- ALWAYS store in UTC. The client can convert it to local time.
alter table book_ratings add column created_time timestamp without time zone default (now() at time zone 'utc');