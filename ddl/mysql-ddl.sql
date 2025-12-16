create table user_account
(
    -- 1. [변경] user_no 사용
    -- '번호(Number)'라는 의미가 강해서 아이디(문자)와 확실히 구분됩니다.
    user_no    bigint auto_increment comment '사용자 고유 번호 (PK)'
        primary key,

    -- 2. 로그인용 ID
    username   varchar(100)                              not null comment '로그인 아이디',
    password   varchar(255)                              not null comment '암호화된 비밀번호',

    -- 3. 추가 정보
    nickname   varchar(50)                               not null comment '닉네임',
    role       varchar(20)  default 'ROLE_USER'          not null comment '권한 [ROLE_USER, ROLE_ADMIN]',
    status     varchar(20)  default 'ACTIVE'             not null comment '상태 [ACTIVE, INACTIVE, DELETED, BANNED]',

    -- 4. 메타 정보
    created_at timestamp(3) default current_timestamp(3) not null,
    updated_at timestamp(3) default current_timestamp(3) not null on update current_timestamp(3),

    constraint uk_user_account_username unique (username)
) comment '사용자 계정 정보';

CREATE TABLE user_session
(
    -- 1. 유저 식별 (FK)
    user_no      bigint                                    not null comment 'user_account.user_no 참조 (FK)',

    -- 2. 핵심 식별자
    jti          varchar(255)                              not null comment 'Refresh Token 고유 ID',
    platform     varchar(20)                               not null comment '플랫폼 [WEB, ANDROID, IOS]',

    -- [복구] 다시 device_id로 변경 (직관성 UP)
    -- 단, 물리적 기기 ID가 아니라 논리적 UUID임을 명시
    device_id    varchar(255)                              not null comment '기기 식별 UUID (App:설치시 생성, Web:브라우저 생성)',

    -- [분리 유지] 푸시 토큰은 따로 관리
    push_token   varchar(500)                              null comment 'FCM/APNs Token (알림용, 없으면 NULL)',

    -- 3. 메타 정보
    issued_at    timestamp(3)                              not null,
    expires_at   timestamp(3)                              not null,
    revoked      tinyint(1)   default 0                    not null,
    user_agent   varchar(500)                              null,
    ip           varchar(45)                               null,

    created_at   timestamp(3) default current_timestamp(3) not null,
    updated_at   timestamp(3) default current_timestamp(3) not null on update current_timestamp(3),

    -- PK: 유저 + 플랫폼 + 기기ID
    primary key (user_no, platform, device_id),

    constraint uk_user_session_jti unique (jti),
    constraint fk_session_to_account
        foreign key (user_no) references user_account (user_no)
            on delete cascade
) comment '사용자 로그인 세션';