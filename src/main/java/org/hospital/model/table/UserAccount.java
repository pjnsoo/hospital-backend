package org.hospital.model.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hospital.model.table.enums.UserRole;
import org.hospital.model.table.enums.UserStatus;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    String userId;
    String passWd;
    String passSalt;
    String grpCd;
    String nameKor;
    String telNo;
    String userClass;
    String usertypeCd;
    String lastupdateDt;
    String activeYn;
    UserRole userRole;
    String createUser;
    Integer loginCnt;
    String lastloginDt;
    Integer permCd;
}