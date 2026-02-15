# EasyBazaarFlipping

Fabric 1.21.5 client-side mod for Hypixel SkyBlock.
Optimizes Bazaar to NPC sell profit by analyzing real-time Bazaar data.

## What this app does
- Fetches the authenticated user's own SkyBlock profile data 
  (coin purse) via `/v2/player` and `/v2/skyblock/profiles`
- Fetches public Bazaar pricing via `/v2/skyblock/bazaar` (no key required)
- Fetches public item data via `/v2/resources/skyblock/items` (no key required)
- All API calls are client-side, each user uses their own API key
- API responses are cached and refreshed every 5 seconds minimum
- No data is stored or sent to any third-party server

## API Endpoints Used
| Endpoint | Auth Required | Purpose |
|----------|--------------|---------|
| `/v2/skyblock/bazaar` | No | Bazaar pricing data |
| `/v2/resources/skyblock/items` | No | Item NPC sell prices |
| `/v2/player` | Yes | Player UUID verification |
| `/v2/skyblock/profiles` | Yes | Coin purse balance |

## Rate Limit Considerations
- Authenticated API calls are only made when the user 
  explicitly clicks the [Fetch] button
- Bazaar/item data endpoints do not require authentication
- UUID is cached at startup (1 call), purse is fetched on demand

## Setup
1. Requires Fabric 1.21.5 + Fabric API
2. Place mod in mods folder and launch once
3. Edit `config/bazaar-optimizer.properties`
4. Set `API_KEY=your_key_here` (obtain via `/api new` in-game)
5. Run `/bzreload` in-game to apply

## Features
- **InstaBuy Mode**: Ranks items by InstaBuy→NPC Sell profit with 
  trip count penalty scoring
- **BuyOrder Mode**: Ranks items by BuyOrder→NPC Sell profit with 
  daily volume bonus scoring  
- **NPC Sell Counter**: Tracks daily NPC sell total (500M limit)
- **Manipulation Detection**: Auto-excludes price-manipulated items
- Click item name → opens Bazaar page + copies buy quantity to clipboard

---日本語---

## Setup
1. Fabric 1.21.5 + Fabric API が必要
2. Modをmodsフォルダに入れて起動
3. `config/bazaar-optimizer.properties` が自動生成される
4. Hypixel APIキーを取得（ゲーム内で `/api new`）
5. propertiesファイルの `API_KEY=your_api_key_here` を自分のキーに書き換え
6. ゲーム内で `/bzreload` を実行

## 機能
- **InstaBuyモード**: InstaBuy→NPC Sellで利益が出るアイテムを表示
- **BuyOrderモード**: BuyOrder→NPC Sellで利益が出るアイテムを表示
- **NPC Sell Counter**: その日のNPC売却額を記録・表示
- アイテム名クリックでBazaarページに飛ぶ+購入可能数をクリップボードにコピー
