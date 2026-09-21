-- =============================================
-- 20260921_rename_tables_and_add_fk.sql（PostgreSQL 専用マイグレーション）
--   ・テーブル名の変更
--   ・concert_program / concert_lesson から concert への外部キー追加
--   ・concert_lesson.concert_main_id → concert_id への置換
-- 単一トランザクションで実行すること。変更前のデータは bak_20260921 スキーマに退避する。
-- =============================================

-- 0. 退避（変更・削除されるデータのバックアップ）
CREATE SCHEMA bak_20260921;
CREATE TABLE bak_20260921.orch AS SELECT * FROM orch;
CREATE TABLE bak_20260921.orch_data AS SELECT * FROM orch_data;
CREATE TABLE bak_20260921.concert AS SELECT * FROM concert;
CREATE TABLE bak_20260921.concert_data AS SELECT * FROM concert_data;
CREATE TABLE bak_20260921.concert_program AS SELECT * FROM concert_program;
CREATE TABLE bak_20260921.lesson_data AS SELECT * FROM lesson_data;

-- 1. 名称の衝突する旧テーブルを削除（concert: 0件の旧構造 / orch: orch_data の旧い写し）
DROP TABLE concert;
DROP TABLE orch;

-- 2. テーブル名の変更
ALTER TABLE orch_data RENAME TO orch_mst;
ALTER TABLE concert_data RENAME TO concert;
ALTER TABLE lesson_data RENAME TO concert_lesson;
ALTER TABLE music RENAME TO music_mst;
ALTER TABLE correction RENAME TO rehea_analysis_correction_mst;
ALTER TABLE instrument_keyword RENAME TO rehea_analysis_instrument_keyword_mst;
ALTER TABLE include_keyword RENAME TO rehea_analysis_include_keyword_mst;
ALTER TABLE exclude_pattern RENAME TO rehea_analysis_exclude_pattern_mst;
ALTER TABLE measure_pattern RENAME TO rehea_analysis_measure_pattern_mst;
ALTER TABLE rehearsal_mark_pattern RENAME TO rehea_analysis_mark_pattern_mst;

-- 3. 制約名・シーケンス名を新テーブル名に合わせる
ALTER TABLE orch_mst RENAME CONSTRAINT orch_data_pkey TO orch_mst_pkey;
ALTER TABLE orch_mst RENAME CONSTRAINT orch_data_orch_name_key TO orch_mst_orch_name_key;
ALTER TABLE concert RENAME CONSTRAINT concert_data_pkey TO concert_pkey;
ALTER TABLE concert_lesson RENAME CONSTRAINT lesson_data_pkey TO concert_lesson_pkey;
ALTER SEQUENCE IF EXISTS lesson_data_id_seq RENAME TO concert_lesson_id_seq;
ALTER TABLE music_mst RENAME CONSTRAINT music_pkey TO music_mst_pkey;
ALTER TABLE rehea_analysis_correction_mst RENAME CONSTRAINT correction_pkey TO rehea_analysis_correction_mst_pkey;
ALTER TABLE rehea_analysis_correction_mst RENAME CONSTRAINT correction_wrong_text_key TO rehea_analysis_correction_mst_uk;
ALTER SEQUENCE IF EXISTS correction_id_seq RENAME TO rehea_analysis_correction_mst_id_seq;
ALTER TABLE rehea_analysis_instrument_keyword_mst RENAME CONSTRAINT instrument_keyword_pkey TO rehea_analysis_instrument_keyword_mst_pkey;
ALTER TABLE rehea_analysis_instrument_keyword_mst RENAME CONSTRAINT instrument_keyword_canonical_name_keyword_key TO rehea_analysis_instrument_keyword_mst_uk;
ALTER SEQUENCE IF EXISTS instrument_keyword_id_seq RENAME TO rehea_analysis_instrument_keyword_mst_id_seq;
ALTER TABLE rehea_analysis_include_keyword_mst RENAME CONSTRAINT include_keyword_pkey TO rehea_analysis_include_keyword_mst_pkey;
ALTER TABLE rehea_analysis_include_keyword_mst RENAME CONSTRAINT include_keyword_keyword_key TO rehea_analysis_include_keyword_mst_uk;
ALTER SEQUENCE IF EXISTS include_keyword_id_seq RENAME TO rehea_analysis_include_keyword_mst_id_seq;
ALTER TABLE rehea_analysis_exclude_pattern_mst RENAME CONSTRAINT exclude_pattern_pkey TO rehea_analysis_exclude_pattern_mst_pkey;
ALTER TABLE rehea_analysis_exclude_pattern_mst RENAME CONSTRAINT exclude_pattern_pattern_key TO rehea_analysis_exclude_pattern_mst_uk;
ALTER SEQUENCE IF EXISTS exclude_pattern_id_seq RENAME TO rehea_analysis_exclude_pattern_mst_id_seq;
ALTER TABLE rehea_analysis_measure_pattern_mst RENAME CONSTRAINT measure_pattern_pkey TO rehea_analysis_measure_pattern_mst_pkey;
ALTER TABLE rehea_analysis_measure_pattern_mst RENAME CONSTRAINT measure_pattern_pattern_key TO rehea_analysis_measure_pattern_mst_uk;
ALTER SEQUENCE IF EXISTS measure_pattern_id_seq RENAME TO rehea_analysis_measure_pattern_mst_id_seq;
ALTER TABLE rehea_analysis_mark_pattern_mst RENAME CONSTRAINT rehearsal_mark_pattern_pkey TO rehea_analysis_mark_pattern_mst_pkey;
ALTER TABLE rehea_analysis_mark_pattern_mst RENAME CONSTRAINT rehearsal_mark_pattern_pattern_key TO rehea_analysis_mark_pattern_mst_uk;
ALTER SEQUENCE IF EXISTS rehearsal_mark_pattern_id_seq RENAME TO rehea_analysis_mark_pattern_mst_id_seq;

-- 4. 外部キーを張れない孤立データの補正
--    concert_program: 枝番違いで別の演奏会IDを指していた行を、その公演の実在する演奏会IDへ付け替える
UPDATE concert_program SET concert_id = '224121YP013' WHERE concert_id = '224121YP010';
UPDATE concert_program SET concert_id = '225121YP012' WHERE concert_id = '225121YP010';

-- 5. concert_lesson: concert_main_id → concert_id
ALTER TABLE concert_lesson ADD COLUMN concert_id VARCHAR(12);
--    (a) 演奏会メインID + 枝番0 の演奏会
UPDATE concert_lesson l SET concert_id = c.concert_id
  FROM concert c WHERE c.concert_main_id = l.concert_main_id AND c.concert_sub_id = '0';
--    (b) 枝番0が無く、演奏会が1件に特定できるもの
UPDATE concert_lesson l SET concert_id = (SELECT min(c.concert_id) FROM concert c WHERE c.concert_main_id = l.concert_main_id)
  WHERE l.concert_id IS NULL
    AND (SELECT count(*) FROM concert c WHERE c.concert_main_id = l.concert_main_id) = 1;
--    (c) 該当する演奏会が無い練習データは削除（バックアップは bak_20260921.lesson_data）
DELETE FROM concert_lesson WHERE concert_id IS NULL;
ALTER TABLE concert_lesson ALTER COLUMN concert_id SET NOT NULL;
ALTER TABLE concert_lesson DROP COLUMN concert_main_id;

-- 6. 外部キー
ALTER TABLE concert_program ADD CONSTRAINT fk_concert_program_concert FOREIGN KEY (concert_id) REFERENCES concert (concert_id);
ALTER TABLE concert_lesson ADD CONSTRAINT fk_concert_lesson_concert FOREIGN KEY (concert_id) REFERENCES concert (concert_id);
CREATE INDEX idx_concert_lesson_concert ON concert_lesson (concert_id);
