package org.hospital.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.hospital.entity.UserAccount;

@Mapper
public interface UserAccountMapper {

    <T> UserAccount findByUsername(T param);
    <T> boolean existsByUsername(T param);
    <T> int insert(T param);


}