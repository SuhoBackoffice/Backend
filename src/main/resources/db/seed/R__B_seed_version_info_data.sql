INSERT INTO version_info (id, name, loop_litz_wire, created_at, modified_at)
VALUES (1, 'v4.5', 151.5, now(), now()),
       (2, 'v7.0', 151.5, now(), now()),
       (3, 'v8.0', 150.0, now(), now()),
       (4, 'v8.0_연장', 150.0, now(), now());

INSERT INTO straight_bom_standard (version_id, condition_type, min_condition_value, max_condition_value,
                                   material_code, item_name, quantity, created_at, modified_at)
VALUES
    # 이상 <= x < 미만
    #V4.5 yoke 설치 파트
    (1, 'YOKE', 0.0, 400.0, 'D103-30128A', 'SUPPORT;YOKE STD', '0', now(), now()),
    (1, 'YOKE', 0.0, 400.0, 'D103-51330A', 'CLAMP;TAP PLATE 70X18', '0', now(), now()),
    (1, 'YOKE', 400.0, 1200.0, 'D103-30128A', 'SUPPORT;YOKE STD', '1', now(), now()),
    (1, 'YOKE', 400.0, 1200.0, 'D103-51330A', 'CLAMP;TAP PLATE 70X18', '2', now(), now()),
    (1, 'YOKE', 1200.0, 1600.0, 'D103-30128A', 'SUPPORT;YOKE STD', '2', now(), now()),
    (1, 'YOKE', 1200.0, 1600.0, 'D103-51330A', 'CLAMP;TAP PLATE 70X18', '4', now(), now()),
    (1, 'YOKE', 1600.0, 2800.0, 'D103-30128A', 'SUPPORT;YOKE STD', '3', now(), now()),
    (1, 'YOKE', 1600.0, 2800.0, 'D103-51330A', 'CLAMP;TAP PLATE 70X18', '6', now(), now()),
    (1, 'YOKE', 2800.0, 3601.0, 'D103-30128A', 'SUPPORT;YOKE STD', '4', now(), now()),
    (1, 'YOKE', 2800.0, 3601.0, 'D103-51330A', 'CLAMP;TAP PLATE 70X18', '8', now(), now()),

    #V4.5 litzwire 설치 파트
    (1, 'LITZ_WIRE', 1.0, 950.0, 'D103-30677A', 'CLAMP;CLAMP-B_4.0V', '2', now(), now()),
    (1, 'LITZ_WIRE', 1.0, 950.0, 'D105-60495A', 'STOP TAG NUT_(4.0V)', '2', now(), now()),
    (1, 'LITZ_WIRE', 950.0, 1845.0, 'D103-30677A', 'CLAMP;CLAMP-B_4.0V', '3', now(), now()),
    (1, 'LITZ_WIRE', 950.0, 1845.0, 'D105-60495A', 'STOP TAG NUT_(4.0V)', '3', now(), now()),

    #V4.5 Loop Litzwire 설치 파트
    (1, 'LOOP_LITZ_WIRE', 0, 0, 'D103-70966A', 'LOOP-LOOP YOKE', 1, now(), now()),
    (1, 'LOOP_LITZ_WIRE', 0, 0, 'D103-30672A', 'COVER;LOOP CABLE COVER_4.0V', 2, now(), now()),
    (1, 'LOOP_LITZ_WIRE', 0, 0, 'D103-30092A', 'NUT;LOOP TAP PLATE 40X18', 2, now(), now()),
    (1, 'LOOP_LITZ_WIRE', 0, 0, 'D103-30115A', 'NUT;LOOP COVER NUT', 4, now(), now()),
    (1, 'LOOP_LITZ_WIRE', 0, 0, 'D103-38516A', 'SUPPORT;LOOP LITZ WIRE SPT-L-V4.5V', 2, now(), now()),
    (1, 'LOOP_LITZ_WIRE', 0, 0, 'D103-38517A', 'SUPPORT;LOOP LITZ WIRE SPT-R-V4.5V', 2, now(), now()),
    (1, 'LOOP_LITZ_WIRE', 0, 0, 'D103-30677A', 'CLAMP;CLAMP-A_4.0V', 8, now(), now()),
    (1, 'LOOP_LITZ_WIRE', 0, 0, 'D105-60495A', 'STOP TAG NUT_(4.0V)', 8, now(), now()),

    #V7 yoke 설치 파트
    (2, 'YOKE', 0.0, 400.0, 'D108-44068A', 'SUPPORT YOKE STD', '0', now(), now()),
    (2, 'YOKE', 0.0, 400.0, 'D104-42652A', 'CLAMP;TAP PLATE 70X18', '0', now(), now()),
    (2, 'YOKE', 400.0, 1200.0, 'D108-44068A', 'SUPPORT YOKE STD', '1', now(), now()),
    (2, 'YOKE', 400.0, 1200.0, 'D104-42652A', 'CLAMP;TAP PLATE 70X18', '2', now(), now()),
    (2, 'YOKE', 1200.0, 1600.0, 'D108-44068A', 'SUPPORT YOKE STD', '2', now(), now()),
    (2, 'YOKE', 1200.0, 1600.0, 'D104-42652A', 'CLAMP;TAP PLATE 70X18', '4', now(), now()),
    (2, 'YOKE', 1600.0, 2800.0, 'D108-44068A', 'SUPPORT YOKE STD', '3', now(), now()),
    (2, 'YOKE', 1600.0, 2800.0, 'D104-42652A', 'CLAMP;TAP PLATE 70X18', '6', now(), now()),
    (2, 'YOKE', 2800.0, 3601.0, 'D108-44068A', 'SUPPORT YOKE STD', '4', now(), now()),
    (2, 'YOKE', 2800.0, 3601.0, 'D104-42652A', 'CLAMP;TAP PLATE 70X18', '8', now(), now()),

    #V7 Litzwire 설치 파트
    (2, 'LITZ_WIRE', 1.0, 950.0, 'D103-30677A', 'CLAMP;CLAMP-B_4.0V', '2', now(), now()),
    (2, 'LITZ_WIRE', 1.0, 950.0, 'D104-39922A', 'TAP PLATE 15X12', '2', now(), now()),
    (2, 'LITZ_WIRE', 950.0, 1845.0, 'D103-30677A', 'CLAMP;CLAMP-B_4.0V', '3', now(), now()),
    (2, 'LITZ_WIRE', 950.0, 1845.0, 'D104-39922A', 'TAP PLATE 15X12', '3', now(), now()),

    #V7 Loop Litzwire 설치 파트
    (2, 'LOOP_LITZ_WIRE', 0, 0, 'D104-33956A', 'YOKE-LOOP YOKE-1', 1, now(), now()),
    (2, 'LOOP_LITZ_WIRE', 0, 0, 'D103-87874A', 'COVER-CABLE COVER-5.0V', 2, now(), now()),
    (2, 'LOOP_LITZ_WIRE', 0, 0, 'D104-04995A', 'PLATE-LOOP YOKE TAP PLATE_V7.0', 2, now(), now()),
    (2, 'LOOP_LITZ_WIRE', 0, 0, 'D104-44058A', 'NUT-LOOP COVER NUT-M6 V7.0', 4, now(), now()),
    (2, 'LOOP_LITZ_WIRE', 0, 0, 'D104-29169A', 'LOOP LITZ WIRE SPT R_V7.0', 2, now(), now()),
    (2, 'LOOP_LITZ_WIRE', 0, 0, 'D104-29170A', 'LOOP LITZ WIRE SPT L_V7.0', 2, now(), now()),
    (2, 'LOOP_LITZ_WIRE', 0, 0, 'D103-30677A', 'CLAMP;CLAMP-B_4.0V', 8, now(), now()),
    (2, 'LOOP_LITZ_WIRE', 0, 0, 'D104-39922A', 'TAP PLATE 15X12', 8, now(), now()),

    #V8 yoke 설치 파트
    (3, 'YOKE', 0.0, 400.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '0', now(), now()),
    (3, 'YOKE', 0.0, 400.0, 'D107-78724A', 'TAP PLATE 80*16*8', '0', now(), now()),
    (3, 'YOKE', 400.0, 1200.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '1', now(), now()),
    (3, 'YOKE', 400.0, 1200.0, 'D107-78724A', 'TAP PLATE 80*16*8', '2', now(), now()),
    (3, 'YOKE', 1200.0, 1600.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '2', now(), now()),
    (3, 'YOKE', 1200.0, 1600.0, 'D107-78724A', 'TAP PLATE 80*16*8', '4', now(), now()),
    (3, 'YOKE', 1600.0, 2800.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '3', now(), now()),
    (3, 'YOKE', 1600.0, 2800.0, 'D107-78724A', 'TAP PLATE 80*16*8', '6', now(), now()),
    (3, 'YOKE', 2800.0, 3601.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '4', now(), now()),
    (3, 'YOKE', 2800.0, 3601.0, 'D107-78724A', 'TAP PLATE 80*16*8', '8', now(), now()),

    #V8 Litzwire 설치 파트
    (3, 'LITZ_WIRE', 1.0, 950.0, 'S054-0340-142-01', 'ONE TOUCH ROTATE', '2', now(), now()),
    (3, 'LITZ_WIRE', 1.0, 950.0, 'S054-0340-141-01', 'ONE TOUCH BRACKET', '2', now(), now()),
    (3, 'LITZ_WIRE', 950.0, 1845.0, 'S054-0340-142-01', 'ONE TOUCH ROTATE', '3', now(), now()),
    (3, 'LITZ_WIRE', 950.0, 1845.0, 'S054-0340-141-01', 'ONE TOUCH BRACKET', '3', now(), now()),

    #V8 Loop Litzwire 설치 파트
    (3, 'LOOP_LITZ_WIRE', 0, 0, '186-0205101-00', 'CABLE COVER', 2, now(), now()),
    (3, 'LOOP_LITZ_WIRE', 0, 0, '186-0213303-00', 'LOOP LITZ WIRE SPT STD', 2, now(), now()),
    (3, 'LOOP_LITZ_WIRE', 0, 0, '186-0213304-00', 'LOOP LITZ WIRE SPT STD_MIR', 2, now(), now()),
    (3, 'LOOP_LITZ_WIRE', 0, 0, 'D107-78726A', 'LOOP TAP PLATE 80X16X8', 2, now(), now()),
    (3, 'LOOP_LITZ_WIRE', 0, 0, 'D107-78740A', 'TAP PLATE 15X12X6', 4, now(), now()),
    (3, 'LOOP_LITZ_WIRE', 0, 0, '186-0205105-00', 'LOOP YOKE 8.0V', 1, now(), now()),
    (3, 'LOOP_LITZ_WIRE', 0, 0, 'S054-0340-142-01', 'ONE TOUCH ROTATE', 8, now(), now()),
    (3, 'LOOP_LITZ_WIRE', 0, 0, 'S054-0340-141-01', 'ONE TOUCH BRACKET', 8, now(), now()),

    #V8-연장 yoke 설치 파트
    (4, 'YOKE', 0.0, 400.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '0', now(), now()),
    (4, 'YOKE', 0.0, 400.0, 'D107-78724A', 'TAP PLATE 80*16*8', '0', now(), now()),
    (4, 'YOKE', 400.0, 1200.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '1', now(), now()),
    (4, 'YOKE', 400.0, 1200.0, 'D107-78724A', 'TAP PLATE 80*16*8', '2', now(), now()),
    (4, 'YOKE', 1200.0, 1600.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '2', now(), now()),
    (4, 'YOKE', 1200.0, 1600.0, 'D107-78724A', 'TAP PLATE 80*16*8', '4', now(), now()),
    (4, 'YOKE', 1600.0, 2800.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '3', now(), now()),
    (4, 'YOKE', 1600.0, 2800.0, 'D107-78724A', 'TAP PLATE 80*16*8', '6', now(), now()),
    (4, 'YOKE', 2800.0, 3601.0, 'D106-76642A', 'YOKE STANDARD_V8.0', '4', now(), now()),
    (4, 'YOKE', 2800.0, 3601.0, 'D107-78724A', 'TAP PLATE 80*16*8', '8', now(), now()),

    #V8-연장 Litzwire 설치 파트
    (4, 'LITZ_WIRE', 1.0, 950.0, 'S054-0340-142-01', 'ONE TOUCH ROTATE', '2', now(), now()),
    (4, 'LITZ_WIRE', 1.0, 950.0, 'S054-0340-141-01', 'ONE TOUCH BRACKET', '2', now(), now()),
    (4, 'LITZ_WIRE', 950.0, 1845.0, 'S054-0340-142-01', 'ONE TOUCH ROTATE', '3', now(), now()),
    (4, 'LITZ_WIRE', 950.0, 1845.0, 'S054-0340-141-01', 'ONE TOUCH BRACKET', '3', now(), now()),

    #V8-연장 Loop Litzwire 설치 파트
    (4, 'LOOP_LITZ_WIRE', 0, 0, '186-0205101-00', 'CABLE COVER', 2, now(), now()),
    (4, 'LOOP_LITZ_WIRE', 0, 0, '186-0213303-00', 'LOOP LITZ WIRE SPT STD', 2, now(), now()),
    (4, 'LOOP_LITZ_WIRE', 0, 0, '186-0213304-00', 'LOOP LITZ WIRE SPT STD_MIR', 2, now(), now()),
    (4, 'LOOP_LITZ_WIRE', 0, 0, 'D107-78726A', 'LOOP TAP PLATE 80X16X8', 2, now(), now()),
    (4, 'LOOP_LITZ_WIRE', 0, 0, 'D107-78740A', 'TAP PLATE 15X12X6', 4, now(), now()),
    (4, 'LOOP_LITZ_WIRE', 0, 0, '186-0205105-00', 'LOOP YOKE 8.0V', 1, now(), now()),
    (4, 'LOOP_LITZ_WIRE', 0, 0, 'S054-0340-142-01', 'ONE TOUCH ROTATE', 8, now(), now()),
    (4, 'LOOP_LITZ_WIRE', 0, 0, 'S054-0340-141-01', 'ONE TOUCH BRACKET', 8, now(), now())
;