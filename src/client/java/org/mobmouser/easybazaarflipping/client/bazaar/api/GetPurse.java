package org.mobmouser.easybazaarflipping.client.bazaar.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import org.mobmouser.easybazaarflipping.client.util.ConfigLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

public class GetPurse {

    private static String cachedUuid = null;
    private static long cachedPurse = -1;
    private static boolean fetchingPurse = false;
    // GetPurse.java に追加
    private static boolean purseUpdated = false;
    private static String status = "";

    public static boolean isPurseUpdated() { return purseUpdated; }
    public static void consumePurseUpdate() { purseUpdated = false; }
    public static String getStatus() { return status; }





    public static long getCachedPurse() {
        return cachedPurse;
    }

    // MC起動時に呼ぶ（非同期）
    public static void initUuid() {
        new Thread(() -> {
            try {
                MinecraftClient client = MinecraftClient.getInstance();
                while (client.player == null) {
                    Thread.sleep(1000);
                }
                String uuid = client.player.getUuidAsString().replace("-", "");
                String apiKey = ConfigLoader.get("API_KEY");

                JsonObject playerJson = fetchJson(
                        "https://api.hypixel.net/v2/player?uuid=" + uuid, apiKey);
                if (playerJson != null) {
                    cachedUuid = playerJson.getAsJsonObject("player").get("uuid").getAsString();
                    System.out.println("[GetPurse] UUID cached: " + cachedUuid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 非同期でPurse取得してキャッシュに保存
    // GetPurse.java の fetchPurseAsync 内
    public static void fetchPurseAsync() {
        if (fetchingPurse || cachedUuid == null) {
            if (cachedUuid == null) status = "UUID not ready";
            return;
        }
        fetchingPurse = true;
        status = "Fetching...";

        new Thread(() -> {
            try {
                String apiKey = ConfigLoader.get("API_KEY");
                JsonObject profilesJson = fetchJson(
                        "https://api.hypixel.net/v2/skyblock/profiles?uuid=" + cachedUuid, apiKey);

                if (profilesJson == null) {
                    status = "API error";
                    return;
                }

                JsonArray profiles = profilesJson.getAsJsonArray("profiles");

                for (int i = 0; i < profiles.size(); i++) {
                    JsonObject profile = profiles.get(i).getAsJsonObject();
                    if (profile.has("selected") && profile.get("selected").getAsBoolean()) {
                        cachedPurse = extractCoinPurse(profile);
                        purseUpdated = true;
                        status = "Done!";
                        return;
                    }
                }

                cachedPurse = extractCoinPurse(profiles.get(0).getAsJsonObject());
                purseUpdated = true;
                status = "Done!";
            } catch (Exception e) {
                e.printStackTrace();
                status = "Error";
            } finally {
                fetchingPurse = false;
            }
        }).start();
    }

    private static long extractCoinPurse(JsonObject profile) {
        JsonObject member = profile.getAsJsonObject("members").getAsJsonObject(cachedUuid);
        double coinPurse = member.getAsJsonObject("currencies").get("coin_purse").getAsDouble();
        return (long) coinPurse;
    }

    private static JsonObject fetchJson(String urlString, String apiKey) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("API-Key", apiKey);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("[GetPurse] HTTP error: " + conn.getResponseCode());
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return new Gson().fromJson(response.toString(), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}