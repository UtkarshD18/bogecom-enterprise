package com.bogecom.user.repository;

import com.bogecom.user.entity.Address;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

  List<Address> findByUserIdAndIsDeletedFalse(Long userId);

  Optional<Address> findByIdAndUserIdAndIsDeletedFalse(Long id, Long userId);
}
