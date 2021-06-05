-- popular books
create table popular_books (
    id serial not null,
    book_id varchar(10) not null,
    rank int,
    ranked_time timestamptz,
    foreign key (book_id) references books(id) on delete cascade
);