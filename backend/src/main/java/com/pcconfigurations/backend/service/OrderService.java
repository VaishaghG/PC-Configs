package com.pcconfigurations.backend.service;

import com.pcconfigurations.backend.dto.OrderDTO;
import com.pcconfigurations.backend.dto.OrderRequest;
import com.pcconfigurations.backend.entity.*;
import com.pcconfigurations.backend.repository.CartRepository;
import com.pcconfigurations.backend.repository.OrderRepository;
import com.pcconfigurations.backend.repository.PaymentRepository;
import com.pcconfigurations.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public OrderDTO createOrder(User user, OrderRequest request) {
        List<CartItem> cartItems = cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // Validate stock and calculate total from database prices
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int qty = cartItem.getQuantity();

            if (product.getStockQuantity() < qty) {
                throw new IllegalArgumentException(
                        "Insufficient stock for " + product.getName() + ". Available: " + product.getStockQuantity()
                );
            }

            // Deduct stock
            product.setStockQuantity(product.getStockQuantity() - qty);
            productRepository.save(product);

            // Build order item
            OrderItem oi = new OrderItem();
            oi.setProduct(product);
            oi.setQuantity(qty);
            oi.setUnitPrice(product.getPrice()); // server-side price
            orderItems.add(oi);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus(Order.OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(order);

        // Attach order to items and save
        for (OrderItem oi : orderItems) {
            oi.setOrder(savedOrder);
        }
        savedOrder.setOrderItems(orderItems);
        orderRepository.save(savedOrder);

        // Create payment record
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(total);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        if (request.getPaymentMethod() == Payment.PaymentMethod.CASH_ON_DELIVERY) {
            payment.setStatus(Payment.PaymentStatus.PENDING);
        } else {
            // Demo mode: mark as completed
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
        }
        paymentRepository.save(payment);

        // Clear cart
        cartRepository.deleteByUser(user);

        return toDTO(savedOrder, payment);
    }

    public List<OrderDTO> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(o -> {
                    Payment p = o.getPayment();
                    return toDTO(o, p);
                })
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }

        return toDTO(order, order.getPayment());
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(o -> toDTO(o, o.getPayment()))
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return toDTO(updated, updated.getPayment());
    }

    private OrderDTO toDTO(Order order, Payment payment) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setCustomerName(order.getUser().getFullName());
            dto.setCustomerEmail(order.getUser().getEmail());
            dto.setCustomerPhone(order.getUser().getPhone());
        }
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        if (payment != null) {
            dto.setPaymentMethod(payment.getPaymentMethod().name());
            dto.setPaymentStatus(payment.getStatus().name());
        }

        if (order.getOrderItems() != null) {
            dto.setItems(order.getOrderItems().stream().map(oi -> {
                OrderDTO.OrderItemDTO itemDTO = new OrderDTO.OrderItemDTO();
                itemDTO.setId(oi.getId());
                itemDTO.setProductId(oi.getProduct().getId());
                itemDTO.setProductName(oi.getProduct().getName());
                itemDTO.setProductBrand(oi.getProduct().getBrand());
                itemDTO.setProductCategory(oi.getProduct().getCategory().name());
                itemDTO.setQuantity(oi.getQuantity());
                itemDTO.setUnitPrice(oi.getUnitPrice());
                itemDTO.setSubtotal(oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQuantity())));
                itemDTO.setImageUrl(oi.getProduct().getImageUrl());
                return itemDTO;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}
