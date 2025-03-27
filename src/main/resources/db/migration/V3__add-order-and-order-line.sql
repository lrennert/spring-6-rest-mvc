drop table if exists beer_order_line;
drop table if exists beer_order cascade;

create table beer_order (
    id                  varchar(36) primary key not null,
    version             bigint,
    created_date        timestamp,
    last_modified_date  timestamp,
    customer_ref        varchar(255),
    customer_id         varchar(36) not null references customer (id)
);

create table beer_order_line (
    id                  varchar(36) primary key,
    version             bigint,
    created_date        timestamp,
    last_modified_date  timestamp,
    beer_id             varchar(36) not null references beer (id),
    order_quantity      integer,
    quantity_allocated  integer,
    beer_order_id       varchar(36) not null references beer_order (id)
);