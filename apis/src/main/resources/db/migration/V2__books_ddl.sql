-- books table
create table books (
    id varchar(10) primary key,
    title varchar(255) not null,
    blurb text,
    pages int,
    published date
);

-- authors table
create table authors (
    id varchar(10) primary key,
    name varchar(32) not null
);

-- table joining books and authors
create table books_authors (
    book_id varchar(10),
    author_id varchar(10),
    primary key (book_id, author_id),
    foreign key (book_id) references books(id) on delete cascade,
    foreign key (author_id) references authors(id) on delete cascade
);

-- genres table
create table genres (
    book_id varchar(10),
    genre varchar(32),
    primary key (book_id, genre),
    foreign key (book_id) references books(id) on delete cascade
);
