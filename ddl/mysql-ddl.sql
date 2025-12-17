-- 1. 사용자 계정 정보
create table user_account
(
    user_no    bigint auto_increment comment '사용자 고유 번호 (pk)'
        primary key,
    username   varchar(100)                              not null comment '로그인 아이디',
    password   varchar(255)                              not null comment '암호화된 비밀번호',
    nickname   varchar(50)                               not null comment '닉네임',
    role       varchar(20)  default 'ROLE_USER'          not null comment '권한 [ROLE_USER, ROLE_ADMIN]',
    status     varchar(20)  default 'ACTIVE'             not null comment '상태 [ACTIVE, INACTIVE, DELETED, BANNED]',
    created_at timestamp(3) default current_timestamp(3) not null,
    updated_at timestamp(3) default current_timestamp(3) not null on update current_timestamp(3),

    constraint uk_user_account_username unique (username)
) comment '사용자 계정 정보';

-- 2. 사용자 로그인 세션 (refresh token 관리 및 기기 연동)
create table user_session
(
    -- pk 구성: 조회가 잦은 순서대로 배치 (user_no -> platform -> device_id)
    user_no    bigint                                    not null comment 'user_account.user_no 참조 (fk)',
    platform   varchar(20)                               not null comment '플랫폼 [WEB, ANDROID, IOS]',
    device_id  varchar(255)                              not null comment '기기 식별 uuid (app:설치시 생성, web:브라우저 생성)',

    -- 토큰 식별 및 보안
    jti        varchar(255)                              not null comment 'refresh token 고유 id',
    push_token varchar(500)                              null comment 'fcm/apns token (알림용)',

    -- 세션 수명 및 상태
    issued_at  timestamp(3)                              not null comment '토큰 발급 일시',
    expires_at timestamp(3)                              not null comment '토큰 만료 일시',
    revoked    tinyint(1)   default 0                    not null comment '중도 폐기 여부 (1: 폐기)',

    -- 클라이언트 메타 데이터
    user_agent varchar(500)                              null comment '브라우저/앱 버전 정보',
    ip         varchar(45)                               null comment '최근 접속 ip',

    created_at timestamp(3) default current_timestamp(3) not null,
    updated_at timestamp(3) default current_timestamp(3) not null on update current_timestamp(3),

    -- 복합 pk: 한 유저가 같은 플랫폼의 같은 기기에서 중복 세션을 갖지 못하게 함
    primary key (user_no, platform, device_id),

    constraint uk_user_session_jti unique (jti),
    constraint fk_session_to_account
        foreign key (user_no) references user_account (user_no)
            on delete cascade
) comment '사용자 로그인 세션';

-- 3. 성능 최적화를 위한 인덱스 추가
-- 유효 기간이 지난 세션을 정리(cleanup batch)할 때 성능을 위해 추가
create index idx_user_session_expires_at on user_session (expires_at);