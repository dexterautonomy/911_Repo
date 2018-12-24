/*
create table userclass
(
    id int unsigned not null auto_increment primary key,
    myposts int unsigned not null,
    adscredit int unsigned not null,
    cpm int unsigned not null,
    cpc int unsigned not null,
    yellow_star int unsigned not null,
    red_flag int unsigned not null,
    blue_share int unsigned not null,
    green_like int unsigned not null,
    followers int unsigned not null,
    black_view int unsigned not null,
    user_rank int unsigned not null,
    color_class varchar(15) not null,
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
    user_id int unsigned not null,
    likes int unsigned not null,
    red_flag int unsigned not null,
    star_flag int unsigned not null,
    views int unsigned not null,
    approved int unsigned not null,
    post_rank int unsigned not null,
    flag int not null,
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
    red_flag int unsigned not null,
    star_flag int unsigned not null,
    share_flag int unsigned not null,
    comment_rank int not null,
    approved int unsigned not null,
    content text not null,
    postdate varchar(50) not null,
    constraint `user_comment_01` foreign key (`user_id`) references userclass(`id`),
    constraint `post_comment_01` foreign key (`post_id`) references postclass(`id`)
);

create table commentreactionclass
(
    id int unsigned not null auto_increment primary key,
    comment_id int unsigned not null,
    user_id int unsigned not null,
    like_flag int not null,
    red_flag int not null,
    star_flag int not null,
    share_flag int not null,
    constraint `fk_comment_react_id` foreign key (`comment_id`) references `commentclass` (`id`),
    constraint `fk_user_comment_react_id` foreign key (`user_id`) references `userclass` (`id`)
);

create table subcommentclass
(
    id int unsigned not null auto_increment primary key,
    user_id int unsigned not null,
    comment_id int unsigned not null,
    likes int unsigned not null,
    red_flag int unsigned not null,
    star_flag int unsigned not null,
    unread varchar(10) not null,
    subcomment_rank int not null,
    approved int unsigned not null,
    content text not null,
    postdate varchar(50) not null,
    constraint `user_quoter_01` foreign key (`user_id`) references userclass(`id`),
    constraint `comment_quoted_01` foreign key (`comment_id`) references commentclass(`id`)
);

create table postreactionclass
(
    id int unsigned not null auto_increment primary key,
    postid int unsigned not null,
    userid int unsigned not null,
    like_flag int not null,
    red_flag int not null,
    star_flag int not null,
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
);

create table followerobject
(
    id int unsigned not null auto_increment primary key,
    user_id int unsigned not null,
    follower_id int unsigned not null,
    flag int not null
);

create table quoteobject
(
    id int unsigned not null auto_increment primary key,
    user_id int unsigned not null,
    comment_id int unsigned not null,
    content text not null,
    quote_date varchar(255) not null,
    constraint `fk_quote_user_1` foreign key (`user_id`) references `userclass` (`id`),
    constraint `fk_quote_comment_1` foreign key (`comment_id`) references `commentclass` (`id`)
);

create table subcommentreactionclass
(
    id int unsigned not null auto_increment primary key,
    subcomment_id int unsigned not null,
    user_id int unsigned not null,
    like_flag int not null,
    red_flag int not null,
    star_flag int not null,
    constraint `fk_sub_comment_react_id` foreign key (`subcomment_id`) references `subcommentclass` (`id`),
    constraint `fk_user_sub_comment_react_id` foreign key (`user_id`) references `userclass` (`id`)
);

create table followedpostdeleteobject
(
    id int unsigned not null auto_increment primary key,
    post_id int unsigned not null,
    user_id int unsigned not null,
    flagdelete int not null,
    flagread int not null
);

create table advertobject
(
    id int unsigned not null auto_increment primary key,
    user_id int unsigned not null,
    approve int not null,
    payoption varchar(10) not null,
    startdate varchar(10) not null,
    clicks int unsigned not null,
    views int unsigned not null,
    adsimage varchar(255) not null,
    landingpage varchar(255) not null,
    pause int not null,
    expired int not null,
    constraint `fk_user_ads` foreign key (`user_id`) references `userclass` (`id`)
);

*/