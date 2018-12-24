package com.linghong.fkdp.repository;

import com.linghong.fkdp.pojo.OpenUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface OpenUserRepository extends JpaRepository<OpenUser,Long> {
    OpenUser findByOpenId(@Param("openId") String openId);
}
