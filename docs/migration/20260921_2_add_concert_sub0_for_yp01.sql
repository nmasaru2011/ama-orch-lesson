-- =============================================
-- 20260921_2_add_concert_sub0_for_yp01.sql（PostgreSQL 用）
--   コンサートプログラムが元々参照していた 224121YP010 / 225121YP010（枝番0）を
--   concert に新規作成し、プログラムを元のIDに戻す。
--   内容は同一公演の既存データ（224121YP013 / 225121YP012）を複製して枝番0にしたもの。
--   concert_lesson（225121YP012 の10件）はそのまま。
-- =============================================
INSERT INTO concert (concert_id, orch_id, concert_main_id, concert_sub_id, concert_name, music_descript,
                     conductor_id, conductor_name, concert_num, hall_id, hall_branch_id, place_name,
                     concert_date, open_time, open_space, price, price_under_cond, price_under, teket_url,
                     create_datetime, update_datetime, update_by)
SELECT concert_main_id || '0', orch_id, concert_main_id, '0', concert_name, music_descript,
       conductor_id, conductor_name, concert_num, hall_id, hall_branch_id, place_name,
       concert_date, open_time, open_space, price, price_under_cond, price_under, teket_url,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system'
FROM concert
WHERE concert_id IN ('224121YP013', '225121YP012');

UPDATE concert_program SET concert_id = '224121YP010' WHERE concert_id = '224121YP013';
UPDATE concert_program SET concert_id = '225121YP010' WHERE concert_id = '225121YP012';

SELECT concert_id, concert_sub_id, concert_name, concert_date, conductor_name FROM concert WHERE concert_main_id IN ('224121YP01','225121YP01') ORDER BY 1;
SELECT concert_id, count(*) FROM concert_program WHERE concert_id LIKE '2%1YP01%' AND concert_id IN ('224121YP010','224121YP013','225121YP010','225121YP012') GROUP BY 1 ORDER BY 1;
