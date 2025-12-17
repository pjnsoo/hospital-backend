package org.hospital.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserSessionMapper {

    <T> Map<String, Object> sel(T param);
    <T> void insert(T param);


    <T> int revokeByJti(T param);
}