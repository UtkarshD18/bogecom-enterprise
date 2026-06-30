package com.bogecom.product.controller;

import com.bogecom.common.response.ApiResponse;
import com.bogecom.product.dto.CreateProductRequest;
import com.bogecom.product.dto.ProductDto;
import com.bogecom.product.dto.ProductSearchCriteria;
import com.bogecom.product.dto.UpdateProductRequest;
import com.bogecom.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  // --- Public Catalog Endpoints ---

  @GetMapping
  public ResponseEntity<ApiResponse<Page<ProductDto>>> getProducts(
      ProductSearchCriteria criteria, @PageableDefault(size = 20) Pageable pageable) {

    // Ensure public users only see published products
    ProductSearchCriteria safeCriteria =
        new ProductSearchCriteria(
            criteria.keyword(),
            criteria.minPrice(),
            criteria.maxPrice(),
            true // Force isPublished to true for public endpoint
            );

    return ResponseEntity.ok(
        ApiResponse.success(productService.getProducts(safeCriteria, pageable)));
  }

  @GetMapping("/{slug}")
  public ResponseEntity<ApiResponse<ProductDto>> getProductBySlug(@PathVariable String slug) {
    return ResponseEntity.ok(ApiResponse.success(productService.getProductBySlug(slug)));
  }

  // --- Admin Endpoints ---

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Page<ProductDto>>> adminGetProducts(
      ProductSearchCriteria criteria, @PageableDefault(size = 20) Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success(productService.getProducts(criteria, pageable)));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<ProductDto>> createProduct(
      @Valid @RequestBody CreateProductRequest request) {
    return ResponseEntity.ok(ApiResponse.success(productService.createProduct(request)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
      @PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
    return ResponseEntity.ok(ApiResponse.success(productService.updateProduct(id, request)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
  }
}
