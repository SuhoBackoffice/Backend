INSERT INTO version_info (id, name, loop_litz_wire, created_at, modified_at)
VALUES (1, 'v4.5', 151.5, now(), now()),
       (2, 'v7.0', 151.5, now(), now()),
       (3, 'v8.0', 150.0, now(), now()),
       (4, 'v8.0_연장', 150.0, now(), now());

INSERT INTO straight_bom_standard (id, version_id, condition_type, min_condition_value, max_condition_value,
                                   material_code, item_name, quantity, created_at, modified_at)
VALUES
    # 이상 <= x < 미만
    #V4.5 yoke 설치 파트
    (1, 1, 'YOKE', 0.0, 400.0, 'D103-30128A', 'SUPPORT;YOKE STD', '0', now(), now()),
    (2, 1, 'YOKE', 0.0, 400.0, 'D103-51330A', 'CLAMP;TAP PLATE 70X18', '0', now(), now()),
    (3, 1, 'YOKE', 400.0, 1200.0, 'D103-30128A', 'SUPPORT;YOKE STD', '1', now(), now()),
    (4, 1, 'YOKE', 400.0, 1200.0, 'D103-51330A', 'CLAMP;TAP PLATE 70X18', '2', now(), now()),
    (5, 1, 'YOKE', 1200.0, 1600.0, 'D103-30128A', 'SUPPORT;YOKE STD', '2', now(), now()),
    (6, 1, 'YOKE', 1200.0, 1600.0, 'D103-51330A', 'CLAMP;TAP PLATE 70X18', '4', now(), now()),
    (7, 1, 'YOKE', 1600.0, 2800.0, 'D103-30128A', 'SUPPORT;YOKE STD', '3', now(), now()),
    (8, 1, 'YOKE', 1600.0, 2800.0, 'D103-51330A', 'CLAMP;TAP PLATE 70X18', '6', now(), now()),
    (9, 1, 'YOKE', 2800.0, 3601.0, 'D103-30128A', 'SUPPORT;YOKE STD', '4', now(), now()),
    (10, 1, 'YOKE', 2800.0, 3601.0, 'D103-51330A', 'CLAMP;TAP PLATE 70X18', '8', now(), now()),
    #V4.5 litzwire 설치 파트
    (11, 1, 'LITZ_WIRE', 1.0, 950.0, 'D103-30677A', 'CLAMP;CLAMP-B_4.0V', '2', now(), now()),
    (12, 1, 'LITZ_WIRE', 1.0, 950.0, 'D105-60495A', 'STOP TAG NUT_(4.0V)', '2', now(), now()),
    (13, 1, 'LITZ_WIRE', 950.0, 1845.0, 'D103-30677A', 'CLAMP;CLAMP-B_4.0V', '3', now(), now()),
    (14, 1, 'LITZ_WIRE', 950.0, 1845.0, 'D105-60495A', 'STOP TAG NUT_(4.0V)', '3', now(), now()),

    #V7 yoke 설치 파트
    (15, 2, 'YOKE', 0.0, 400.0, 'D108-44068A', 'SUPPORT YOKE STD', '0', now(), now()),
    (16, 2, 'YOKE', 0.0, 400.0, 'D104-42652A', 'CLAMP;TAP PLATE 70X18', '0', now(), now()),
    (17, 2, 'YOKE', 400.0, 1200.0, 'D108-44068A', 'SUPPORT YOKE STD', '1', now(), now()),
    (18, 2, 'YOKE', 400.0, 1200.0, 'D104-42652A', 'CLAMP;TAP PLATE 70X18', '2', now(), now()),
    (19, 2, 'YOKE', 1200.0, 1600.0, 'D108-44068A', 'SUPPORT YOKE STD', '2', now(), now()),
    (20, 2, 'YOKE', 1200.0, 1600.0, 'D104-42652A', 'CLAMP;TAP PLATE 70X18', '4', now(), now()),
    (21, 2, 'YOKE', 1600.0, 2800.0, 'D108-44068A', 'SUPPORT YOKE STD', '3', now(), now()),
    (22, 2, 'YOKE', 1600.0, 2800.0, 'D104-42652A', 'CLAMP;TAP PLATE 70X18', '6', now(), now()),
    (23, 2, 'YOKE', 2800.0, 3601.0, 'D108-44068A', 'SUPPORT YOKE STD', '4', now(), now()),
    (24, 2, 'YOKE', 2800.0, 3601.0, 'D104-42652A', 'CLAMP;TAP PLATE 70X18', '8', now(), now()),

    #V7 Litzwire 설치 파트
    (25, 2, 'LITZ_WIRE', 1.0, 950.0, 'D103-30677A', 'CLAMP;CLAMP-B_4.0V', '2', now(), now()),
    (26, 2, 'LITZ_WIRE', 1.0, 950.0, 'D104-39922A', 'TAP PLATE 15X12', '2', now(), now()),
    (27, 2, 'LITZ_WIRE', 950.0, 1845.0, 'D103-30677A', 'CLAMP;CLAMP-B_4.0V', '3', now(), now()),
    (28, 2, 'LITZ_WIRE', 950.0, 1845.0, 'D104-39922A', 'TAP PLATE 15X12', '3', now(), now()),

    #V8 yoke 설치 파트
    (29, 3, 'YOKE', 0.0, 400.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '0', now(), now()),
    (30, 3, 'YOKE', 0.0, 400.0, 'D107-78724A', 'TAP PLATE 80*16*8', '0', now(), now()),
    (31, 3, 'YOKE', 400.0, 1200.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '1', now(), now()),
    (32, 3, 'YOKE', 400.0, 1200.0, 'D107-78724A', 'TAP PLATE 80*16*8', '2', now(), now()),
    (33, 3, 'YOKE', 1200.0, 1600.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '2', now(), now()),
    (34, 3, 'YOKE', 1200.0, 1600.0, 'D107-78724A', 'TAP PLATE 80*16*8', '4', now(), now()),
    (35, 3, 'YOKE', 1600.0, 2800.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '3', now(), now()),
    (36, 3, 'YOKE', 1600.0, 2800.0, 'D107-78724A', 'TAP PLATE 80*16*8', '6', now(), now()),
    (37, 3, 'YOKE', 2800.0, 3601.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '4', now(), now()),
    (38, 3, 'YOKE', 2800.0, 3601.0, 'D107-78724A', 'TAP PLATE 80*16*8', '8', now(), now()),

    #V8 Litzwire 설치 파트
    (39, 3, 'LITZ_WIRE', 1.0, 950.0, 'S054-0340-142-01', 'ONE TOUCH ROTATE ', '2', now(), now()),
    (40, 3, 'LITZ_WIRE', 1.0, 950.0, 'S054-0340-141-01', 'ONE TOUCH BRACKET', '2', now(), now()),
    (41, 3, 'LITZ_WIRE', 950.0, 1845.0, 'S054-0340-142-01', 'ONE TOUCH ROTATE ', '3', now(), now()),
    (42, 3, 'LITZ_WIRE', 950.0, 1845.0, 'S054-0340-141-01', 'ONE TOUCH BRACKET', '3', now(), now()),

    #V8-연장 yoke 설치 파트
    (43, 4, 'YOKE', 0.0, 400.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '0', now(), now()),
    (44, 4, 'YOKE', 0.0, 400.0, 'D107-78724A', 'TAP PLATE 80*16*8', '0', now(), now()),
    (45, 4, 'YOKE', 400.0, 1200.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '1', now(), now()),
    (46, 4, 'YOKE', 400.0, 1200.0, 'D107-78724A', 'TAP PLATE 80*16*8', '2', now(), now()),
    (47, 4, 'YOKE', 1200.0, 1600.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '2', now(), now()),
    (48, 4, 'YOKE', 1200.0, 1600.0, 'D107-78724A', 'TAP PLATE 80*16*8', '4', now(), now()),
    (49, 4, 'YOKE', 1600.0, 2800.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '3', now(), now()),
    (50, 4, 'YOKE', 1600.0, 2800.0, 'D107-78724A', 'TAP PLATE 80*16*8', '6', now(), now()),
    (51, 4, 'YOKE', 2800.0, 3601.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '4', now(), now()),
    (52, 4, 'YOKE', 2800.0, 3601.0, 'D107-78724A', 'TAP PLATE 80*16*8', '8', now(), now()),

    #V8 Litzwire 설치 파트
    (53, 4, 'LITZ_WIRE', 1.0, 950.0, 'S054-0340-142-01', 'ONE TOUCH ROTATE ', '2', now(), now()),
    (54, 4, 'LITZ_WIRE', 1.0, 950.0, 'S054-0340-141-01', 'ONE TOUCH BRACKET', '2', now(), now()),
    (55, 4, 'LITZ_WIRE', 950.0, 1845.0, 'S054-0340-142-01', 'ONE TOUCH ROTATE ', '3', now(), now()),
    (56, 4, 'LITZ_WIRE', 950.0, 1845.0, 'S054-0340-141-01', 'ONE TOUCH BRACKET', '3', now(), now());