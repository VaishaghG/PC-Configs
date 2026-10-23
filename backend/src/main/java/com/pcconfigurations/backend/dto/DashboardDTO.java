package com.pcconfigurations.backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardDTO {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private int totalOrders;
    private BigDecimal totalSpent;
    private int savedBuilds;
    private List<OrderDTO> recentOrders;
    private List<PcBuildDTO> recentBuilds;
}
