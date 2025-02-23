drop table if exists beer cascade;
drop table if exists customer cascade;

create table beer (
    id                  varchar(36) primary key not null,
    version             bigint,
    created_date        timestamp,
    last_modified_date  timestamp,
    beer_name           varchar(255) not null,
    beer_style          smallint not null check (beer_style between 0 and 9),
    upc                 varchar(50) not null,
    quantity_on_hand    integer,
    price               numeric(10,2) not null
);

create table customer (
    id                  varchar(36) primary key not null,
    version             bigint,
    created_date        timestamp,
    last_modified_date  timestamp,
    name                varchar(255) not null
 );