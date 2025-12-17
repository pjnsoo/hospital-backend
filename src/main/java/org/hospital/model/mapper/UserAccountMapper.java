package org.hospital.model.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.hospital.model.table.UserAccount;

@Mapper
public interface UserAccountMapper {

    <T> UserAccount findByUsername(T param);
    <T> boolean existsByUsername(T param);
    <T> int insert(T param);


}