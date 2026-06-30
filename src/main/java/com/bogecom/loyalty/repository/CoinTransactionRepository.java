package com.bogecom.loyalty.repository;

import com.bogecom.loyalty.entity.CoinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoinTransactionRepository extends JpaRepository<CoinTransaction, Long> {}
