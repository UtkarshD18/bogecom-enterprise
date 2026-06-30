package com.bogecom.loyalty.entity;

import com.bogecom.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coin_wallets")
public class CoinWallet extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false, unique = true)
  private Long userId;

  @Builder.Default
  @Column(nullable = false)
  private Long balance = 0L;

  @Builder.Default
  @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CoinTransaction> transactions = new ArrayList<>();

  public void addTransaction(CoinTransaction transaction) {
    transactions.add(transaction);
    transaction.setWallet(this);
  }
}
