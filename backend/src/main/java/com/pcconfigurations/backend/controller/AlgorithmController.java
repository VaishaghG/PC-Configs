package com.pcconfigurations.backend.controller;

import com.pcconfigurations.backend.algorithms.ComponentLinkedList;
import com.pcconfigurations.backend.algorithms.ProductBinarySearch;
import com.pcconfigurations.backend.algorithms.ProductMergeSort;
import com.pcconfigurations.backend.algorithms.SelectionStack;
import com.pcconfigurations.backend.dto.ProductDTO;
import com.pcconfigurations.backend.entity.Product;
import com.pcconfigurations.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/algorithms")
public class AlgorithmController {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Demonstrates all four DSA algorithms.
     * GET /api/algorithms/demo
     */
    @GetMapping("/demo")
    public ResponseEntity<Map<String, Object>> demo() {
        List<Product> allProducts = productRepository.findAll();
        Map<String, Object> result = new LinkedHashMap<>();

        // 1. Linked List Demo
        ComponentLinkedList<String> linkedList = new ComponentLinkedList<>();
        linkedList.add("CPU - Intel Core i9-13900K");
        linkedList.add("GPU - NVIDIA RTX 4090");
        linkedList.add("RAM - Corsair Dominator 32GB DDR5");
        linkedList.add("Storage - Samsung 990 Pro 2TB");
        linkedList.add("Cabinet - Lian Li PC-O11 Dynamic");
        String beforeRemove = linkedList.toString();
        linkedList.remove("GPU - NVIDIA RTX 4090");

        Map<String, Object> llDemo = new LinkedHashMap<>();
        llDemo.put("description", "Custom Singly Linked List managing 5 PC components");
        llDemo.put("beforeRemove", beforeRemove);
        llDemo.put("afterRemoveGPU", linkedList.toString());
        llDemo.put("size", linkedList.size());
        llDemo.put("find", linkedList.find("CPU - Intel Core i9-13900K"));
        result.put("linkedList", llDemo);

        // 2. Stack Demo (undo simulation)
        SelectionStack<String> stack = new SelectionStack<>();
        stack.push("Selected CPU");
        stack.push("Selected GPU");
        stack.push("Selected RAM");

        Map<String, Object> stackDemo = new LinkedHashMap<>();
        stackDemo.put("description", "Custom Stack for undo support in PC Builder");
        stackDemo.put("peek", stack.peek());
        stackDemo.put("pop (undo)", stack.pop());
        stackDemo.put("peekAfterPop", stack.peek());
        stackDemo.put("size", stack.size());
        stackDemo.put("isEmpty", stack.isEmpty());
        result.put("stack", stackDemo);

        // 3. Binary Search Demo
        List<Product> sorted = allProducts.stream()
                .sorted(Comparator.comparingLong(Product::getId))
                .collect(Collectors.toList());

        long targetId = sorted.isEmpty() ? 1L : sorted.get(sorted.size() / 2).getId();
        Product found = ProductBinarySearch.search(sorted, targetId);

        Map<String, Object> bsDemo = new LinkedHashMap<>();
        bsDemo.put("description", "Binary Search on products sorted by ID (O(log n))");
        bsDemo.put("searchedId", targetId);
        bsDemo.put("found", found != null ? found.getName() + " (" + found.getBrand() + ")" : "Not found");
        bsDemo.put("totalProducts", sorted.size());
        result.put("binarySearch", bsDemo);

        // 4. Merge Sort Demo
        List<Product> sortedAsc = ProductMergeSort.sortByPrice(allProducts, ProductMergeSort.SortOrder.ASCENDING);
        List<Product> sortedDesc = ProductMergeSort.sortByPrice(allProducts, ProductMergeSort.SortOrder.DESCENDING);

        Map<String, Object> msDemo = new LinkedHashMap<>();
        msDemo.put("description", "Merge Sort on products by price (O(n log n))");
        msDemo.put("top3Cheapest", sortedAsc.stream().limit(3)
                .map(p -> p.getName() + " - ₹" + p.getPrice()).collect(Collectors.toList()));
        msDemo.put("top3MostExpensive", sortedDesc.stream().limit(3)
                .map(p -> p.getName() + " - ₹" + p.getPrice()).collect(Collectors.toList()));
        result.put("mergeSort", msDemo);

        return ResponseEntity.ok(result);
    }

    /** Binary search by product ID */
    @GetMapping("/search/{id}")
    public ResponseEntity<?> binarySearch(@PathVariable Long id) {
        List<Product> sorted = productRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Product::getId))
                .collect(Collectors.toList());
        Product found = ProductBinarySearch.search(sorted, id);
        if (found == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ProductDTO.fromProduct(found));
    }

    /** Merge sort all products by price */
    @GetMapping("/sort")
    public ResponseEntity<List<ProductDTO>> mergeSort(
            @RequestParam(defaultValue = "asc") String order) {
        List<Product> all = productRepository.findAll();
        ProductMergeSort.SortOrder sortOrder = "desc".equalsIgnoreCase(order)
                ? ProductMergeSort.SortOrder.DESCENDING
                : ProductMergeSort.SortOrder.ASCENDING;
        return ResponseEntity.ok(
                ProductMergeSort.sortByPrice(all, sortOrder)
                        .stream()
                        .map(ProductDTO::fromProduct)
                        .collect(Collectors.toList())
        );
    }
}
