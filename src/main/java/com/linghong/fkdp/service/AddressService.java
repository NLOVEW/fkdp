package com.linghong.fkdp.service;

import com.linghong.fkdp.pojo.Address;
import com.linghong.fkdp.pojo.User;
import com.linghong.fkdp.repository.AddressRepository;
import com.linghong.fkdp.repository.UserRepository;
import com.linghong.fkdp.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: luck_nhb
 * @Date: 2018/11/29 11:04
 * @Version 1.0
 * @Description:
 */
@Service
public class AddressService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private AddressRepository addressRepository;
    @Resource
    private UserRepository userRepository;

    public boolean addAddress(Address address, HttpServletRequest request) {
        Long userId = JwtUtil.getUserId(request);
        User user = userRepository.findById(userId).get();
        address.setUser(user);
        address.setCreateTime(new Date());
        address.setUserful(true);
        addressRepository.save(address);
        return true;
    }

    public List<Address> getAllAddressByUserId(Long userId) {
        List<Address> addresses = addressRepository.findAllByUser_UserId(userId);
        addresses = addresses.stream()
                             .filter(address -> address.getUserful().equals(true))
                             .collect(Collectors.toList());
        return addresses;
    }

    public Address findAddressByAddressId(Long addressId) {
        return addressRepository.findById(addressId).get();
    }

    public boolean deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId).get();
        address.setUserful(false);
        addressRepository.save(address);
        return true;
    }
}
