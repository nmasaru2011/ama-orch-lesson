-- =============================================
-- correction テーブル（音楽用語の誤字修正マッピング）
-- sort_order で適用順序を保持
-- =============================================

-- 楽器名
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (1, '倍りの皆さん', 'ヴァイオリンの皆さん');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (2, '倍り', 'ヴァイオリン');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (3, 'バイオリン', 'ヴァイオリン');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (4, 'ファーストバイリン', 'ファースト・ヴァイオリン');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (5, 'セカンドバイリン', 'セカンド・ヴァイオリン');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (6, 'ファーストバイ', 'ファースト・ヴァイオリン');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (7, 'セカンドバイ', 'セカンド・ヴァイオリン');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (8, 'バイベル', 'ヴィオラ');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (9, 'ライ、家', 'ヴィオラ');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (10, 'ラグループ', 'ヴィオラグループ');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (11, '原学器の皆さん', '弦楽器の皆さん');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (12, '原学期の皆さん', '弦楽器の皆さん');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (13, '現の皆さん', '弦の皆さん');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (14, '原学器', '弦楽器');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (15, '原学期', '弦楽器');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (16, '現学器', '弦楽器');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (17, '間学器', '管楽器');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (18, '半学器', '管楽器');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (19, '木の皆さん', '木管の皆さん');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (20, 'パストリ', 'ファゴット');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (21, 'パスト', 'ファゴット');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (22, '金貨', '金管');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (23, '金官', '金管');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (24, '家の皆さん', 'ヴィオラの皆さん');
-- 小節・拍
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (25, '小説', '小節');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (26, '章説明', '小節');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (27, '泊目', '拍目');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (28, '泊', '拍');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (29, '2分オプ', '2拍目');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (30, '2分オパ', '2拍目');
-- 音楽記号・奏法
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (31, 'スラースタックカート', 'スラー・スタッカート');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (32, 'デガート', 'スタッカート');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (33, 'ポルテ', 'フォルテ');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (34, 'ホルテ', 'フォルテ');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (35, 'コルテシ', 'フォルテッシモ');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (36, 'ゴルテ', 'フォルテ');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (37, 'スコーザ', 'スフォルツァンド');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (38, 'プレシエンド', 'クレッシェンド');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (39, 'プレッシ', 'クレッシェンド');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (40, 'クレシェ', 'クレッシェンド');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (41, 'クレシント', 'クレッシェンド');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (42, 'クレシェード', 'クレッシェンド');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (43, 'ディエンド', 'ディミヌエンド');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (44, 'リネンド', 'ディミヌエンド');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (45, 'メザフォルテ', 'メゾフォルテ');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (46, 'ピアニ', 'ピアノ');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (47, 'ピアニッシュ', 'ピアニッシモ');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (48, 'ピアニシ', 'ピアニッシモ');
-- 作曲家名
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (49, 'グループな', 'ブルックナー');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (50, 'ドボルザーク', 'ドヴォルザーク');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (51, '美容ら引き', 'ヴィオラ弾き');
-- その他
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (52, '渋音符', '16音符');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (53, '学譜', '楽譜');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (54, '学法', '楽譜');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (55, 'マニクス', '手書き譜');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (56, '実筆', '自筆');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (57, '店舗', 'テンポ');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (58, '竹合', '掛け合い');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (59, '対当', '対等');
INSERT INTO correction (sort_order, wrong_text, correct_text) VALUES (60, '余因', '余韻');

-- =============================================
-- instrument_keyword テーブル（楽器キーワード定義）
-- canonical_name: 正式名称, keyword: 検索キーワード（バリエーション）
-- =============================================

INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ファースト・ヴァイオリン', 'ファーストバイオリン');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ファースト・ヴァイオリン', 'ファーストバイリン');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ファースト・ヴァイオリン', 'ファーストバイ');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ファースト・ヴァイオリン', 'ファースト');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('セカンド・ヴァイオリン', 'セカンドバイオリン');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('セカンド・ヴァイオリン', 'セカンドバイリン');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('セカンド・ヴァイオリン', 'セカンドバイ');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('セカンド・ヴァイオリン', 'セカンド');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ヴァイオリン', 'バイオリン');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ヴァイオリン', 'ヴァイオリン');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ヴィオラ', 'ヴィオラ');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ヴィオラ', 'バイベル');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ヴィオラ', 'ライ、家');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ヴィオラ', 'ラの皆さん');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ヴィオラ', '家の皆さん');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('チェロ', 'チェロ');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('コントラバス', 'コントラバス');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('コントラバス', 'ベース');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('弦楽器', '弦楽器');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('弦楽器', '現学器');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('弦楽器', '原学器');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('弦楽器', '弦の皆さん');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('木管', '木管');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('木管', '木の皆さん');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('金管', '金管');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('金管', '金官');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('金管', '金貨');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('フルート', 'フルート');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('オーボエ', 'オーボエ');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('クラリネット', 'クラリネット');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ファゴット', 'ファゴット');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ファゴット', 'パストリ');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ファゴット', 'パスト');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('ホルン', 'ホルン');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('トランペット', 'トランペット');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('トロンボーン', 'トロンボーン');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('トロンボーン', 'バストロ');
INSERT INTO instrument_keyword (canonical_name, keyword) VALUES ('チューバ', 'チューバ');

-- =============================================
-- include_keyword テーブル（意味のある指摘に含まれるべきキーワード）
-- =============================================

INSERT INTO include_keyword (keyword) VALUES ('お願い');
INSERT INTO include_keyword (keyword) VALUES ('ください');
INSERT INTO include_keyword (keyword) VALUES ('注意');
INSERT INTO include_keyword (keyword) VALUES ('気をつけ');
INSERT INTO include_keyword (keyword) VALUES ('切らない');
INSERT INTO include_keyword (keyword) VALUES ('切って');
INSERT INTO include_keyword (keyword) VALUES ('大きく');
INSERT INTO include_keyword (keyword) VALUES ('小さく');
INSERT INTO include_keyword (keyword) VALUES ('もっと');
INSERT INTO include_keyword (keyword) VALUES ('しっかり');
INSERT INTO include_keyword (keyword) VALUES ('ちゃんと');
INSERT INTO include_keyword (keyword) VALUES ('フォルテ');
INSERT INTO include_keyword (keyword) VALUES ('ピアノ');
INSERT INTO include_keyword (keyword) VALUES ('メゾ');
INSERT INTO include_keyword (keyword) VALUES ('フォルツ');
INSERT INTO include_keyword (keyword) VALUES ('クレッシェンド');
INSERT INTO include_keyword (keyword) VALUES ('ディミヌエンド');
INSERT INTO include_keyword (keyword) VALUES ('歌って');
INSERT INTO include_keyword (keyword) VALUES ('返');
INSERT INTO include_keyword (keyword) VALUES ('アクセント');
INSERT INTO include_keyword (keyword) VALUES ('小節');
INSERT INTO include_keyword (keyword) VALUES ('拍');
INSERT INTO include_keyword (keyword) VALUES ('聞こえ');
INSERT INTO include_keyword (keyword) VALUES ('バランス');
INSERT INTO include_keyword (keyword) VALUES ('遅れ');
INSERT INTO include_keyword (keyword) VALUES ('早');
INSERT INTO include_keyword (keyword) VALUES ('合わせ');

-- =============================================
-- exclude_pattern テーブル（除外パターン：正規表現）
-- =============================================

INSERT INTO exclude_pattern (pattern) VALUES ('^[12あおうえはせの]+$');
INSERT INTO exclude_pattern (pattern) VALUES ('^[ワンツー]+$');
INSERT INTO exclude_pattern (pattern) VALUES ('よろしく');
INSERT INTO exclude_pattern (pattern) VALUES ('おはよう');
INSERT INTO exclude_pattern (pattern) VALUES ('お疲れ');
INSERT INTO exclude_pattern (pattern) VALUES ('ありがとう');
INSERT INTO exclude_pattern (pattern) VALUES ('休憩');
INSERT INTO exclude_pattern (pattern) VALUES ('終わり');
INSERT INTO exclude_pattern (pattern) VALUES ('^[0-9]+$');
INSERT INTO exclude_pattern (pattern) VALUES ('^[あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん]{1,3}$');

-- =============================================
-- measure_pattern テーブル（小節番号抽出パターン：正規表現）
-- =============================================

INSERT INTO measure_pattern (pattern) VALUES ('(\d+)\s*小節');
INSERT INTO measure_pattern (pattern) VALUES ('(\d+)\s*章説明');
INSERT INTO measure_pattern (pattern) VALUES ('(\d+)\s*から');
INSERT INTO measure_pattern (pattern) VALUES ('[A-Z]\s*の\s*(\d+)');

-- =============================================
-- rehearsal_mark_pattern テーブル（練習番号パターン：正規表現）
-- =============================================

INSERT INTO rehearsal_mark_pattern (pattern) VALUES ('\b([A-Z])\b(?=の|から|入|まで)');
