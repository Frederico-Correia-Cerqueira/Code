create table PLAYER(
id serial primary key,
username varchar(30) not null unique,
password varchar (30) not null,
state VARCHAR(30) NOT NULL DEFAULT 'IDLE')
;

create table GAME(
id serial primary key,
board varchar(3000) not null,
playerB integer references PLAYER(id),
playerW integer references PLAYER(id),
type varchar(300),
rules varchar(300),
information boolean
);

create table SAVEGAME(
id serial primary key,
game integer references GAME(id) not null ,
player integer references PLAYER(id) not null,
name varchar(30) not null,
description varchar(300)
);

create table WAITINGROOM(
id serial primary key,
playerID integer references PLAYER(id),
variants varchar(400) not null,
openingRules varchar(400) not null,
rank varchar(400) not null
);

create table STATS(
playerID integer references PLAYER(id) primary key,
elo INT NOT NULL,
victories INT NOT NULL,
defeats INT NOT NULL,
totalGames INT NOT NULL,
winRate INT NOT NULL,
rank varchar(400) not null
);

create table TOKEN(
token VARCHAR(256) primary key,
userID int references PLAYER(id)
);


