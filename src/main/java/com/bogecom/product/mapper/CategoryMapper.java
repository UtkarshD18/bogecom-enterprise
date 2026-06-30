package com.bogecom.product.mapper;

import com.bogecom.product.dto.CategoryDto;
import com.bogecom.product.dto.CreateCategoryRequest;
import com.bogecom.product.dto.UpdateCategoryRequest;
import com.bogecom.product.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    builder = @org.mapstruct.Builder(disableBuilder = true))
public interface CategoryMapper {

  @Mapping(source = "parent.id", target = "parentId")
  @Mapping(source = "active", target = "isActive")
  CategoryDto toDto(Category category);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "slug", ignore = true)
  @Mapping(target = "parent", ignore = true) // Will be set by service
  @Mapping(target = "subCategories", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deletedBy", ignore = true)
  @Mapping(target = "version", ignore = true)
  Category toEntity(CreateCategoryRequest request);

  @Mapping(source = "isActive", target = "active")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "slug", ignore = true)
  @Mapping(target = "parent", ignore = true) // Will be set by service
  @Mapping(target = "subCategories", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "deletedAt", ignore = true)
  @Mapping(target = "deletedBy", ignore = true)
  @Mapping(target = "version", ignore = true)
  void updateEntityFromDto(UpdateCategoryRequest request, @MappingTarget Category category);
}
