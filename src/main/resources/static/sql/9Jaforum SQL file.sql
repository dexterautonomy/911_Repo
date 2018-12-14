/*
create table postclass
(
    id int unsigned not null auto_increment primary key,
    user_id int unsigned not null,

    likes int unsigned not null,
    views int unsigned not null,
    
    approved int unsigned not null,

    content text not null,
    title varchar(255) not null,    
    category varchar(255) not null,
    postdate varchar(50) not null,
    coverimage varchar(255) not null,

    constraint `user_post_01` foreign key (`user_id`) references userclass(`id`)
);


create table commentclass
(
    id int unsigned not null auto_increment primary key,
    user_id int unsigned not null,
    post_id int unsigned not null,

    likes int unsigned not null,
    
    approved int unsigned not null,

    content text not null,
    postdate varchar(50) not null,

    constraint `user_comment_01` foreign key (`user_id`) references userclass(`id`),
    constraint `post_comment_01` foreign key (`post_id`) references postclass(`id`)
);


create table subcommentclass
(
    id int unsigned not null auto_increment primary key,
    user_id int unsigned not null,
    comment_id int unsigned not null,

    likes int unsigned not null,
    
    approved int unsigned not null,

    content text not null,
    postdate varchar(50) not null,

    constraint `user_quoter_01` foreign key (`user_id`) references userclass(`id`),
    constraint `comment_quoted_01` foreign key (`comment_id`) references commentclass(`id`)
);

create table postlikeclass
(
    id int unsigned not null auto_increment primary key,
    postid int unsigned not null,
    userid int unsigned not null,
    flag int not null,
    constraint `fk_post_like_id` foreign key (`postid`) references `postclass` (`id`),
    constraint `fk_user_like_id` foreign key (`userid`) references `userclass` (`id`)
);

create table messageobject
(
    id int unsigned not null auto_increment primary key,
    recipient_id int unsigned not null,
    postlink varchar(255) not null,
    comment_id int unsigned not null,
    flag int not null,
    unread varchar(10) not null,
    constraint `fk_comment_message_id1` foreign key (`comment_id`) references `commentclass` (`id`)
)

create table followerobject
(
    id int unsigned not null auto_increment primary key,
    user_id int unsigned not null,
    follower_id int unsigned not null,
    flag int not null
)
*/

