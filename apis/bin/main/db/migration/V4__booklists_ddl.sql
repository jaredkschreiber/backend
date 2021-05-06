--enum for book type
create type list_type AS ENUM ('READ', 'READING', 'DNF', 'TOREAD');

create table users (
    id varchar(10) primary key,
    created date
);

-- book list table
create table booklists (
    id varchar(25) primary key,
    list_type list_type,
    num_books int, 
    created date
);

-- table joining users and book lists
create table users_booklists (
    user_id varchar(10),
    booklists_id varchar(25),
    primary key (user_id, booklists_id),
    foreign key (user_id) references users(id) on delete cascade,
    foreign key (user_id) references booklists(id) on delete cascade
);

-- table joining books and book lists
create table books_booklists (
    book_id varchar(10),
    booklists_id varchar(25),
    primary key (book_id, booklists_id),
    foreign key (book_id) references books(id) on delete cascade,
    foreign key (booklists_id) references booklists(id) on delete cascade
);