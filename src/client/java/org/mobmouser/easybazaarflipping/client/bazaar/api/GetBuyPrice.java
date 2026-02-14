package org.mobmouser.easybazaarflipping.client.bazaar.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GetBuyPrice {
    static String apiUrl = "https://api.hypixel.net/v2/skyblock/bazaar";
    private static JsonObject cachedProducts = null; // キャッシュ追加

    // 一度に全商品の価格を取得
    public static Map<String, Double> getBuyOrderPrices() {
        Map<String, Double> priceMap = new HashMap<>();
        JsonObject products = fetchProducts();

        if (products == null) return priceMap;

        for (String productId : products.keySet()) {
            JsonObject product = products.getAsJsonObject(productId);
            JsonArray sellSummary = product.getAsJsonArray("sell_summary");

            if (!sellSummary.isEmpty()) {
                double highestPrice = -1.0;
                for (int i = 0; i < sellSummary.size(); i++) {
                    JsonObject order = sellSummary.get(i).getAsJsonObject();
                    double pricePerUnit = order.get("pricePerUnit").getAsDouble();
                    if (pricePerUnit > highestPrice) {
                        highestPrice = pricePerUnit;
                    }
                }
                highestPrice = Math.round((highestPrice + 0.1) * 10) / 10.0;
                priceMap.put(productId, highestPrice + 0.1);
            }
        }
        return priceMap;
    }

    public static Map<String, double[]> getInstaBuyPrices() {
        Map<String, double[]> priceMap = new HashMap<>();
        JsonObject products = fetchProducts();

        if (products == null) return priceMap;

        for (String productId : products.keySet()) {
            JsonObject product = products.getAsJsonObject(productId);
            JsonArray buySummary = product.getAsJsonArray("buy_summary");

            if (!buySummary.isEmpty()) {
                JsonObject firstOrder = buySummary.get(0).getAsJsonObject();
                double price = firstOrder.get("pricePerUnit").getAsDouble();
                int amount = firstOrder.get("amount").getAsInt();
                priceMap.put(productId, new double[]{price, amount});
            }
        }
        return priceMap;
    }


    // 共通: API取得部分を切り出し（重複削減）
    private static JsonObject fetchProducts() {
        // キャッシュがあれば返す（オプション）
        if (cachedProducts != null) {
            return cachedProducts;
        }

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Gson gson = new Gson();
                JsonObject json = gson.fromJson(response.toString(), JsonObject.class);
                cachedProducts = json.getAsJsonObject("products");
                return cachedProducts;
            } else {
                System.out.println("error: http code " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Map<String, Double> getSellMovingWeek() {
        Map<String, Double> volumeMap = new HashMap<>();
        JsonObject products = fetchProducts();

        if (products == null) return volumeMap;

        for (String productId : products.keySet()) {
            JsonObject product = products.getAsJsonObject(productId);
            JsonObject quickStatus = product.getAsJsonObject("quick_status");

            double sellMovingWeek = quickStatus.get("sellMovingWeek").getAsDouble();
            volumeMap.put(productId, sellMovingWeek);
        }

        return volumeMap;
    }
}
