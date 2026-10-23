package com.pcconfigurations.backend.service;

import com.pcconfigurations.backend.algorithms.ComponentLinkedList;
import com.pcconfigurations.backend.algorithms.SelectionStack;
import com.pcconfigurations.backend.dto.PcBuildDTO;
import com.pcconfigurations.backend.dto.PcBuildRequest;
import com.pcconfigurations.backend.entity.*;
import com.pcconfigurations.backend.repository.PcBuildRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PcBuildService {

    @Autowired
    private PcBuildRepository pcBuildRepository;

    @Autowired
    private ProductService productService;

    @Transactional
    public PcBuildDTO createBuild(User user, PcBuildRequest request) {
        if (request.getComponentIds() == null || request.getComponentIds().isEmpty()) {
            throw new IllegalArgumentException("At least one component is required");
        }

        // Use custom LinkedList to manage components during creation
        ComponentLinkedList<Product> componentList = new ComponentLinkedList<>();

        // Use custom Stack to track addition order (for undo/audit)
        SelectionStack<Long> selectionHistory = new SelectionStack<>();

        for (Long productId : request.getComponentIds()) {
            Product product = productService.getProductEntityById(productId);
            componentList.add(product);
            selectionHistory.push(productId);
        }

        PcBuild build = new PcBuild();
        build.setUser(user);
        build.setBuildName(request.getBuildName());
        build.setNotes(request.getNotes());

        BigDecimal total = BigDecimal.ZERO;
        List<PcBuildComponent> components = new ArrayList<>();

        for (Product product : componentList.toList()) {
            PcBuildComponent comp = new PcBuildComponent();
            comp.setPcBuild(build);
            comp.setProduct(product);
            comp.setComponentType(product.getCategory());
            components.add(comp);
            total = total.add(product.getPrice());
        }

        build.setTotalPrice(total);
        build.setComponents(components);

        PcBuild saved = pcBuildRepository.save(build);
        return toDTO(saved);
    }

    public List<PcBuildDTO> getUserBuilds(User user) {
        return pcBuildRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PcBuildDTO getBuildById(User user, Long buildId) {
        PcBuild build = pcBuildRepository.findById(buildId)
                .orElseThrow(() -> new IllegalArgumentException("Build not found"));
        if (!build.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }
        return toDTO(build);
    }

    @Transactional
    public PcBuildDTO updateBuild(User user, Long buildId, PcBuildRequest request) {
        PcBuild build = pcBuildRepository.findById(buildId)
                .orElseThrow(() -> new IllegalArgumentException("Build not found"));
        if (!build.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }

        build.setBuildName(request.getBuildName());
        build.setNotes(request.getNotes());
        build.getComponents().clear();

        ComponentLinkedList<Product> componentList = new ComponentLinkedList<>();
        for (Long productId : request.getComponentIds()) {
            componentList.add(productService.getProductEntityById(productId));
        }

        BigDecimal total = BigDecimal.ZERO;
        for (Product product : componentList.toList()) {
            PcBuildComponent comp = new PcBuildComponent();
            comp.setPcBuild(build);
            comp.setProduct(product);
            comp.setComponentType(product.getCategory());
            build.getComponents().add(comp);
            total = total.add(product.getPrice());
        }
        build.setTotalPrice(total);

        return toDTO(pcBuildRepository.save(build));
    }

    @Transactional
    public void deleteBuild(User user, Long buildId) {
        PcBuild build = pcBuildRepository.findById(buildId)
                .orElseThrow(() -> new IllegalArgumentException("Build not found"));
        if (!build.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Access denied");
        }
        pcBuildRepository.delete(build);
    }

    private PcBuildDTO toDTO(PcBuild build) {
        PcBuildDTO dto = new PcBuildDTO();
        dto.setId(build.getId());
        dto.setBuildName(build.getBuildName());
        dto.setNotes(build.getNotes());
        dto.setTotalPrice(build.getTotalPrice());
        dto.setCreatedAt(build.getCreatedAt());
        dto.setUpdatedAt(build.getUpdatedAt());

        if (build.getComponents() != null) {
            dto.setComponents(build.getComponents().stream().map(c -> {
                PcBuildDTO.ComponentDTO cdto = new PcBuildDTO.ComponentDTO();
                cdto.setId(c.getId());
                cdto.setProductId(c.getProduct().getId());
                cdto.setProductName(c.getProduct().getName());
                cdto.setProductBrand(c.getProduct().getBrand());
                cdto.setComponentType(c.getComponentType().name());
                cdto.setPrice(c.getProduct().getPrice());
                cdto.setImageUrl(c.getProduct().getImageUrl());
                return cdto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}
