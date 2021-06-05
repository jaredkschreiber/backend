-- popular books by genre
create table popular_books_by_genre (
    id serial not null,
    genre varchar(32) not null,
    book_id varchar(10) not null,
    rank int not null,
    ranked_time timestamptz not null,
    foreign key (book_id) references books(id) on delete cascade
);