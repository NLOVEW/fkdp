package com.linghong.fkdp.repository;

import com.linghong.fkdp.pojo.GoodsOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoodsOrderRepository extends JpaRepository<GoodsOrder,String> {
    List<GoodsOrder> findAllByGoods_GoodsId(@Param("goodsId") String goodsId);
    List<GoodsOrder> findAllByUser_UserId(@Param("userId") Long userId);
    List<GoodsOrder> findAllByUser_UserIdAndStatusOrderByCreateTimeDesc(@Param("userId") Long userId,
                                                                        @Param("status") Integer status);
    List<GoodsOrder> findAllByUser_UserIdAndPickUpOrderByCreateTimeDesc(@Param("userId") Long userId,
                                                                        @Param("pickUp") Integer pickUp);
    List<GoodsOrder> findAllByGoods_Merchant_User_UserId(@Param("userId") Long userId);
}
