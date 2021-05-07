create table booksinlist(
    list_id varchar(25),
    book_in_list varchar(10),
    primary key (list_id, book_in_list),
    foreign key (list_id) references booklists(id) on delete cascade
);

-- book list table
create table booklists (
   id varchar(25) primary key,
   list_type list_type,
   num_books int
);
