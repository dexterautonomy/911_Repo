/*
create table userclass
(
    id int unsigned not null auto_increment primary key,
    myposts int unsigned not null,
    adscredit int unsigned not null,
    
    cpm int unsigned not null,
    cpc int unsigned not null,
    appban int not null,
    postban int not null,
    commentban int not null,
    active int not null,

    pix varchar(255) not null,
    email varchar(255) not null,
    gender varchar(10) not null,
    username varchar(255) unique not null,
    password varchar(255) not null,
    confirmemail varchar(255) not null
);

create table roleclass
(
    id int unsigned not null auto_increment primary key,
    rolename varchar(20) unique not null
);

create table userrole
(
    userid int unsigned not null,
    roleid int unsigned not null,
    primary key (`userid`,`roleid`),
    constraint `fk_user` foreign key (`userid`) references `userclass` (`id`),
    constraint `fk_role` foreign key (`roleid`) references `roleclass` (`id`)
);

create table postclass
(
    id int unsigned not null auto_increment primary key,
    likes int unsigned not null,
    views int unsigned not null,
    
    approved int unsigned not null,

    content text not null,
    title varchar(255) not null,    
    category varchar(255) not null,
    username varchar(255) not null,
    postdate varchar(50) not null,
    coverimage varchar(255) not null
);


create table postuserclass
(
    postid int unsigned not null,
    userid int unsigned not null,
    primary key (`postid`, `userid`),
    constraint `fk_post_1` foreign key (`postid`) references `postclass` (`id`),
    constraint `fk_user_1` foreign key (`userid`) references `userclass` (`id`)
);

*/