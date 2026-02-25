package com.ecommerce.product.persistence.repository;

import com.ecommerce.product.model.dto.JoiningProduct;
import com.ecommerce.product.persistence.entity.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository
        extends JpaRepository<ProductEntity, String>, PagingAndSortingRepository<ProductEntity, String> {

    Optional<ProductEntity> findBySku(String sku);

    @Query("""
                SELECT p.sku
                FROM ProductEntity p
                WHERE
                    p.categoryId = :categoryId
                    AND
                    p.name LIKE CONCAT('%', :queryText, '%')
                    AND
                    p.isActive = true
            """)
    Page<String> findSkusByLikeQueryText(
            @Param("categoryId") String categoryId,
            @Param("queryText") String queryText,
            Pageable pageable);

    @Query("""
            SELECT new com.ecommerce.product.model.dto.JoiningProduct(
                p.name,
                p.description,
                p.price,
                p.sku,
                c.id, c.name, c.description,
                b.name, b.description, b.logoUrl
            )
            FROM ProductEntity p
            JOIN Category c ON p.categoryId = c.id
            JOIN BrandEntity b ON p.brandId = b.id
            WHERE p.sku IN :skus
              AND p.isActive = true
            """)
    List<JoiningProduct> findProductsBySkus(@Param("skus") Set<String> skus);

}
