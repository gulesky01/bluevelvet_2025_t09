package com.musicstore.bluevelvet.domain.service;

import com.musicstore.bluevelvet.api.request.CategoryRequest;
import com.musicstore.bluevelvet.api.response.CategoryResponse;
import com.musicstore.bluevelvet.infrastructure.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface CategoryService {

    CategoryResponse findById(Long id);

    Page<CategoryResponse> findAll(Pageable pageable);

    void deleteById(Long id);

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    CategoryResponse setPictureOfCategory(Long id, MultipartFile file) throws Exception;

    Page<CategoryResponse> findByProductId(Long id, Pageable pageable);

    Page<CategoryResponse> findByName(String name, Pageable pageable);

    Page<CategoryResponse> findByParentId(Long id, Pageable pageable);

    boolean existsByName(String name);
}
