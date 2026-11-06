package com.pcconfigurations.backend.service;

import com.pcconfigurations.backend.algorithms.ProductMergeSort;
import com.pcconfigurations.backend.dto.GreedyBuildRequest;
import com.pcconfigurations.backend.dto.GreedyBuildResponse;
import com.pcconfigurations.backend.dto.ProductDTO;
import com.pcconfigurations.backend.entity.Product;
import com.pcconfigurations.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * GreedyBuildService
 *
 * Implements Phase 4 Greedy Algorithm for the PC Configurations platform.
 *
 * Greedy Selection Strategy:
 * 1. For each of the 5 mandatory hardware slots (CPU, GPU, RAM, STORAGE, CABINET):
 *    - Filters eligible candidates having valid stock (stockQuantity > 0) and valid price.
 *    - Sorts the candidates in ascending order by price using O(n log n) ProductMergeSort.
 *    - Greedily identifies the candidate with the minimum price.
 * 2. Feasibility Verification:
 *    - Verifies that the minimum sum across all 5 required categories does not exceed the target budget.
 *    - As components are committed, ensures remaining budget accommodates the chosen item.
 * 3. Output Generation:
 *    - Returns the selected hardware components, individual prices, total cost, and residual budget.
 *
 * Time Complexity:  O(K * M log M) where K is number of categories (5) and M is components per category.
 * Space Complexity: O(N) for sorting buffers and selected components list.
 */
@Service
public class GreedyBuildService {

    public static final List<Product.Category> REQUIRED_CATEGORIES = List.of(
            Product.Category.CPU,
            Product.Category.GPU,
            Product.Category.RAM,
            Product.Category.STORAGE,
            Product.Category.CABINET
    );

    @Autowired(required = false)
    private ProductRepository productRepository;

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##,##0.00");

    /**
     * Entry point for REST controller using request DTO.
     */
    public GreedyBuildResponse generateGreedyBuild(GreedyBuildRequest request) {
        if (request == null || request.getBudget() == null) {
            throw new IllegalArgumentException("Target budget is required.");
        }
        if (request.getBudget().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget must be a positive amount greater than zero.");
        }

        List<Product> inventory = productRepository != null ? productRepository.findAll() : Collections.emptyList();
        return buildWithinBudget(inventory, request.getBudget());
    }

    /**
     * Pure algorithmic solver accepting an in-memory product list and budget.
     * Decoupled from JPA context to enable comprehensive unit testing.
     */
    public GreedyBuildResponse buildWithinBudget(List<Product> availableProducts, BigDecimal budget) {
        if (budget == null || budget.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget must be a positive amount greater than zero.");
        }

        if (availableProducts == null || availableProducts.isEmpty()) {
            throw new IllegalArgumentException("Product catalog is empty. No hardware components available.");
        }

        // Group eligible products (in-stock with valid price) by category
        Map<Product.Category, List<Product>> categoryMap = new EnumMap<>(Product.Category.class);
        for (Product.Category cat : REQUIRED_CATEGORIES) {
            categoryMap.put(cat, new ArrayList<>());
        }

        for (Product p : availableProducts) {
            if (p != null && p.getCategory() != null && categoryMap.containsKey(p.getCategory())) {
                boolean inStock = p.getStockQuantity() != null && p.getStockQuantity() > 0;
                boolean validPrice = p.getPrice() != null && p.getPrice().compareTo(BigDecimal.ZERO) > 0;
                if (inStock && validPrice) {
                    categoryMap.get(p.getCategory()).add(p);
                }
            }
        }

        // Verify that every required category has at least one eligible product
        for (Product.Category cat : REQUIRED_CATEGORIES) {
            List<Product> candidates = categoryMap.get(cat);
            if (candidates.isEmpty()) {
                throw new IllegalArgumentException("No eligible in-stock products found for required category: " + cat.name());
            }
        }

        // Sort candidates within each category ascending by price using ProductMergeSort
        Map<Product.Category, List<Product>> sortedCategoryMap = new EnumMap<>(Product.Category.class);
        BigDecimal minTotalRequired = BigDecimal.ZERO;

        for (Product.Category cat : REQUIRED_CATEGORIES) {
            List<Product> candidates = categoryMap.get(cat);
            List<Product> sorted = ProductMergeSort.sortByPrice(candidates, ProductMergeSort.SortOrder.ASCENDING);
            sortedCategoryMap.put(cat, sorted);
            minTotalRequired = minTotalRequired.add(sorted.get(0).getPrice());
        }

        // Global Budget Check: If the cheapest possible combination exceeds the budget, reject early
        if (minTotalRequired.compareTo(budget) > 0) {
            BigDecimal deficit = minTotalRequired.subtract(budget);
            throw new IllegalArgumentException(String.format(
                    "Cannot generate a complete build within budget of ₹%s. The minimum cost for all 5 required components is ₹%s (short by ₹%s).",
                    formatCurrency(budget),
                    formatCurrency(minTotalRequired),
                    formatCurrency(deficit)
            ));
        }

        // Greedy Selection Phase:
        // Select the lowest-priced component for each category while strictly adhering to the budget constraint
        BigDecimal remainingBudget = budget;
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<Product> selectedProducts = new ArrayList<>();

        for (Product.Category cat : REQUIRED_CATEGORIES) {
            List<Product> sorted = sortedCategoryMap.get(cat);
            Product cheapest = sorted.get(0);

            if (cheapest.getPrice().compareTo(remainingBudget) > 0) {
                throw new IllegalArgumentException(String.format(
                        "Insufficient budget: Cannot fit cheapest %s (%s - ₹%s) within remaining budget of ₹%s.",
                        cat.name(),
                        cheapest.getName(),
                        formatCurrency(cheapest.getPrice()),
                        formatCurrency(remainingBudget)
                ));
            }

            selectedProducts.add(cheapest);
            totalPrice = totalPrice.add(cheapest.getPrice());
            remainingBudget = remainingBudget.subtract(cheapest.getPrice());
        }

        List<ProductDTO> dtos = selectedProducts.stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());

        return GreedyBuildResponse.builder()
                .budget(budget)
                .totalPrice(totalPrice)
                .remainingBudget(remainingBudget)
                .selectedComponents(dtos)
                .strategy("Greedy Lowest-Price Optimizer across 5 required hardware categories (O(N log N))")
                .message("Successfully generated complete 5-component custom PC build within budget.")
                .build();
    }

    private static synchronized String formatCurrency(BigDecimal amount) {
        return amount == null ? "0.00" : CURRENCY_FORMAT.format(amount);
    }
}
