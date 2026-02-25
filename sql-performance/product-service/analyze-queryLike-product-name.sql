explain analyze select
        pe1_0.name,
        pe1_0.description,
        pe1_0.price,
        pe1_0.sku,
        c1_0.id,
        c1_0.name,
        c1_0.description,
        be1_0.name,
        be1_0.description,
        be1_0.logo_url 
    from
        product_service.products pe1_0 
    join
        product_service.categories c1_0 
            on pe1_0.category_id=c1_0.id 
    join
        product_service.brands be1_0 
            on pe1_0.brand_id=be1_0.id 
    where
        pe1_0.sku in ('') 
        and pe1_0.is_active=1;

-> Limit: 200 row(s)  (cost=1.02 rows=0.1) (actual time=0.0441..0.0441 rows=0 loops=1)
    -> Nested loop inner join  (cost=1.02 rows=0.1) (actual time=0.0433..0.0433 rows=0 loops=1)
        -> Nested loop inner join  (cost=0.984 rows=0.1) (actual time=0.0428..0.0428 rows=0 loops=1)
            -> Filter: ((pe1_0.is_active = 1) and (pe1_0.category_id is not null) and (pe1_0.brand_id is not null))  (cost=0.949 rows=0.1) (actual time=0.0299..0.0299 rows=0 loops=1)
                -> Index lookup on pe1_0 using products_sku_IDX (sku = '')  (cost=0.949 rows=1) (actual time=0.0152..0.0152 rows=0 loops=1)
            -> Single-row index lookup on c1_0 using PRIMARY (id = pe1_0.category_id)  (cost=1.25 rows=1) (never executed)
        -> Single-row index lookup on be1_0 using PRIMARY (id = pe1_0.brand_id)  (cost=1.25 rows=1) (never executed)

===================================== ALREADY INDEX => PERFORMANCE GOOD


select
    count(pe1_0.id) 
from
    products pe1_0 
where
    pe1_0.category_id=? 
    and pe1_0.name like replace(concat('%', ?, '%'), '\\', '\\\\') 
    and pe1_0.is_active=1;

select
    pe1_0.sku 
from
    products pe1_0 
where
    pe1_0.category_id=? 
    and pe1_0.name like replace(concat('%', ?, '%'), '\\', '\\\\') 
    and pe1_0.is_active=1 
order by
    pe1_0.price,
    pe1_0.name desc 
limit
    ?;



-> Limit: 10 row(s)  (cost=208584 rows=10) (actual time=8535..8535 rows=10 loops=1)
    -> Sort: pe1_0.price, pe1_0.`name` DESC, limit input to 10 row(s) per chunk  (cost=208584 rows=341516) (actual time=8535..8535 rows=10 loops=1)
        -> Filter: ((pe1_0.is_active = 1) and (pe1_0.`name` like <cache>(replace(concat('%','Clock','%'),'\\','\\\\'))))  (cost=208584 rows=341516) (actual time=11..8527 rows=4251 loops=1)
            -> Index lookup on pe1_0 using idx_category (category_id = '3c01f521-cf95-11f0-af7b-0242ac110002'), with index condition: (pe1_0.category_id = '3c01f521-cf95-11f0-af7b-0242ac110002')  (cost=208584 rows=341516) (actual time=11..8502 rows=157210 loops=1)


    ALTER TABLE product_service.products
  ADD FULLTEXT INDEX ft_products_name (name);

-> Limit: 10 row(s)  (cost=1.01 rows=1) (actual time=7345..7346 rows=10 loops=1)
    -> Sort row IDs: product_service.products.price, product_service.products.`name` DESC, limit input to 10 row(s) per chunk  (cost=1.01 rows=1) (actual time=7345..7346 rows=10 loops=1)
        -> Filter: ((product_service.products.is_active = 1) and (product_service.products.category_id = '3c01f521-cf95-11f0-af7b-0242ac110002') and (match product_service.products.`name` against ('+Clock' in boolean mode)))  (cost=1.01 rows=1) (actual time=27.1..7321 rows=4251 loops=1)
            -> Full-text index search on products using ft_products_name (name = '+Clock')  (cost=1.01 rows=1) (actual time=26.7..7236 rows=162041 loops=1)
