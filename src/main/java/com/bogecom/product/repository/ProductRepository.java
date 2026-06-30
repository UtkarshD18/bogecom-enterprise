package com.bogecom.product.repository;

import com.bogecom.product.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository
    extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

  Optional<Product> findBySkuAndIsDeletedFalse(String sku);

  Optional<Product> findBySlugAndIsDeletedFalse(String slug);

  boolean existsBySkuAndIsDeletedFalse(String sku);

  boolean existsBySlugAndIsDeletedFalse(String slug);
}
