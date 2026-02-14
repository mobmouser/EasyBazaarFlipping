package org.mobmouser.easybazaarflipping.client.bazaar.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.mobmouser.easybazaarflipping.client.bazaar.data.ItemInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetSellPrice {
    public static List<ItemInfo> getSellPrice() {

        String apiUrl = "https://api.hypixel.net/v2/resources/skyblock/items";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();

            List<ItemInfo> itemList = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(response.toString(), JsonObject.class);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                JsonArray items = json.getAsJsonArray("items");

                // 全アイテムを処理
                for (JsonElement i : items) {
                    JsonObject item = i.getAsJsonObject();

                    // npc_sell_priceがない場合や0の場合はスキップ
                    if (!item.has("npc_sell_price")) {
                        continue;
                    }

                    double npcSellPrice = item.get("npc_sell_price").getAsDouble();
                    if (npcSellPrice == 0) {
                        continue;
                    }

                    String id = item.get("id").getAsString();
                    String name = item.get("name").getAsString();

                    //System.out.println(name + " | " + npcSellPrice);

                    itemList.add(new ItemInfo(id, name, npcSellPrice));

                }

                return itemList;

            } else {
                System.out.println("error: http code " + responseCode);
                String cause = json.get("cause").getAsString();
                System.out.println(cause);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
