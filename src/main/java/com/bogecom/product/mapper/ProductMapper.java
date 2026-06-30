package com.bogecom.product.mapper;

import com.bogecom.product.dto.CreateProductRequest;
import com.bogecom.product.dto.ProductDto;
import com.bogecom.product.dto.ProductImageDto;
import com.bogecom.product.dto.UpdateProductRequest;
import com.bogecom.product.entity.Product;
import com.bogecom.product.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    builder = @org.mapstruct.Builder(disableBuilder = true))
public interface ProductMapper {

  @Mapping(source = "published", target = "isPublished")
  ProductDto toDto(Product product);

  @Mapping(source = "primary", target = "isPrimary")
  ProductImageDto toDto(ProductImage productImage);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "slug", ignore = true)
  @Mapping(target = "images", ignore = true)
  @Mapping(target = "published", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deletedBy", ignore = true)
  @Mapping(target = "version", ignore = true)
  Product toEntity(CreateProductRequest request);

  @Mapping(source = "isPublished", target = "published")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "sku", ignore = true)
  @Mapping(target = "slug", ignore = true)
  @Mapping(target = "category", ignore = true)
  @Mapping(target = "images", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deletedBy", ignore = true)
  @Mapping(target = "version", ignore = true)
  void updateEntityFromDto(UpdateProductRequest request, @MappingTarget Product product);

  @Mapping(source = "isPrimary", target = "primary")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "product", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deletedBy", ignore = true)
  @Mapping(target = "version", ignore = true)
  ProductImage toEntity(ProductImageDto dto);
}
