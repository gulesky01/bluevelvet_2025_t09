package com.musicstore.bluevelvet.infrastructure.repository;

import com.musicstore.bluevelvet.infrastructure.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT c FROM Product p JOIN p.categories c WHERE p.id = :product_id ORDER BY c.id")
    Page<Category> findByProductId(Long product_id, Pageable pageable);

    Page<Category> findByNameLike(String name, Pageable pageable);

    Page<Category> findByParentId(Long parent_id, Pageable pageable);

    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
