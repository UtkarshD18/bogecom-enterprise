package com.bogecom.auth.repository;

import com.bogecom.auth.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByTokenIdAndIsDeletedFalseAndRevokedFalse(String tokenId);

  @Modifying
  @Query(
      "UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId AND r.isDeleted = false")
  void revokeAllUserTokens(@Param("userId") Long userId);
}
