package com.linghong.fkdp.repository;

import com.linghong.fkdp.pojo.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface MerchantRepository extends JpaRepository<Merchant,Long> {
    Merchant findByUser_UserId(@Param("userId") Long userId);
}
