package com.pcconfigurations.backend;

import com.pcconfigurations.backend.dto.GreedyBuildResponse;
import com.pcconfigurations.backend.entity.Product;
import com.pcconfigurations.backend.service.GreedyBuildService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GreedyBuildTest {

    private GreedyBuildService greedyBuildService;

    @BeforeEach
    void setUp() {
        greedyBuildService = new GreedyBuildService();
    }

    private List<Product> createSampleCatalog() {
        List<Product> products = new ArrayList<>();
        long id = 1;

        // CPUs
        products.add(createProduct(id++, "Intel Core i5-13400F", Product.Category.CPU, new BigDecimal("20000.00"), 10));
        products.add(createProduct(id++, "Intel Core i9-13900K", Product.Category.CPU, new BigDecimal("50000.00"), 5));

        // GPUs
        products.add(createProduct(id++, "NVIDIA RTX 4060", Product.Category.GPU, new BigDecimal("45000.00"), 8));
        products.add(createProduct(id++, "NVIDIA RTX 4090", Product.Category.GPU, new BigDecimal("160000.00"), 2));

        // RAMs
        products.add(createProduct(id++, "Kingston 16GB DDR4", Product.Category.RAM, new BigDecimal("4500.00"), 15));
        products.add(createProduct(id++, "Corsair 32GB DDR5", Product.Category.RAM, new BigDecimal("14000.00"), 12));

        // STORAGE
        products.add(createProduct(id++, "Crucial 500GB SSD", Product.Category.STORAGE, new BigDecimal("3500.00"), 20));
        products.add(createProduct(id++, "Samsung 2TB NVMe", Product.Category.STORAGE, new BigDecimal("15000.00"), 10));

        // CABINET
        products.add(createProduct(id++, "NZXT H5 Flow", Product.Category.CABINET, new BigDecimal("7000.00"), 6));
        products.add(createProduct(id++, "Lian Li O11 Dynamic", Product.Category.CABINET, new BigDecimal("13000.00"), 4));

        return products;
    }

    private Product createProduct(Long id, String name, Product.Category category, BigDecimal price, int stock) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setBrand("TestBrand");
        p.setCategory(category);
        p.setPrice(price);
        p.setStockQuantity(stock);
        return p;
    }

    @Test
    void testGreedyBuild_successfulSelection() {
        List<Product> catalog = createSampleCatalog();
        // Cheapest components:
        // CPU: 20000, GPU: 45000, RAM: 4500, STORAGE: 3500, CABINET: 7000 => Total = 80000.00
        BigDecimal budget = new BigDecimal("100000.00");

        GreedyBuildResponse response = greedyBuildService.buildWithinBudget(catalog, budget);

        assertNotNull(response);
        assertEquals(5, response.getSelectedComponents().size());
        assertEquals(0, new BigDecimal("80000.00").compareTo(response.getTotalPrice()));
        assertEquals(0, new BigDecimal("20000.00").compareTo(response.getRemainingBudget()));
        assertEquals(0, budget.compareTo(response.getBudget()));

        // Verify that the cheapest item was picked for every required category
        assertTrue(response.getSelectedComponents().stream().anyMatch(c -> c.getName().equals("Intel Core i5-13400F")));
        assertTrue(response.getSelectedComponents().stream().anyMatch(c -> c.getName().equals("NVIDIA RTX 4060")));
        assertTrue(response.getSelectedComponents().stream().anyMatch(c -> c.getName().equals("Kingston 16GB DDR4")));
        assertTrue(response.getSelectedComponents().stream().anyMatch(c -> c.getName().equals("Crucial 500GB SSD")));
        assertTrue(response.getSelectedComponents().stream().anyMatch(c -> c.getName().equals("NZXT H5 Flow")));

        // Ensure total price never exceeds budget
        assertTrue(response.getTotalPrice().compareTo(budget) <= 0);
    }

    @Test
    void testGreedyBuild_insufficientBudget_throwsException() {
        List<Product> catalog = createSampleCatalog();
        // Minimum build cost is 80,000.00. Providing 75,000.00 should fail.
        BigDecimal budget = new BigDecimal("75000.00");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                greedyBuildService.buildWithinBudget(catalog, budget)
        );

        assertTrue(ex.getMessage().contains("Cannot generate a complete build within budget"));
        assertTrue(ex.getMessage().contains("80,000.00"));
    }

    @Test
    void testGreedyBuild_missingCategory_throwsException() {
        List<Product> catalog = createSampleCatalog();
        // Remove all GPU components to simulate zero eligible products in a required slot
        catalog.removeIf(p -> p.getCategory() == Product.Category.GPU);

        BigDecimal budget = new BigDecimal("150000.00");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                greedyBuildService.buildWithinBudget(catalog, budget)
        );

        assertTrue(ex.getMessage().contains("No eligible in-stock products found for required category: GPU"));
    }

    @Test
    void testGreedyBuild_neverExceedsBudget() {
        List<Product> catalog = createSampleCatalog();
        // Exactly minimum cost (80000.00)
        BigDecimal exactBudget = new BigDecimal("80000.00");
        GreedyBuildResponse responseExact = greedyBuildService.buildWithinBudget(catalog, exactBudget);
        assertEquals(0, exactBudget.compareTo(responseExact.getTotalPrice()));
        assertEquals(0, BigDecimal.ZERO.compareTo(responseExact.getRemainingBudget()));
        assertTrue(responseExact.getTotalPrice().compareTo(exactBudget) <= 0);

        // Huge budget (500000.00)
        BigDecimal hugeBudget = new BigDecimal("500000.00");
        GreedyBuildResponse responseHuge = greedyBuildService.buildWithinBudget(catalog, hugeBudget);
        assertTrue(responseHuge.getTotalPrice().compareTo(hugeBudget) <= 0);
        assertEquals(0, new BigDecimal("80000.00").compareTo(responseHuge.getTotalPrice()));
        assertEquals(0, new BigDecimal("420000.00").compareTo(responseHuge.getRemainingBudget()));

        // 1 rupee below minimum
        BigDecimal belowMin = new BigDecimal("79999.00");
        assertThrows(IllegalArgumentException.class, () ->
                greedyBuildService.buildWithinBudget(catalog, belowMin)
        );
    }

    @Test
    void testGreedyBuild_outOfStockIgnored() {
        List<Product> catalog = createSampleCatalog();
        // Add a super cheap CPU (₹10,000) that is out of stock (stockQuantity = 0)
        catalog.add(createProduct(999L, "Cheap Out-of-Stock CPU", Product.Category.CPU, new BigDecimal("10000.00"), 0));

        BigDecimal budget = new BigDecimal("100000.00");
        GreedyBuildResponse response = greedyBuildService.buildWithinBudget(catalog, budget);

        // Should ignore the out-of-stock CPU and pick the in-stock 20000 CPU
        assertTrue(response.getSelectedComponents().stream().anyMatch(c -> c.getName().equals("Intel Core i5-13400F")));
        assertFalse(response.getSelectedComponents().stream().anyMatch(c -> c.getName().equals("Cheap Out-of-Stock CPU")));
        assertEquals(0, new BigDecimal("80000.00").compareTo(response.getTotalPrice()));
    }

    @Test
    void testGreedyBuild_invalidBudgetValidation() {
        List<Product> catalog = createSampleCatalog();

        assertThrows(IllegalArgumentException.class, () ->
                greedyBuildService.buildWithinBudget(catalog, null)
        );

        assertThrows(IllegalArgumentException.class, () ->
                greedyBuildService.buildWithinBudget(catalog, BigDecimal.ZERO)
        );

        assertThrows(IllegalArgumentException.class, () ->
                greedyBuildService.buildWithinBudget(catalog, new BigDecimal("-5000.00"))
        );
    }

    @Test
    void testGreedyBuild_emptyCatalog() {
        assertThrows(IllegalArgumentException.class, () ->
                greedyBuildService.buildWithinBudget(new ArrayList<>(), new BigDecimal("50000.00"))
        );
    }
}
