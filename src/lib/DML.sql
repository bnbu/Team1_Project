-- 시퀀스 생성 --
create sequence movie_seq
increment by 1
;

create sequence screening_info_seq
increment by 1
;

create sequence ticketing_seq
increment by 1
;

create sequence ticketing_seq
increment by 1
;
----------------------------------------
-- MOVIE 영화 정보 추가 --
INSERT INTO MOVIE VALUES (
movie_seq.nextval,
'극장판 짱구는 못말려: 동물소환 닌자 배꼽수비대',
100,
'하시모토 마사카즈',
'애니메이션',
0,
'2023/05/04'
);
----------------------------------------
-- 상영정보 추가 --
INSERT INTO SCREENING_INFO VALUES (
581315||screening_info_seq.nextval ,
58,
1,
to_date('2023-05-23 13:15:00', 'yyyy-mm-dd hh24:mi:ss'),
to_date('2023-05-23 14:45:00', 'yyyy-mm-dd hh24:mi:ss'),
'2023/05/23'
);
----------------------------------------
-- 상영관 정보 추가--
INSERT INTO THEATER VALUES(1,10,10);
INSERT INTO THEATER VALUES(2,10,15);
INSERT INTO THEATER VALUES(3,10,20);
INSERT INTO THEATER VALUES(4,15,20);
----------------------------------------
-- 회원 정보 추가--
INSERT INTO MEMBER (member_id, member_name, member_pwd, member_phone, member_birthday) 
VALUES('tester1','테스터','1q2w3e4r!','010-3333-44444','950101');
----------------------------------------
-- 어드민 정보 추가--
INSERT INTO MEMBER (member_id, member_name, member_pwd, member_phone, member_birthday) 
VALUES('admin1','admin','1q2w3e4r!','010-2222-3333','000101');
INSERT INTO ADMIN VALUES('admin1',15); -- 전체 권한
INSERT INTO MEMBER (member_id, member_name, member_pwd, member_phone, member_birthday) 
VALUES('admin2','admin','1q2w3e4r!','010-2222-3333','000101');
INSERT INTO ADMIN VALUES('admin1',7); -- 취소권한만 뺴고 모두

