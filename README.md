# 株式会社ゆめみ Android エンジニアコードチェック課題

## 概要

株式会社ゆめみ様 Androidエンジニアコードチェック課題の提出用リポジトリ


## アプリ仕様

本アプリは GitHub のリポジトリを検索するアプリです。

<img src="docs/HowToUse.gif" width="320" alt="アプリの使用方法デモ">

### 環境

- IDE：Android Studio Meerkat Feature Drop | 2024.3.2 Patch 1
- Kotlin：1.9.21
- Java：11
- Gradle：8.11.1
- AGP：8.10.1
- minSdk：23
- targetSdk：36
- compileSdk：36

### 動作

1. 何かしらのキーワードを入力
2. GitHub API（`search/repositories`）でリポジトリを検索し、結果一覧を概要（リポジトリ名）で表示
3. 特定の結果を選択したら、該当リポジトリの詳細（リポジトリ名、オーナーアイコン、プロジェクト言語、Star 数、Watcher 数、Fork 数、Issue 数）を表示

## アピール
<!-- 出来上がり次第，追記・整理していく -->

- issue/PRの活用
    - テンプレートを使用してわかりやすいissue/PRの作成を心がけます
    - Geminiを使用してレビューを行い、修正漏れの発生や潜在的なリスクの発見に役立てます

- Jetpack Composeを導入
    - 宣言的なUIで画面を構築することで、コードの可読性と保守性を向上させました

- MVVMアーキテクチャを導入
    - Dagger Hiltを使用してviewModelやGitHub API処理などを切り離して保守性を向上させました

- Material3に沿ったデザインを使用
    - ダークモード、横画面にも対応させて見やすいデザインに配慮しました
    - [画像はこちら](docs/image)

- GitHub APIのレート制限のエラー出力
    - 比較的すぐにレート制限に引っかかったので、待機時間を取得してsnackbarに表示させました

## 備考
- テストを実施しましたが、期限に間に合わずユニットテストのみとなりました(`OneViewModel`, `GitHubRepositoryImpl`)
    - UIテスト等は未達となっています
    - [ユニットテスト カバレッジはこちら](docs/coverage)