# 株式会社ゆめみ Android エンジニアコードチェック課題

## 概要

株式会社ゆめみ様 Androidエンジニアコードチェック課題用のリポジトリ


## アプリ仕様

本アプリは GitHub のリポジトリを検索するアプリです。

<img src="docs/app.gif" width="320">

### 環境

- IDE：Android Studio Meerkat Feature Drop | 2024.3.2 Patch 1
- Kotlin：1.9.20
- Java：11
- Gradle：8.11.1
- AGP：8.10.1
- minSdk：23
- targetSdk：31
- compileSdk：31

### 動作

1. 何かしらのキーワードを入力
2. GitHub API（`search/repositories`）でリポジトリを検索し、結果一覧を概要（リポジトリ名）で表示
3. 特定の結果を選択したら、該当リポジトリの詳細（リポジトリ名、オーナーアイコン、プロジェクト言語、Star 数、Watcher 数、Fork 数、Issue 数）を表示

## アピール
<!-- 出来上がり次第，追記・整理していく -->

- issue/PRの活用
    - テンプレートを使用してわかりやすいissue/PRの作成を心がけます
