package com.ecommerce.backend.service;

import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // T025: Add product
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // T026: Get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // T027: Get product by ID
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with id: " + id)
                );
    }

    // T027: Update product
    public Product updateProduct(Long id, Product product) {
        Product existing = getProductById(id);
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setQuantity(product.getQuantity());
        existing.setCategory(product.getCategory());
        existing.setImageUrl(product.getImageUrl());
        return productRepository.save(existing);
    }

    // Delete product
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // T028: Search by name or description
    public List<Product> searchByName(String keyword) {
        return productRepository.searchByKeyword(keyword);
    }

    // T028: Filter by category
    public List<Product> getByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    // T028: Filter by max price
    public List<Product> getByMaxPrice(Double maxPrice) {
        return productRepository.findByPriceLessThanEqual(maxPrice);
    }

    // T028: Filter by category AND price
    public List<Product> getByCategoryAndPrice(
            String category, Double maxPrice) {
        return productRepository.findByCategoryAndPriceLessThanEqual(
                category, maxPrice
        );
    }
}