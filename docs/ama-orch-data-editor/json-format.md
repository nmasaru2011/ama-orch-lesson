# ama-orch-data-editor JSON形式

データエディタは、ルートがオブジェクトの配列であるJSONを基本形式として扱います。

```json
[
  {
    "orch_id": "AMA001",
    "orch_name": "サンプル管弦楽団",
    "activity": true
  }
]
```

次のように、配列をプロパティに持つオブジェクトも読み込めます。最初に見つかった配列が編集対象になります。

```json
{
  "dataType": "orch",
  "items": [
    {
      "orch_id": "AMA001",
      "orch_name": "サンプル管弦楽団"
    }
  ]
}
```

現段階では、JSON Schemaによる項目チェックやSpring Boot側への登録処理はこのエディタに含めていません。Webアプリ側に受信APIを追加した際は、エディタ下部の送信先URLへそのURLを入力して使用します。
