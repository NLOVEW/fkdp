package com.linghong.fkdp.repository;

import com.linghong.fkdp.pojo.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoodsRepository extends JpaRepository<Goods,String>, JpaSpecificationExecutor<Goods> {
    List<Goods> findAllByMerchant_User_UserId(@Param("userId") Long userId);
}
