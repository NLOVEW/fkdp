package com.linghong.fkdp.repository;

import com.linghong.fkdp.pojo.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address,Long> {
    List<Address> findAllByUser_UserId(@Param("userId") Long userId);
}
