# EasyBazaarFlipping

Hypixel SkyBlock用のFabric 1.21.5クライアントModです。
Bazaarのリアルタイムデータを分析し、NPC売却利益を最適化します。

A Fabric 1.21.5 client-side mod for Hypixel SkyBlock.
Optimizes Bazaar to NPC sell profit by analyzing real-time Bazaar data.

## このアプリの機能 / What this app does
- 認証済みユーザー自身のSkyBlockプロフィールデータ（所持金）を
  `/v2/player` および `/v2/skyblock/profiles` から取得
- 公開Bazaar価格データを `/v2/skyblock/bazaar` から取得（キー不要）
- 公開アイテムデータを `/v2/resources/skyblock/items` から取得（キー不要）
- すべてのAPI呼び出しはクライアントサイドで、各ユーザーが自分のAPIキーを使用
- APIレスポンスはキャッシュされ、最短5秒間隔で更新
- データは第三者サーバーに保存・送信されません

- Fetches the authenticated user's own SkyBlock profile data
  (coin purse) via `/v2/player` and `/v2/skyblock/profiles`
- Fetches public Bazaar pricing via `/v2/skyblock/bazaar` (no key required)
- Fetches public item data via `/v2/resources/skyblock/items` (no key required)
- All API calls are client-side, each user uses their own API key
- API responses are cached and refreshed every 5 seconds minimum
- No data is stored or sent to any third-party server

## 使用APIエンドポイント / API Endpoints Used
| エンドポイント / Endpoint | 認証 / Auth | 用途 / Purpose |
|--------------------------|-------------|----------------|
| `/v2/skyblock/bazaar` | 不要 / No | Bazaar価格データ / Bazaar pricing data |
| `/v2/resources/skyblock/items` | 不要 / No | NPC売却価格 / Item NPC sell prices |
| `/v2/player` | 必要 / Yes | UUID確認 / Player UUID verification |
| `/v2/skyblock/profiles` | 必要 / Yes | 所持金残高 / Coin purse balance |

## レートリミットについて / Rate Limit Considerations
- 認証が必要なAPI呼び出しはユーザーが[Fetch]ボタンを押した時のみ実行
- Bazaar/アイテムデータのエンドポイントは認証不要
- UUIDは起動時にキャッシュ（1回のみ）、所持金はオンデマンドで取得

- Authenticated API calls are only made when the user
  explicitly clicks the [Fetch] button
- Bazaar/item data endpoints do not require authentication
- UUID is cached at startup (1 call), purse is fetched on demand

## セットアップ / Setup
1. Fabric 1.21.5 + Fabric API が必要
2. Modをmodsフォルダに入れて一度起動
3. `config/bazaar-optimizer.properties` を編集
4. `API_KEY=あなたのキー` を設定（ゲーム内で `/api new` で取得）
5. ゲーム内で `/bzreload` を実行して反映

1. Requires Fabric 1.21.5 + Fabric API
2. Place mod in mods folder and launch once
3. Edit `config/bazaar-optimizer.properties`
4. Set `API_KEY=your_key_here` (obtain via `/api new` in-game)
5. Run `/bzreload` in-game to apply

## 機能 / Features
- **InstaBuyモード / InstaBuy Mode**: InstaBuy→NPC売却の利益を
  往復回数ペナルティ付きスコアでランキング表示
- **BuyOrderモード / BuyOrder Mode**: BuyOrder→NPC売却の利益を
  日次取引量ボーナス付きスコアでランキング表示
- **NPC売却カウンター / NPC Sell Counter**: 1日のNPC売却合計を記録（5億上限）
- **価格操作検出 / Manipulation Detection**: 価格操作が疑われるアイテムを自動除外
- アイテム名クリック → Bazaarページに移動 + 購入可能数をクリップボードにコピー
- Click item name → opens Bazaar page + copies buy quantity to clipboard
