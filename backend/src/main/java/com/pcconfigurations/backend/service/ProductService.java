package com.pcconfigurations.backend.service;

import com.pcconfigurations.backend.algorithms.ProductBinarySearch;
import com.pcconfigurations.backend.algorithms.ProductMergeSort;
import com.pcconfigurations.backend.dto.ProductDTO;
import com.pcconfigurations.backend.entity.Product;
import com.pcconfigurations.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDTO> getAllProducts(String sort) {
        List<Product> products;

        if ("price_asc".equalsIgnoreCase(sort)) {
            products = ProductMergeSort.sortByPrice(productRepository.findAll(), ProductMergeSort.SortOrder.ASCENDING);
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            products = ProductMergeSort.sortByPrice(productRepository.findAll(), ProductMergeSort.SortOrder.DESCENDING);
        } else if ("name".equalsIgnoreCase(sort)) {
            products = productRepository.findAll().stream()
                    .sorted(Comparator.comparing(Product::getName))
                    .collect(Collectors.toList());
        } else {
            products = productRepository.findAll();
        }

        return products.stream().map(ProductDTO::fromProduct).collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        // Use binary search on id-sorted list for DSA demonstration
        List<Product> sorted = productRepository.findAll()
                .stream()
                .sorted(Comparator.comparingLong(Product::getId))
                .collect(Collectors.toList());

        Product found = ProductBinarySearch.search(sorted, id);
        if (found == null) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        return ProductDTO.fromProduct(found);
    }

    public List<ProductDTO> getProductsByCategory(String category, String sort) {
        Product.Category cat = Product.Category.valueOf(category.toUpperCase());
        List<Product> products = productRepository.findByCategory(cat);

        if ("price_asc".equalsIgnoreCase(sort)) {
            products = ProductMergeSort.sortByPrice(products, ProductMergeSort.SortOrder.ASCENDING);
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            products = ProductMergeSort.sortByPrice(products, ProductMergeSort.SortOrder.DESCENDING);
        }

        return products.stream().map(ProductDTO::fromProduct).collect(Collectors.toList());
    }

    public List<ProductDTO> searchProducts(String query) {
        return productRepository.searchProducts(query)
                .stream()
                .map(ProductDTO::fromProduct)
                .collect(Collectors.toList());
    }

    public Product getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    // Admin
    public ProductDTO createProduct(Product product) {
        return ProductDTO.fromProduct(productRepository.save(product));
    }

    public ProductDTO updateProduct(Long id, Product updated) {
        Product existing = getProductEntityById(id);
        existing.setName(updated.getName());
        existing.setBrand(updated.getBrand());
        existing.setDescription(updated.getDescription());
        existing.setCategory(updated.getCategory());
        existing.setPrice(updated.getPrice());
        existing.setStockQuantity(updated.getStockQuantity());
        existing.setImageUrl(updated.getImageUrl());
        existing.setSpecifications(updated.getSpecifications());
        return ProductDTO.fromProduct(productRepository.save(existing));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product p = getProductEntityById(id);
        if (p.getCartItems() != null) {
            p.getCartItems().clear();
        }
        if (p.getBuildComponents() != null) {
            p.getBuildComponents().clear();
        }
        productRepository.delete(p);
    }
}
