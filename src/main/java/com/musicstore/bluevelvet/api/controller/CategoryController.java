package com.musicstore.bluevelvet.api.controller;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.api.response.ProductResponse;
import com.musicstore.bluevelvet.domain.exception.BlobNotFoundException;
import com.musicstore.bluevelvet.domain.exception.CategoryNotFoundException;
import com.musicstore.bluevelvet.domain.exception.InvalidDataOperationException;
import com.musicstore.bluevelvet.domain.service.CategoryService;
import com.musicstore.bluevelvet.domain.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService service;
    private final ProductService productService;

    @PreAuthorize("hasRole('Administrator') or hasRole('Editor')")
    @GetMapping("/{id}")
    @Operation(summary = "Fetch category by id", description = "Fetch a category from the Blue Velvet Music Store")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        log.info("Request received to fetch a category by id {}.", id);

        return ResponseEntity.ok(service.findById(id));
    }

    @PreAuthorize("hasRole('Administrator') or hasRole('Salesperson') or hasRole('Shipper')")
    @GetMapping
    @Operation(summary = "Get all categories, with optional name filter", description = "Get all product categories from the Blue Velvet Music Store")
    public ResponseEntity<Page<CategoryResponse>> getProductsOfCategory(@RequestParam(required=false) String name, Pageable pageable) {
        log.info("Request received to fetch all categories.");

        if (name != null){
            return ResponseEntity.ok(service.findByName(name,pageable));
        }
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/products_of_category/{id}/")
    @Operation(summary = "Get all Products of a category", description = "Get all products that belong in a category with a given ID.")
    public ResponseEntity<Page<ProductResponse>> getProductsOfCategory(@PathVariable Long id, Pageable pageable) {
        log.info("Request received to fetch all products of category ID {}.", id);

        return ResponseEntity.ok(productService.findByCategoryId(id, pageable));
    }


    @GetMapping("/child_categories_of/{id}/")
    @Operation(summary = "Get all child categories of another category", description = "Get all categories whose parent category has a given ID.")
    public ResponseEntity<Page<CategoryResponse>> getChildrenOfCategory(@PathVariable Long id, Pageable pageable) {
        log.info("Request received to fetch all children of category ID {}.", id);

        return ResponseEntity.ok(service.findByParentId(id, pageable));
    }

    @PreAuthorize("hasRole('Administrator') or hasRole('Editor')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category by id", description = "Delete a category from the Blue Velvet Music Store")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long id) {
        log.info("Request received to delete a product by id {}", id);

        service.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists")
    @Operation(summary = "Check if category exists by name")
    public ResponseEntity<Boolean> categoryExists(@RequestParam String name) {
        log.info("Checking if category exists with name: {}", name);
        return ResponseEntity.ok(service.existsByName(name));
    }


    @PreAuthorize("hasRole('Administrator')")
    @PostMapping
    @Operation(summary = "Create a new category", description = "Create a category of products for the Blue Velvet Music Store")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request){
        log.info("Request received to create a new product. The request is {}", request);

        return ResponseEntity.ok(service.createCategory(request));
    }

    @PreAuthorize("hasRole('Administrator')")
    @PutMapping("/{id}")
    @Operation(summary = "Update a category", description = "Update a category of products from the Blue Velvet Music Store")
    public ResponseEntity<CategoryResponse> updateCategoryById(@PathVariable Long id, @RequestBody CategoryRequest request) {
        log.info("Request received to update the product with id {} with the request {}", id, request);
        return ResponseEntity.ok(service.updateCategory(id, request));
    }

    @PreAuthorize("hasRole('Administrator')")
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





    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<?> handleInvalidData(CategoryNotFoundException exc) { return ResponseEntity.notFound().build(); }


    @ExceptionHandler(BlobNotFoundException.class)
    public ResponseEntity<?> handleBlobNotFound(BlobNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InvalidDataOperationException.class)
    public ResponseEntity<?> handleInvalidData(InvalidDataOperationException exc) { return ResponseEntity.badRequest().body(exc.getMessage()); }

}
