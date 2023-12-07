create table _user (
    tg_id bigint primary key
);

create table deck (
    id serial primary key,
    owner bigint references _user(tg_id),
    title varchar(100)
);

create table user_deck (
    user_tg_id bigint references _user(tg_id) on delete cascade,
    deck_id integer references deck(id) on delete cascade,
    primary key (user_tg_id, deck_id)
);

create table side (
    id serial primary key,
    title varchar(100),
    text varchar(320),
    image_link varchar(60)
);

create table card (
    id serial primary key,
    deck_id integer not null references deck(id),
    question integer not null references side(id),
    answer integer not null references side(id)
);
