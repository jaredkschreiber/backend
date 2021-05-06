alter table booklists
drop column user;

alter table booklists
add user_id varchar (25);

drop table users_booklists;
drop table users;