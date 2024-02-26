create table USERS(
id serial primary key,
user_name varchar (30) not null,
password varchar (30) not null,
user_token varchar(200) not null unique,
email varchar (300) not null unique
);

create table BOARD (
id serial primary key,
board_name varchar(50) not null unique,
descriptions VARCHAR(300) not null
);

create table USER_BOARD(
board_id integer not null,
user_id integer not null,
foreign key (board_id) references BOARD(id),
foreign key (user_id) references USERS(id),
primary key(board_id, user_id)
);

create table LIST (
id serial primary key,
list_name varchar(50) not null unique ,
board integer not null,
foreign key (board) references BOARD(id)
);

create table CARD(
id serial primary key,
descriptions VARCHAR(300) not null,
card_name varchar(50) not null,
dueDate date,
positions integer,
list integer,
foreign key (list) references LIST(id)
);



