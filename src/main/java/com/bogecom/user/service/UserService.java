package com.bogecom.user.service;

import com.bogecom.exception.BusinessException;
import com.bogecom.user.dto.AddressDto;
import com.bogecom.user.dto.AdminUpdateUserRequest;
import com.bogecom.user.dto.UpdateProfileRequest;
import com.bogecom.user.dto.UserResponse;
import com.bogecom.user.entity.Address;
import com.bogecom.user.entity.User;
import com.bogecom.user.mapper.AddressMapper;
import com.bogecom.user.mapper.UserMapper;
import com.bogecom.user.repository.AddressRepository;
import com.bogecom.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final AddressRepository addressRepository;
  private final UserMapper userMapper;
  private final AddressMapper addressMapper;

  @Transactional
  public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
    User user = getUserOrThrow(userId);

    user.setFirstName(request.firstName());
    user.setLastName(request.lastName());
    user.setPhone(request.phone());

    user = userRepository.save(user);
    log.info("User {} updated their profile", userId);
    return userMapper.toResponse(user);
  }

  @Transactional(readOnly = true)
  public List<AddressDto> getUserAddresses(Long userId) {
    return addressRepository.findByUserIdAndIsDeletedFalse(userId).stream()
        .map(addressMapper::toDto)
        .toList();
  }

  @Transactional
  public AddressDto addAddress(Long userId, AddressDto request) {
    User user = getUserOrThrow(userId);

    Address address = addressMapper.toEntity(request);
    address.setUser(user);

    // If it's default, we need to unset any other default
    if (address.isDefault()) {
      unsetOtherDefaultAddresses(userId);
    } else {
      // If it's the only address, make it default
      List<Address> existing = addressRepository.findByUserIdAndIsDeletedFalse(userId);
      if (existing.isEmpty()) {
        address.setDefault(true);
      }
    }

    address = addressRepository.save(address);
    log.info("Address {} added for user {}", address.getId(), userId);
    return addressMapper.toDto(address);
  }

  @Transactional
  public AddressDto updateAddress(Long userId, Long addressId, AddressDto request) {
    Address address = getAddressOrThrow(addressId, userId);

    if (request.isDefault() && !address.isDefault()) {
      unsetOtherDefaultAddresses(userId);
    }

    addressMapper.updateEntityFromDto(request, address);
    address = addressRepository.save(address);
    log.info("Address {} updated for user {}", addressId, userId);
    return addressMapper.toDto(address);
  }

  @Transactional
  public void deleteAddress(Long userId, Long addressId) {
    Address address = getAddressOrThrow(addressId, userId);
    address.setDeleted(true);
    addressRepository.save(address);
    log.info("Address {} soft-deleted for user {}", addressId, userId);
  }

  // --- Admin Endpoints ---

  @Transactional(readOnly = true)
  public List<UserResponse> getAllUsers() {
    return userRepository.findAll().stream()
        .filter(u -> !u.isDeleted())
        .map(userMapper::toResponse)
        .toList();
  }

  @Transactional
  public UserResponse adminUpdateUser(Long targetUserId, AdminUpdateUserRequest request) {
    User user = getUserOrThrow(targetUserId);

    user.setRole(request.role());
    user.setAccountStatus(request.accountStatus());

    user = userRepository.save(user);
    log.info(
        "Admin updated user {}: Role={}, Status={}",
        targetUserId,
        user.getRole(),
        user.getAccountStatus());
    return userMapper.toResponse(user);
  }

  // --- Private Helpers ---

  private User getUserOrThrow(Long userId) {
    return userRepository
        .findById(userId)
        .filter(u -> !u.isDeleted())
        .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
  }

  private Address getAddressOrThrow(Long addressId, Long userId) {
    return addressRepository
        .findByIdAndUserIdAndIsDeletedFalse(addressId, userId)
        .orElseThrow(() -> new BusinessException("Address not found", HttpStatus.NOT_FOUND));
  }

  private void unsetOtherDefaultAddresses(Long userId) {
    List<Address> addresses = addressRepository.findByUserIdAndIsDeletedFalse(userId);
    for (Address addr : addresses) {
      if (addr.isDefault()) {
        addr.setDefault(false);
        addressRepository.save(addr);
      }
    }
  }
}
