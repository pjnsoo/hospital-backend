package org.hospital.model.mapper;

import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserSessionMapper {

    <T> void updateSession(T param);

    <T> int rotateRefreshToken(T param);

    <T> int revokeByJti(T param);
}