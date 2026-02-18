-- =============================================
-- correction テーブル（音楽用語の誤字修正マッピング）
-- sort_order で適用順序を保持
-- =============================================

-- 楽器名
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 1, '倍りの皆さん', 'ヴァイオリンの皆さん' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '倍りの皆さん');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 2, '倍り', 'ヴァイオリン' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '倍り');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 3, 'バイオリン', 'ヴァイオリン' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'バイオリン');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 4, 'ファーストバイリン', 'ファースト・ヴァイオリン' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'ファーストバイリン');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 5, 'セカンドバイリン', 'セカンド・ヴァイオリン' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'セカンドバイリン');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 6, 'ファーストバイ', 'ファースト・ヴァイオリン' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'ファーストバイ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 7, 'セカンドバイ', 'セカンド・ヴァイオリン' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'セカンドバイ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 8, 'バイベル', 'ヴィオラ' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'バイベル');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 9, 'ライ、家', 'ヴィオラ' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'ライ、家');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 10, 'ラグループ', 'ヴィオラグループ' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'ラグループ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 11, '原学器の皆さん', '弦楽器の皆さん' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '原学器の皆さん');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 12, '原学期の皆さん', '弦楽器の皆さん' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '原学期の皆さん');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 13, '現の皆さん', '弦の皆さん' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '現の皆さん');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 14, '原学器', '弦楽器' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '原学器');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 15, '原学期', '弦楽器' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '原学期');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 16, '現学器', '弦楽器' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '現学器');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 17, '間学器', '管楽器' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '間学器');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 18, '半学器', '管楽器' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '半学器');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 19, '木の皆さん', '木管の皆さん' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '木の皆さん');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 20, 'パストリ', 'ファゴット' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'パストリ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 21, 'パスト', 'ファゴット' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'パスト');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 22, '金貨', '金管' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '金貨');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 23, '金官', '金管' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '金官');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 24, '家の皆さん', 'ヴィオラの皆さん' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '家の皆さん');
-- 小節・拍
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 25, '小説', '小節' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '小説');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 26, '章説明', '小節' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '章説明');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 27, '泊目', '拍目' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '泊目');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 28, '泊', '拍' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '泊');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 29, '2分オプ', '2拍目' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '2分オプ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 30, '2分オパ', '2拍目' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '2分オパ');
-- 音楽記号・奏法
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 31, 'スラースタックカート', 'スラー・スタッカート' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'スラースタックカート');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 32, 'デガート', 'スタッカート' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'デガート');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 33, 'ポルテ', 'フォルテ' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'ポルテ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 34, 'ホルテ', 'フォルテ' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'ホルテ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 35, 'コルテシ', 'フォルテッシモ' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'コルテシ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 36, 'ゴルテ', 'フォルテ' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'ゴルテ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 37, 'スコーザ', 'スフォルツァンド' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'スコーザ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 38, 'プレシエンド', 'クレッシェンド' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'プレシエンド');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 39, 'プレッシ', 'クレッシェンド' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'プレッシ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 40, 'クレシェ', 'クレッシェンド' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'クレシェ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 41, 'クレシント', 'クレッシェンド' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'クレシント');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 42, 'クレシェード', 'クレッシェンド' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'クレシェード');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 43, 'ディエンド', 'ディミヌエンド' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'ディエンド');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 44, 'リネンド', 'ディミヌエンド' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'リネンド');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 45, 'メザフォルテ', 'メゾフォルテ' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'メザフォルテ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 46, 'ピアニ', 'ピアノ' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'ピアニ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 47, 'ピアニッシュ', 'ピアニッシモ' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'ピアニッシュ');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 48, 'ピアニシ', 'ピアニッシモ' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'ピアニシ');
-- 作曲家名
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 49, 'グループな', 'ブルックナー' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'グループな');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 50, 'ドボルザーク', 'ドヴォルザーク' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'ドボルザーク');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 51, '美容ら引き', 'ヴィオラ弾き' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '美容ら引き');
-- その他
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 52, '渋音符', '16音符' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '渋音符');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 53, '学譜', '楽譜' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '学譜');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 54, '学法', '楽譜' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '学法');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 55, 'マニクス', '手書き譜' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = 'マニクス');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 56, '実筆', '自筆' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '実筆');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 57, '店舗', 'テンポ' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '店舗');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 58, '竹合', '掛け合い' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '竹合');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 59, '対当', '対等' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '対当');
INSERT INTO correction (sort_order, wrong_text, correct_text) SELECT 60, '余因', '余韻' WHERE NOT EXISTS (SELECT 1 FROM correction WHERE wrong_text = '余因');

-- =============================================
-- instrument_keyword テーブル（楽器キーワード定義）
-- canonical_name: 正式名称, keyword: 検索キーワード（バリエーション）
-- =============================================

INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ファースト・ヴァイオリン', 'ファーストバイオリン' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ファースト・ヴァイオリン' AND keyword = 'ファーストバイオリン');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ファースト・ヴァイオリン', 'ファーストバイリン' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ファースト・ヴァイオリン' AND keyword = 'ファーストバイリン');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ファースト・ヴァイオリン', 'ファーストバイ' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ファースト・ヴァイオリン' AND keyword = 'ファーストバイ');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ファースト・ヴァイオリン', 'ファースト' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ファースト・ヴァイオリン' AND keyword = 'ファースト');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'セカンド・ヴァイオリン', 'セカンドバイオリン' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'セカンド・ヴァイオリン' AND keyword = 'セカンドバイオリン');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'セカンド・ヴァイオリン', 'セカンドバイリン' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'セカンド・ヴァイオリン' AND keyword = 'セカンドバイリン');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'セカンド・ヴァイオリン', 'セカンドバイ' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'セカンド・ヴァイオリン' AND keyword = 'セカンドバイ');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'セカンド・ヴァイオリン', 'セカンド' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'セカンド・ヴァイオリン' AND keyword = 'セカンド');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ヴァイオリン', 'バイオリン' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ヴァイオリン' AND keyword = 'バイオリン');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ヴァイオリン', 'ヴァイオリン' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ヴァイオリン' AND keyword = 'ヴァイオリン');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ヴィオラ', 'ヴィオラ' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ヴィオラ' AND keyword = 'ヴィオラ');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ヴィオラ', 'バイベル' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ヴィオラ' AND keyword = 'バイベル');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ヴィオラ', 'ライ、家' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ヴィオラ' AND keyword = 'ライ、家');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ヴィオラ', 'ラの皆さん' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ヴィオラ' AND keyword = 'ラの皆さん');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ヴィオラ', '家の皆さん' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ヴィオラ' AND keyword = '家の皆さん');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'チェロ', 'チェロ' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'チェロ' AND keyword = 'チェロ');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'コントラバス', 'コントラバス' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'コントラバス' AND keyword = 'コントラバス');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'コントラバス', 'ベース' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'コントラバス' AND keyword = 'ベース');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT '弦楽器', '弦楽器' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = '弦楽器' AND keyword = '弦楽器');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT '弦楽器', '現学器' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = '弦楽器' AND keyword = '現学器');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT '弦楽器', '原学器' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = '弦楽器' AND keyword = '原学器');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT '弦楽器', '弦の皆さん' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = '弦楽器' AND keyword = '弦の皆さん');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT '木管', '木管' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = '木管' AND keyword = '木管');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT '木管', '木の皆さん' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = '木管' AND keyword = '木の皆さん');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT '金管', '金管' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = '金管' AND keyword = '金管');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT '金管', '金官' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = '金管' AND keyword = '金官');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT '金管', '金貨' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = '金管' AND keyword = '金貨');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'フルート', 'フルート' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'フルート' AND keyword = 'フルート');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'オーボエ', 'オーボエ' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'オーボエ' AND keyword = 'オーボエ');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'クラリネット', 'クラリネット' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'クラリネット' AND keyword = 'クラリネット');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ファゴット', 'ファゴット' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ファゴット' AND keyword = 'ファゴット');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ファゴット', 'パストリ' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ファゴット' AND keyword = 'パストリ');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ファゴット', 'パスト' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ファゴット' AND keyword = 'パスト');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'ホルン', 'ホルン' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'ホルン' AND keyword = 'ホルン');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'トランペット', 'トランペット' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'トランペット' AND keyword = 'トランペット');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'トロンボーン', 'トロンボーン' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'トロンボーン' AND keyword = 'トロンボーン');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'トロンボーン', 'バストロ' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'トロンボーン' AND keyword = 'バストロ');
INSERT INTO instrument_keyword (canonical_name, keyword) SELECT 'チューバ', 'チューバ' WHERE NOT EXISTS (SELECT 1 FROM instrument_keyword WHERE canonical_name = 'チューバ' AND keyword = 'チューバ');

-- =============================================
-- include_keyword テーブル（意味のある指摘に含まれるべきキーワード）
-- =============================================

INSERT INTO include_keyword (keyword) SELECT 'お願い' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = 'お願い');
INSERT INTO include_keyword (keyword) SELECT 'ください' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = 'ください');
INSERT INTO include_keyword (keyword) SELECT '注意' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '注意');
INSERT INTO include_keyword (keyword) SELECT '気をつけ' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '気をつけ');
INSERT INTO include_keyword (keyword) SELECT '切らない' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '切らない');
INSERT INTO include_keyword (keyword) SELECT '切って' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '切って');
INSERT INTO include_keyword (keyword) SELECT '大きく' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '大きく');
INSERT INTO include_keyword (keyword) SELECT '小さく' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '小さく');
INSERT INTO include_keyword (keyword) SELECT 'もっと' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = 'もっと');
INSERT INTO include_keyword (keyword) SELECT 'しっかり' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = 'しっかり');
INSERT INTO include_keyword (keyword) SELECT 'ちゃんと' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = 'ちゃんと');
INSERT INTO include_keyword (keyword) SELECT 'フォルテ' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = 'フォルテ');
INSERT INTO include_keyword (keyword) SELECT 'ピアノ' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = 'ピアノ');
INSERT INTO include_keyword (keyword) SELECT 'メゾ' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = 'メゾ');
INSERT INTO include_keyword (keyword) SELECT 'フォルツ' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = 'フォルツ');
INSERT INTO include_keyword (keyword) SELECT 'クレッシェンド' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = 'クレッシェンド');
INSERT INTO include_keyword (keyword) SELECT 'ディミヌエンド' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = 'ディミヌエンド');
INSERT INTO include_keyword (keyword) SELECT '歌って' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '歌って');
INSERT INTO include_keyword (keyword) SELECT '返' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '返');
INSERT INTO include_keyword (keyword) SELECT 'アクセント' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = 'アクセント');
INSERT INTO include_keyword (keyword) SELECT '小節' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '小節');
INSERT INTO include_keyword (keyword) SELECT '拍' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '拍');
INSERT INTO include_keyword (keyword) SELECT '聞こえ' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '聞こえ');
INSERT INTO include_keyword (keyword) SELECT 'バランス' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = 'バランス');
INSERT INTO include_keyword (keyword) SELECT '遅れ' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '遅れ');
INSERT INTO include_keyword (keyword) SELECT '早' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '早');
INSERT INTO include_keyword (keyword) SELECT '合わせ' WHERE NOT EXISTS (SELECT 1 FROM include_keyword WHERE keyword = '合わせ');

-- =============================================
-- exclude_pattern テーブル（除外パターン：正規表現）
-- =============================================

INSERT INTO exclude_pattern (pattern) SELECT '^[12あおうえはせの]+$' WHERE NOT EXISTS (SELECT 1 FROM exclude_pattern WHERE pattern = '^[12あおうえはせの]+$');
INSERT INTO exclude_pattern (pattern) SELECT '^[ワンツー]+$' WHERE NOT EXISTS (SELECT 1 FROM exclude_pattern WHERE pattern = '^[ワンツー]+$');
INSERT INTO exclude_pattern (pattern) SELECT 'よろしく' WHERE NOT EXISTS (SELECT 1 FROM exclude_pattern WHERE pattern = 'よろしく');
INSERT INTO exclude_pattern (pattern) SELECT 'おはよう' WHERE NOT EXISTS (SELECT 1 FROM exclude_pattern WHERE pattern = 'おはよう');
INSERT INTO exclude_pattern (pattern) SELECT 'お疲れ' WHERE NOT EXISTS (SELECT 1 FROM exclude_pattern WHERE pattern = 'お疲れ');
INSERT INTO exclude_pattern (pattern) SELECT 'ありがとう' WHERE NOT EXISTS (SELECT 1 FROM exclude_pattern WHERE pattern = 'ありがとう');
INSERT INTO exclude_pattern (pattern) SELECT '休憩' WHERE NOT EXISTS (SELECT 1 FROM exclude_pattern WHERE pattern = '休憩');
INSERT INTO exclude_pattern (pattern) SELECT '終わり' WHERE NOT EXISTS (SELECT 1 FROM exclude_pattern WHERE pattern = '終わり');
INSERT INTO exclude_pattern (pattern) SELECT '^[0-9]+$' WHERE NOT EXISTS (SELECT 1 FROM exclude_pattern WHERE pattern = '^[0-9]+$');
INSERT INTO exclude_pattern (pattern) SELECT '^[あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん]{1,3}$' WHERE NOT EXISTS (SELECT 1 FROM exclude_pattern WHERE pattern = '^[あいうえおかきくけこさしすせそたちつてとなにぬねのはひふへほまみむめもやゆよらりるれろわをん]{1,3}$');

-- =============================================
-- measure_pattern テーブル（小節番号抽出パターン：正規表現）
-- =============================================

INSERT INTO measure_pattern (pattern) SELECT '(\d+)\s*小節' WHERE NOT EXISTS (SELECT 1 FROM measure_pattern WHERE pattern = '(\d+)\s*小節');
INSERT INTO measure_pattern (pattern) SELECT '(\d+)\s*章説明' WHERE NOT EXISTS (SELECT 1 FROM measure_pattern WHERE pattern = '(\d+)\s*章説明');
INSERT INTO measure_pattern (pattern) SELECT '(\d+)\s*から' WHERE NOT EXISTS (SELECT 1 FROM measure_pattern WHERE pattern = '(\d+)\s*から');
INSERT INTO measure_pattern (pattern) SELECT '[A-Z]\s*の\s*(\d+)' WHERE NOT EXISTS (SELECT 1 FROM measure_pattern WHERE pattern = '[A-Z]\s*の\s*(\d+)');

-- =============================================
-- rehearsal_mark_pattern テーブル（練習番号パターン：正規表現）
-- =============================================

INSERT INTO rehearsal_mark_pattern (pattern) SELECT '\b([A-Z])\b(?=の|から|入|まで)' WHERE NOT EXISTS (SELECT 1 FROM rehearsal_mark_pattern WHERE pattern = '\b([A-Z])\b(?=の|から|入|まで)');
