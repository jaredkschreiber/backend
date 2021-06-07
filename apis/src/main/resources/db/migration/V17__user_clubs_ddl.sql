create table clubs (
    id serial not null primary key,
    club_name varchar(32) not null,
    club_description varchar(255) not null,
    club_owner int not null,
    foreign key (club_owner) references users(id) on delete cascade
);

create table clubs_users (
    id serial not null primary key,
    join_dt date not null default CURRENT_DATE,
    user_id int not null,
    club_id int not null,
    foreign key (club_id) references clubs(id) on delete cascade,
    foreign key (user_id) references users(id) on delete cascade
);