package com.pcconfigurations.backend.service;

import com.pcconfigurations.backend.dto.DashboardDTO;
import com.pcconfigurations.backend.dto.OrderDTO;
import com.pcconfigurations.backend.dto.PcBuildDTO;
import com.pcconfigurations.backend.entity.Order;
import com.pcconfigurations.backend.entity.User;
import com.pcconfigurations.backend.repository.OrderRepository;
import com.pcconfigurations.backend.repository.PcBuildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PcBuildRepository pcBuildRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PcBuildService pcBuildService;

    public DashboardDTO getDashboard(User user) {
        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(user);

        BigDecimal totalSpent = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<OrderDTO> recentOrders = orders.stream()
                .limit(5)
                .map(o -> orderService.getOrderById(user, o.getId()))
                .collect(Collectors.toList());

        List<PcBuildDTO> recentBuilds = pcBuildService.getUserBuilds(user)
                .stream().limit(3).collect(Collectors.toList());

        DashboardDTO dto = new DashboardDTO();
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setTotalOrders(orders.size());
        dto.setTotalSpent(totalSpent);
        dto.setSavedBuilds((int) pcBuildRepository.findByUserOrderByCreatedAtDesc(user).size());
        dto.setRecentOrders(recentOrders);
        dto.setRecentBuilds(recentBuilds);
        return dto;
    }
}
