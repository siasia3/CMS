-- 테스트 계정 (비밀번호는 BCrypt 인코딩된 값)
-- admin 계정: username=admin, password=admin1234
-- user1 계정: username=user1, password=user1234
insert into users (username, password, role, nickname, created_date)
values ('admin', '$2a$10$yT.IoTpvLj2AOV9l4slaGePqqP4AYKcDUVofTBA2LpoXoexvOWWZq', 'ADMIN', '관리자', now()),
       ('user1', '$2a$10$AeMwKYvvyVs9WRQHSklCCeOSO2WxhacXXCWYb1Ow54GAU0MxXEHdC', 'USER',  '일반사용자', now());

-- 테스트 콘텐츠
insert into contents (title, description, view_count, created_by, user_id, created_date)
values ('첫 번째 콘텐츠', '관리자가 작성한 첫 번째 콘텐츠입니다.', 0, 'admin', 1, now()),
       ('두 번째 콘텐츠', '관리자가 작성한 두 번째 콘텐츠입니다.', 0, 'admin', 1, now()),
       ('세 번째 콘텐츠', '일반 사용자가 작성한 콘텐츠입니다.',   0, 'user1', 2, now());
