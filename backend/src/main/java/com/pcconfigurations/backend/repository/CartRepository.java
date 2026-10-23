package com.pcconfigurations.backend.repository;

import com.pcconfigurations.backend.entity.CartItem;
import com.pcconfigurations.backend.entity.User;
import com.pcconfigurations.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);
    void deleteByUser(User user);
    long countByUser(User user);
}
