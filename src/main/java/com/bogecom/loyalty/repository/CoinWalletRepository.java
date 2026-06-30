package com.bogecom.loyalty.repository;

import com.bogecom.loyalty.entity.CoinWallet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinWalletRepository extends JpaRepository<CoinWallet, Long> {

  Optional<CoinWallet> findByUserIdAndIsDeletedFalse(Long userId);
}
