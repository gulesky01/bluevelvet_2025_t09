package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.request.ProductRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.api.response.ProductResponse;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import com.musicstore.bluevelvet.domain.service.ProductService;
import com.musicstore.bluevelvet.infrastructure.entity.Category;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService service;

    @GetMapping("/{id}")
    @Operation(summary = "Fetch category by id", description = "Fetch a product from the Blue Velvet Music Store")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        log.info("Request received to fetch a product by id {}", id);

        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Get all products from the Blue Velvet Music Store")
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(Pageable pageable) {
        log.info("Request received to fetch all products");

        return ResponseEntity.ok(service.findAll(pageable));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by id", description = "Delete a product from the Blue Velvet Music Store")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long id) {
        log.info("Request received to delete a product by id {}", id);

        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Create a new category", description = "Create a product for the Blue Velvet Music Store")
    public ResponseEntity<CategoryResponse> createProduct(@RequestBody CategoryRequest request){
        log.info("Request received to create a new product. The request is {}", request);

        return ResponseEntity.ok(service.createCategory(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a category", description = "Update a product from the Blue Velvet Music Store")
    public ResponseEntity<CategoryResponse> updateProductById(@PathVariable Long id, @RequestBody CategoryRequest request) {
        log.info("Request received to update the product with id {} with the request {}", id, request);

        return ResponseEntity.ok(service.updateCategory(id, request));
    }

}
