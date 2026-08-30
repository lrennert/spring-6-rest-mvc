alter table beer_order_line
  add column order_line_status varchar(100);

update beer_order_line
   set order_line_status = 'NEW';

alter table beer_order_line
alter column order_line_status set not null;