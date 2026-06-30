package com.bogecom.product.repository;

import com.bogecom.product.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlugAndIsDeletedFalse(String slug);

    boolean existsBySlugAndIsDeletedFalse(String slug);
    
    List<Category> findByParentIdIsNullAndIsDeletedFalse();
    
    List<Category> findByParentIdAndIsDeletedFalse(Long parentId);
}
