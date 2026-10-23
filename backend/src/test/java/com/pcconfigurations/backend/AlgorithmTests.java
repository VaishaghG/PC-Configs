package com.pcconfigurations.backend;

import com.pcconfigurations.backend.algorithms.*;
import com.pcconfigurations.backend.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = AlgorithmTests.class)
class AlgorithmTests {

    // ========== Linked List Tests ==========

    @Test
    void testLinkedList_addAndSize() {
        ComponentLinkedList<String> list = new ComponentLinkedList<>();
        list.add("CPU");
        list.add("GPU");
        list.add("RAM");
        assertEquals(3, list.size());
        assertFalse(list.isEmpty());
    }

    @Test
    void testLinkedList_remove() {
        ComponentLinkedList<String> list = new ComponentLinkedList<>();
        list.add("CPU");
        list.add("GPU");
        boolean removed = list.remove("GPU");
        assertTrue(removed);
        assertEquals(1, list.size());
        assertFalse(list.remove("GPU")); // not found
    }

    @Test
    void testLinkedList_find() {
        ComponentLinkedList<String> list = new ComponentLinkedList<>();
        list.add("CPU");
        list.add("RAM");
        assertEquals("CPU", list.find("CPU"));
        assertNull(list.find("Storage"));
    }

    @Test
    void testLinkedList_toList() {
        ComponentLinkedList<Integer> list = new ComponentLinkedList<>();
        list.add(10);
        list.add(20);
        list.add(30);
        List<Integer> result = list.toList();
        assertEquals(List.of(10, 20, 30), result);
    }

    // ========== Stack Tests ==========

    @Test
    void testStack_pushAndPeek() {
        SelectionStack<String> stack = new SelectionStack<>();
        stack.push("SelectCPU");
        stack.push("SelectGPU");
        assertEquals("SelectGPU", stack.peek());
        assertEquals(2, stack.size());
    }

    @Test
    void testStack_pop() {
        SelectionStack<String> stack = new SelectionStack<>();
        stack.push("SelectCPU");
        stack.push("SelectGPU");
        String popped = stack.pop();
        assertEquals("SelectGPU", popped);
        assertEquals(1, stack.size());
    }

    @Test
    void testStack_isEmpty() {
        SelectionStack<String> stack = new SelectionStack<>();
        assertTrue(stack.isEmpty());
        stack.push("action");
        assertFalse(stack.isEmpty());
    }

    @Test
    void testStack_popEmptyThrows() {
        SelectionStack<String> stack = new SelectionStack<>();
        assertThrows(IllegalStateException.class, stack::pop);
    }

    // ========== Binary Search Tests ==========

    private List<Product> makeSortedProducts() {
        List<Product> products = new ArrayList<>();
        for (long i = 1; i <= 10; i++) {
            Product p = new Product();
            p.setId(i);
            p.setName("Product " + i);
            p.setBrand("Brand");
            p.setCategory(Product.Category.CPU);
            p.setPrice(BigDecimal.valueOf(i * 1000));
            p.setStockQuantity(10);
            products.add(p);
        }
        return products;
    }

    @Test
    void testBinarySearch_found() {
        List<Product> products = makeSortedProducts();
        Product result = ProductBinarySearch.search(products, 5L);
        assertNotNull(result);
        assertEquals(5L, result.getId());
    }

    @Test
    void testBinarySearch_notFound() {
        List<Product> products = makeSortedProducts();
        assertNull(ProductBinarySearch.search(products, 99L));
    }

    @Test
    void testBinarySearch_firstElement() {
        List<Product> products = makeSortedProducts();
        Product result = ProductBinarySearch.search(products, 1L);
        assertNotNull(result);
        assertEquals("Product 1", result.getName());
    }

    @Test
    void testBinarySearch_lastElement() {
        List<Product> products = makeSortedProducts();
        Product result = ProductBinarySearch.search(products, 10L);
        assertNotNull(result);
        assertEquals("Product 10", result.getName());
    }

    @Test
    void testBinarySearch_emptyList() {
        assertNull(ProductBinarySearch.search(new ArrayList<>(), 1L));
        assertNull(ProductBinarySearch.search(null, 1L));
    }

    @Test
    void testBinarySearch_singleElement() {
        List<Product> single = List.of(makeSortedProducts().get(0)); // ID 1
        assertNotNull(ProductBinarySearch.search(single, 1L));
        assertNull(ProductBinarySearch.search(single, 2L));
    }

    // ========== Merge Sort Tests ==========

    private List<Product> makeUnsortedProducts() {
        List<Product> products = new ArrayList<>();
        int[] prices = {5000, 1000, 8000, 3000, 2000, 9000, 4000, 7000, 6000};
        for (int i = 0; i < prices.length; i++) {
            Product p = new Product();
            p.setId((long)(i + 1));
            p.setName("Product " + (i + 1));
            p.setBrand("Brand");
            p.setCategory(Product.Category.GPU);
            p.setPrice(BigDecimal.valueOf(prices[i]));
            p.setStockQuantity(5);
            products.add(p);
        }
        return products;
    }

    @Test
    void testMergeSort_ascending() {
        List<Product> products = makeUnsortedProducts();
        List<Product> sorted = ProductMergeSort.sortByPrice(products, ProductMergeSort.SortOrder.ASCENDING);
        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue(sorted.get(i).getPrice().compareTo(sorted.get(i + 1).getPrice()) <= 0);
        }
    }

    @Test
    void testMergeSort_descending() {
        List<Product> products = makeUnsortedProducts();
        List<Product> sorted = ProductMergeSort.sortByPrice(products, ProductMergeSort.SortOrder.DESCENDING);
        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue(sorted.get(i).getPrice().compareTo(sorted.get(i + 1).getPrice()) >= 0);
        }
    }

    @Test
    void testMergeSort_doesNotMutateOriginal() {
        List<Product> products = makeUnsortedProducts();
        BigDecimal firstPrice = products.get(0).getPrice();
        ProductMergeSort.sortByPrice(products, ProductMergeSort.SortOrder.ASCENDING);
        assertEquals(firstPrice, products.get(0).getPrice());
    }

    @Test
    void testMergeSort_emptyList() {
        List<Product> sorted = ProductMergeSort.sortByPrice(new ArrayList<>(), ProductMergeSort.SortOrder.ASCENDING);
        assertTrue(sorted.isEmpty());
        assertTrue(ProductMergeSort.sortByPrice(null, ProductMergeSort.SortOrder.ASCENDING).isEmpty());
    }

    @Test
    void testMergeSort_singleElement() {
        List<Product> single = List.of(makeSortedProducts().get(0));
        List<Product> sorted = ProductMergeSort.sortByPrice(single, ProductMergeSort.SortOrder.ASCENDING);
        assertEquals(1, sorted.size());
        assertEquals(single.get(0).getPrice(), sorted.get(0).getPrice());
    }

    @Test
    void testMergeSort_duplicatePrices() {
        List<Product> products = new ArrayList<>();
        int[] prices = {3000, 1000, 3000, 2000, 1000};
        for (int i = 0; i < prices.length; i++) {
            Product p = new Product();
            p.setId((long)(i + 1));
            p.setPrice(BigDecimal.valueOf(prices[i]));
            products.add(p);
        }
        List<Product> sorted = ProductMergeSort.sortByPrice(products, ProductMergeSort.SortOrder.ASCENDING);
        assertEquals(5, sorted.size());
        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue(sorted.get(i).getPrice().compareTo(sorted.get(i + 1).getPrice()) <= 0);
        }
    }
}
