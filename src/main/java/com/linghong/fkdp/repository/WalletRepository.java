package com.linghong.fkdp.repository;

import com.linghong.fkdp.pojo.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface WalletRepository extends JpaRepository<Wallet,Long> {
    Wallet findByUser_UserId(@Param("userId") Long userId);
}
