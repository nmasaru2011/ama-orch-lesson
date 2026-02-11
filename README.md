# Rehearsal Lesson Summary Service

オーケストラ練習の字幕から指摘内容を抽出・分析するツール

## 概要

このアプリケーションは、YouTube上の練習動画のSRT字幕ファイルを解析して、楽器別・小節別の指摘内容を自動抽出し、Excel、PDF、CSV、JSON形式でエクスポートするサービスです。

## 技術スタック

- **Java**: 21
- **Spring Boot**: 3.1.12
- **Web Framework**: Spring MVC + Thymeleaf
- **ビルドツール**: Maven
- **その他**:
  - Apache POI (Excel出力)
  - OpenPDF (PDF出力)

## 主な機能

### 解析機能
- **SRT字幕解析**: YouTube の SRT形式字幕ファイルを解析
- **音楽用語修正**: OCRの誤字を自動修正
- **指摘内容抽出**: 意味のある指摘のみを抽出
- **楽器別抽出**: 対象楽器を自動判定
- **小節番号抽出**: 小節番号やリハーサルマークを抽出
- **YouTube リンク**: タイムスタンプ付きYouTubeリンクを生成

### エクスポート機能
- **Excel形式**: スタイル付きの見やすい出力
- **PDF形式**: 印刷対応のレイアウト
- **CSV形式**: 汎用スプレッドシート形式
- **JSON形式**: プログラム連携用

## 前提条件

- Java 21以上
- Maven 3.6以上

## セットアップ

### 1. プロジェクトの取得

```bash
cd /path/to/project
```

### 2. ビルド

```bash
mvn clean install
```

### 3. 実行

```bash
mvn spring-boot:run
```

アプリケーションは `http://localhost:8091` で起動します。

## 使用方法

1. ブラウザで `http://localhost:8091/rehearsal` にアクセス
2. SRTファイル（字幕ファイル）を選択
3. YouTube URL（オプション）を入力するとタイムスタンプ付きリンクが生成
4. 「分析」ボタンをクリック
5. 結果が表示され、Excel/PDF/CSV/JSONでダウンロード可能

## プロジェクト構成

```
src/main/java/com/amaorcnsuaru/lesson/
├── LessonApplication.java                    # メインクラス
├── controller/
│   └── RehearsalController.java              # リハーサルコントローラ
├── service/
│   └── RehearsalSrtService.java              # SRT解析・エクスポート処理
└── resource/
    └── RehearsalInstruction.java             # 指摘データ構造

src/main/resources/
├── application.yml                           # アプリケーション設定
├── templates/
│   └── rehearsal/
│       └── form.html                         # SRT解析フォーム
└── static/                                   # 静的リソース
```

## API エンドポイント

- `GET /rehearsal` - SRT解析フォーム
- `POST /rehearsal/analyze` - SRTファイル解析
- `GET /rehearsal/download/json` - JSON出力
- `GET /rehearsal/download/csv` - CSV出力
- `GET /rehearsal/download/excel` - Excel出力
- `GET /rehearsal/download/pdf` - PDF出力

## 設定ファイル

### application.yml

```yaml
server:
  port: 8091
spring:
  application:
    name: lesson
```

## ライセンス

このプロジェクトはプロトタイプ・実験的なプロジェクトです。
