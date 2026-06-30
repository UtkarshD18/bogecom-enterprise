package com.bogecom.product.controller;

import com.bogecom.common.response.ApiResponse;
import com.bogecom.product.dto.CategoryDto;
import com.bogecom.product.dto.CreateCategoryRequest;
import com.bogecom.product.dto.UpdateCategoryRequest;
import com.bogecom.product.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  // --- Public Endpoints ---

  @GetMapping("/tree")
  public ResponseEntity<ApiResponse<List<CategoryDto>>> getCategoryTree() {
    return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryTree()));
  }

  @GetMapping("/{slug}")
  public ResponseEntity<ApiResponse<CategoryDto>> getCategoryBySlug(@PathVariable String slug) {
    return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryBySlug(slug)));
  }

  // --- Admin Endpoints ---

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<CategoryDto>> createCategory(
      @Valid @RequestBody CreateCategoryRequest request) {
    return ResponseEntity.ok(ApiResponse.success(categoryService.createCategory(request)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
      @PathVariable Long id, @Valid @RequestBody UpdateCategoryRequest request) {
    return ResponseEntity.ok(ApiResponse.success(categoryService.updateCategory(id, request)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
    categoryService.deleteCategory(id);
    return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
  }
}
