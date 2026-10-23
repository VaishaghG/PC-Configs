package com.pcconfigurations.backend.service;

import com.pcconfigurations.backend.dto.CartDTO;
import com.pcconfigurations.backend.dto.CartItemDTO;
import com.pcconfigurations.backend.entity.CartItem;
import com.pcconfigurations.backend.entity.Product;
import com.pcconfigurations.backend.entity.User;
import com.pcconfigurations.backend.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    public CartDTO getCart(User user) {
        List<CartItem> items = cartRepository.findByUser(user);
        return buildCartDTO(items);
    }

    @Transactional
    public CartDTO addToCart(User user, Long productId, int quantity) {
        Product product = productService.getProductEntityById(productId);

        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        CartItem existing = cartRepository.findByUserAndProduct(user, product).orElse(null);
        if (existing != null) {
            int newQty = existing.getQuantity() + quantity;
            if (newQty > product.getStockQuantity()) {
                throw new IllegalArgumentException("Cannot exceed available stock of " + product.getStockQuantity());
            }
            existing.setQuantity(newQty);
            cartRepository.save(existing);
        } else {
            CartItem item = new CartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(quantity);
            cartRepository.save(item);
        }

        return getCart(user);
    }

    @Transactional
    public CartDTO updateQuantity(User user, Long itemId, int quantity) {
        CartItem item = cartRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }

        if (quantity <= 0) {
            cartRepository.delete(item);
        } else {
            if (quantity > item.getProduct().getStockQuantity()) {
                throw new IllegalArgumentException("Cannot exceed available stock");
            }
            item.setQuantity(quantity);
            cartRepository.save(item);
        }

        return getCart(user);
    }

    @Transactional
    public CartDTO removeItem(User user, Long itemId) {
        CartItem item = cartRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }

        cartRepository.delete(item);
        return getCart(user);
    }

    @Transactional
    public void clearCart(User user) {
        cartRepository.deleteByUser(user);
    }

    private CartDTO buildCartDTO(List<CartItem> items) {
        List<CartItemDTO> dtoItems = items.stream().map(this::toDTO).collect(Collectors.toList());
        BigDecimal total = dtoItems.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartDTO dto = new CartDTO();
        dto.setItems(dtoItems);
        dto.setTotal(total);
        dto.setItemCount(dtoItems.stream().mapToInt(CartItemDTO::getQuantity).sum());
        return dto;
    }

    private CartItemDTO toDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setProductBrand(item.getProduct().getBrand());
        dto.setProductCategory(item.getProduct().getCategory().name());
        dto.setUnitPrice(item.getProduct().getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        dto.setImageUrl(item.getProduct().getImageUrl());
        dto.setStockQuantity(item.getProduct().getStockQuantity());
        return dto;
    }
}
