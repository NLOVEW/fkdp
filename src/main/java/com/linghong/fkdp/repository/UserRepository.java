package com.linghong.fkdp.repository;

import com.linghong.fkdp.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByMobilePhone(@Param("mobilePhone") String mobilePhone);
    User findByOpenUser_OpenId(@Param("openId") String openId);
}
