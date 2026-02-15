# EasyBazaarFlipping

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
