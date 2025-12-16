package org.hospital.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserSessionMapper {

    <T> void insert(T param);


    <T> int revokeByJti(T param);
}