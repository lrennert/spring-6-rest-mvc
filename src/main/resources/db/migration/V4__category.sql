drop table if exists category cascade;
drop table if exists beer_category;

create table category (
    id                 varchar(36) primary key not null,
    version            bigint,
    created_date       timestamp,
    last_modified_date timestamp,
    description        varchar(50)
);

create table beer_category (
    beer_id            varchar(36) not null,
    category_id        varchar(36) not null,
    primary key (beer_id, category_id),
    constraint pc_beer_id_fk foreign key (beer_id) references beer (id),
    constraint pc_category_id_fk foreign key (category_id) references category (id)
);
