package com.example.DOTSAPI.repository;

import com.example.DOTSAPI.model.Category;
import com.example.DOTSAPI.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
}
