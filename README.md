# ama-orch

オーケストラ活動のデータ管理と練習記録を扱うアプリケーション群です。

## アプリケーション

- [ama-orch-web](ama-orch-web/README.md): Spring Boot + Thymeleafで動作するWebアプリ
- [ama-orch-data-editor](ama-orch-data-editor/README.md): Windows/Android対応を前提にしたGUIアプリの土台

## ドキュメント

- [ama-orch-webの設計書](docs/ama-orch-web/)
- [ama-orch-data-editorの今後のJSON設計メモ](docs/ama-orch-data-editor/json-format.md)

## VS Codeでの起動

`.vscode/launch.json`から次の構成を選択できます。

- `LessonApplication (H2 開発)`
- `LessonApplication (PostgreSQL テスト)`
- `ama-orch-data-editor (ブラウザ)`
