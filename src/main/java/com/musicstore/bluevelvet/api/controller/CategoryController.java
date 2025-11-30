package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.request.ProductRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.api.response.ProductResponse;
import com.musicstore.bluevelvet.domain.exception.BlobNotFoundException;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import com.musicstore.bluevelvet.domain.service.ProductService;
import com.musicstore.bluevelvet.infrastructure.entity.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping(path = "/category_picture/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Set category picture", description = "Change the picture representing a certain category")
    public ResponseEntity<CategoryResponse> updateCategoryPicture(@PathVariable("id") Long id, @Parameter(description="file") @RequestParam("file") MultipartFile file){
        log.info("MVC Trigger: update picture at existing category {}", id);
        try {
            return ResponseEntity.ok(service.setPictureOfCategory(id, file));
        }
        catch (Exception e){
            log.error("MVC picture update failure: {}", e.toString());
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(BlobNotFoundException.class)
    public ResponseEntity<?> handleBlobNotFound(BlobNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
