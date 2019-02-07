package com.linghong.fkdp.repository;

import com.linghong.fkdp.pojo.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    Admin findByMobilePhone(@Param("mobilePhone") String mobilePhone);
}
