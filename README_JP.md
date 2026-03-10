# Shang Blog - 上ブログ

<p align="center">
  <b>モダンなフルスタックブログプラットフォーム</b>
</p>

<p align="center">
  <a href="README.md">中文</a> |
  <a href="README_EN.md">English</a> |
  <a href="README_JP.md">日本語</a>
</p>

---

## 📖 プロジェクト紹介

**Shang Blog** は、豊富な機能を持つフルスタックブログプラットフォームです。フロントエンドとバックエンドを分離したアーキテクチャを採用し、複数の端末からアクセスできます。プロジェクトにはユーザーポータル、管理画面、モバイルアプリ/ミニプログラムが含まれています。

## 🚀 技術スタック

### バックエンド (blog/)
| 技術 | バージョン | 説明 |
|------|------------|------|
| Spring Boot | 2.7.0 | コアフレームワーク |
| Java | 8 | プログラミング言語 |
| MyBatis-Plus | - | ORM フレームワーク |
| Sa-Token | - | 認証認可 |
| MySQL | 8.0 | データベース |
| Redis | 7 | キャッシュ |
| RabbitMQ | - | メッセージキュー |
| Quartz | - | ジョブスケジューラー |
| Dify API | - | AI 統合 |

### フロントエンド
| プロジェクト | 技術スタック | 説明 |
|--------------|--------------|------|
| blog-web | Vue 2.7 + Vite | ユーザーポータル |
| blog-admin | Vue 3.2 + Element Plus | 管理画面 |
| uniapp-blog | UniApp | モバイルアプリ |

## 📁 プロジェクト構成

```
shang/
├── blog/                   # バックエンドプロジェクト
│   ├── shang-server/      # エントリーモジュール
│   ├── shang-api/         # ポータル API
│   ├── shang-admin/       # 管理 API
│   ├── shang-auth/        # 認証モジュール
│   ├── shang-common/      # 共通モジュール
│   ├── shang-file/        # ファイルストレージ
│   ├── shang-quartz/      # スケジュールジョブ
│   └── Shang-AiMode/      # AI モジュール
├── blog-web/              # Web フロントエンド
├── blog-admin/            # 管理画面
├── uniapp-blog/           # モバイルアプリ
├── docker/                # Docker 設定
└── shang-blog.sql         # データベーススキーマ
```

## 🔧 クイックスタート

### ローカル開発

1. **データベースを起動**
```bash
docker-compose up -d mysql redis
```

2. **バックエンドを実行**
```bash
cd blog
mvn clean install -DskipTests
cd shang-server
mvn spring-boot:run
```

3. **フロントエンドを実行**
```bash
# ユーザーポータル
cd blog-web
npm install
npm run dev

# 管理画面
cd blog-admin
npm install
npm run dev
```

### Docker デプロイ

```bash
cd docker
./deploy.sh  # Linux/Mac
deploy.bat     # Windows
```

アクセス URL：
- ブログポータル：http://localhost:9001
- 管理画面：http://localhost:9003
- API ドキュメント：http://localhost:8800/shiyi/doc.html

## ✨ 主要機能

- 📝 Markdown エディタ対応
- 🎨 カスタムテーマ
- 🔍 全文検索
- 💬 コメントシステム
- 📊 統計ダッシュボード
- 🤖 AI スマートアシスタント
- 🔐 多プラットフォーム OAuth (QQ/微博/Gitee/GitHub)
- 📱 レスポンシブデザイン
- 🌙 ダークモード

## 🐳 Docker サービス

| サービス | コンテナポート | ホストポート | 説明 |
|----------|---------------|-------------|------|
| MySQL | 3306 | 3307 | データベース |
| Redis | 6379 | 6380 | キャッシュ |
| バックエンド | 8800 | 8800 | Spring Boot API |
| フロントエンドポータル | 80 | 9001 | ユーザーポータル |
| フロントエンド管理 | 80 | 9003 | 管理画面 |

## 📄 ライセンス

[MIT](LICENSE)

## 👤 作者

Shang

---

<p align="center">
  <b>Made with ❤️ by Shang</b>
</p>
