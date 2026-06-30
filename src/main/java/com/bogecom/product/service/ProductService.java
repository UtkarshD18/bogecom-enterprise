package com.bogecom.product.service;

import com.bogecom.exception.BusinessException;
import com.bogecom.product.dto.CreateProductRequest;
import com.bogecom.product.dto.ProductDto;
import com.bogecom.product.dto.ProductSearchCriteria;
import com.bogecom.product.dto.UpdateProductRequest;
import com.bogecom.product.entity.Product;
import com.bogecom.product.mapper.ProductMapper;
import com.bogecom.product.repository.ProductRepository;
import com.bogecom.product.repository.ProductSpecification;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  @Transactional(readOnly = true)
  public Page<ProductDto> getProducts(ProductSearchCriteria criteria, Pageable pageable) {
    Page<Product> productPage =
        productRepository.findAll(ProductSpecification.withCriteria(criteria), pageable);
    return productPage.map(productMapper::toDto);
  }

  @Transactional(readOnly = true)
  public ProductDto getProductBySlug(String slug) {
    return productMapper.toDto(getProductEntityBySlug(slug));
  }

  @Transactional(readOnly = true)
  public ProductDto getProductById(Long id) {
    return productMapper.toDto(getProductEntityById(id));
  }

  @Transactional
  public ProductDto createProduct(CreateProductRequest request) {
    if (productRepository.existsBySkuAndIsDeletedFalse(request.sku())) {
      throw new BusinessException("Product SKU already exists", HttpStatus.CONFLICT);
    }

    String slug = generateSlug(request.name());
    if (productRepository.existsBySlugAndIsDeletedFalse(slug)) {
      slug = slug + "-" + System.currentTimeMillis();
    }

    Product product = productMapper.toEntity(request);
    product.setSlug(slug);

    if (request.images() != null && !request.images().isEmpty()) {
      for (var imgDto : request.images()) {
        product.addImage(productMapper.toEntity(imgDto));
      }
    }

    product = productRepository.save(product);
    log.info("Product created with SKU: {}", product.getSku());
    return productMapper.toDto(product);
  }

  @Transactional
  public ProductDto updateProduct(Long id, UpdateProductRequest request) {
    Product product = getProductEntityById(id);

    productMapper.updateEntityFromDto(request, product);

    // If name changes, we could optionally update the slug, but usually slugs should remain stable
    // for SEO.
    // For now, we leave the slug unchanged during update.

    product = productRepository.save(product);
    log.info("Product updated with ID: {}", id);
    return productMapper.toDto(product);
  }

  @Transactional
  public void deleteProduct(Long id) {
    Product product = getProductEntityById(id);
    product.setDeleted(true);
    productRepository.save(product);
    log.info("Product soft-deleted with ID: {}", id);
  }

  // --- Private Helpers ---

  private Product getProductEntityById(Long id) {
    return productRepository
        .findById(id)
        .filter(p -> !p.isDeleted())
        .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));
  }

  private Product getProductEntityBySlug(String slug) {
    return productRepository
        .findBySlugAndIsDeletedFalse(slug)
        .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));
  }

  private String generateSlug(String name) {
    return name.toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
  }
}
