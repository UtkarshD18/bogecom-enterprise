package com.bogecom.user.repository;

import com.bogecom.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmailAndIsDeletedFalse(String email);

  boolean existsByEmailAndIsDeletedFalse(String email);
}
