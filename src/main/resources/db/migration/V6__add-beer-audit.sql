drop table if exists beer_audit;

create table beer_audit (
    audit_id            varchar(36) primary key not null,
    id                  varchar(36) not null,
    version             bigint,
    created_date        timestamp,
    last_modified_date  timestamp,
    beer_name           varchar,
    beer_style          smallint check (beer_style between 0 and 9),
    upc                 varchar,
    quantity_on_hand    integer,
    price               numeric(38,2),
    created_date_audit  timestamp,
    principal_name      varchar,
    audit_event_type    varchar
);