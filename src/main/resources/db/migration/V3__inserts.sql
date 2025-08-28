insert into customer (login, email, is_active, id)
values ('user1', 'user1@example.com', true, gen_random_uuid());
insert into product (name, article, dictionary, category, price, qty, inserted_at, last_qty_change, is_available, id)
values ('Product 1', 'ART123', 'Dictionary 1', 'Category 1', 10.99, 100, current_timestamp, current_timestamp, true, gen_random_uuid());
insert into "order" (customer_id, status, delivery_address, id)
values (1, 'Pending', '123 Main St', gen_random_uuid());
insert into ordered_product (qty, price, order_id, product_id, id)
values (5, 54.95, (select id from "order"), (select id from "product"), gen_random_uuid());
