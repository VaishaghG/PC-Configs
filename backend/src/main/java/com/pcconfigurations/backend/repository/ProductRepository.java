package com.pcconfigurations.backend.repository;

import com.pcconfigurations.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Product.Category category);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByOrderByPriceAsc();
    List<Product> findByOrderByPriceDesc();
    List<Product> findByOrderByNameAsc();

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchProducts(@Param("query") String query);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchByCategory(@Param("category") Product.Category category, @Param("query") String query);
}
