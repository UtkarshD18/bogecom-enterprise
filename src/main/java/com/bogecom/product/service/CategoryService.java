package com.bogecom.product.service;

import com.bogecom.exception.BusinessException;
import com.bogecom.product.dto.CategoryDto;
import com.bogecom.product.dto.CreateCategoryRequest;
import com.bogecom.product.dto.UpdateCategoryRequest;
import com.bogecom.product.entity.Category;
import com.bogecom.product.mapper.CategoryMapper;
import com.bogecom.product.repository.CategoryRepository;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final CategoryMapper categoryMapper;

  @Transactional(readOnly = true)
  public List<CategoryDto> getCategoryTree() {
    // Fetch only root categories (parentId = null)
    // Since we mapped the subCategories relation, they will be recursively fetched
    // depending on JPA fetching strategy or MapStruct mapping.
    // For standard tree mapping, we map the root nodes.
    List<Category> rootCategories = categoryRepository.findByParentIdIsNullAndIsDeletedFalse();
    return rootCategories.stream().map(categoryMapper::toDto).toList();
  }

  @Transactional(readOnly = true)
  public CategoryDto getCategoryBySlug(String slug) {
    return categoryMapper.toDto(getCategoryEntityBySlug(slug));
  }

  @Transactional
  public CategoryDto createCategory(CreateCategoryRequest request) {
    String slug = generateSlug(request.name());
    if (categoryRepository.existsBySlugAndIsDeletedFalse(slug)) {
      slug = slug + "-" + System.currentTimeMillis();
    }

    Category category = categoryMapper.toEntity(request);
    category.setSlug(slug);

    if (request.parentId() != null) {
      Category parent = getCategoryEntityById(request.parentId());
      category.setParent(parent);
    }

    category = categoryRepository.save(category);
    log.info("Category created with slug: {}", category.getSlug());
    return categoryMapper.toDto(category);
  }

  @Transactional
  public CategoryDto updateCategory(Long id, UpdateCategoryRequest request) {
    Category category = getCategoryEntityById(id);

    categoryMapper.updateEntityFromDto(request, category);

    // Handle parent updates
    if (request.parentId() != null) {
      if (request.parentId().equals(id)) {
        throw new BusinessException("A category cannot be its own parent", HttpStatus.BAD_REQUEST);
      }
      Category parent = getCategoryEntityById(request.parentId());
      category.setParent(parent);
    } else {
      category.setParent(null);
    }

    category = categoryRepository.save(category);
    log.info("Category updated with ID: {}", id);
    return categoryMapper.toDto(category);
  }

  @Transactional
  public void deleteCategory(Long id) {
    Category category = getCategoryEntityById(id);

    // Ensure no active subcategories exist
    if (!category.getSubCategories().isEmpty()
        && category.getSubCategories().stream().anyMatch(c -> !c.isDeleted())) {
      throw new BusinessException(
          "Cannot delete a category that has active subcategories", HttpStatus.CONFLICT);
    }

    category.setDeleted(true);
    categoryRepository.save(category);
    log.info("Category soft-deleted with ID: {}", id);
  }

  // --- Private Helpers ---

  private Category getCategoryEntityById(Long id) {
    return categoryRepository
        .findById(id)
        .filter(c -> !c.isDeleted())
        .orElseThrow(() -> new BusinessException("Category not found", HttpStatus.NOT_FOUND));
  }

  private Category getCategoryEntityBySlug(String slug) {
    return categoryRepository
        .findBySlugAndIsDeletedFalse(slug)
        .orElseThrow(() -> new BusinessException("Category not found", HttpStatus.NOT_FOUND));
  }

  private String generateSlug(String name) {
    return name.toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
  }
}
