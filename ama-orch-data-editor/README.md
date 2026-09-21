# ama-orch-data-editor

ama-orchのJSONデータをブラウザで確認・編集するシンプルなWebアプリです。
JSONファイルを開いて表形式で編集し、編集後のJSONを保存できます。送信先URLを指定すれば、HTTP POSTでWebアプリへ送信することもできます。

## 起動方法

### VS Codeから起動

ルートの`.vscode/launch.json`で、`ama-orch-data-editor (ブラウザ)`を選んで実行します。

### Windowsで起動

このフォルダの`index.html`をChromeまたはEdgeで開きます。ファイル選択とJSON保存だけなら、Webサーバーは不要です。

### ローカルWebサーバーで起動

AndroidタブレットからWindows上のアプリへ接続する場合は、Webサーバーで配信します。

```powershell
cd ama-orch-data-editor
python -m http.server 5173 --bind 0.0.0.0
```

WindowsのIPアドレスを確認し、同じWi-Fi上の端末から次のURLを開きます。

```text
http://<WindowsのIPアドレス>:5173/
```

## 使い方

1. 「JSONを開く」または「サンプルを表示」を選ぶ
2. 表のセルを編集する
3. 「JSONを保存」で編集済みファイルをダウンロードする
4. API送信を使う場合は送信先URLを入力して「送信」を選ぶ

JSONの想定形式は[JSON形式の説明](../docs/ama-orch-data-editor/json-format.md)を参照してください。
