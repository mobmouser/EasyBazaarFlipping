package org.mobmouser.easybazaarflipping.client.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class NpcSellCounter {
    private static long dailyTotal = 0;
    private static String lastDate = "";
    private static final long NPC_LIMIT = 500_000_000;
    private static final Path SAVE_PATH = Path.of("config", "bazaar-npc-sell.properties");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 起動時に呼ぶ
    public static void init() {
        load();
        checkDateReset();
    }

    public static void register(String text) {
        String[] words = text.split("\\s+");
        if (words.length < 3) return;

        String mode = words[1];
        try {
            int amount = Integer.parseInt(words[words.length - 2].replace(",", ""));

            if (mode.equals("sold")) {
                dailyTotal += amount;
                save();
            } else if (mode.equals("bought")) {
                dailyTotal -= amount;
                if (dailyTotal < 0) dailyTotal = 0;
                save();
            }
        } catch (NumberFormatException e) {
            // パースできないメッセージは無視
        }
    }

    // GMTの日付を取得（SkyBlockのリセット基準）
    private static String getTodayGMT() {
        return LocalDate.now(ZoneOffset.UTC).format(DATE_FORMAT);
    }

    // 日付が変わっていたらリセット
    public static void checkDateReset() {
        String today = getTodayGMT();
        if (!today.equals(lastDate)) {
            dailyTotal = 0;
            lastDate = today;
            save();
            System.out.println("[NpcSellCounter] Date changed to " + today + " - counter reset");
        }
    }

    private static void save() {
        try {
            Files.createDirectories(SAVE_PATH.getParent());
            Properties props = new Properties();
            props.setProperty("dailyTotal", String.valueOf(dailyTotal));
            props.setProperty("lastDate", lastDate);
            props.store(Files.newBufferedWriter(SAVE_PATH), "NPC Sell Counter Data");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void load() {
        try {
            if (Files.exists(SAVE_PATH)) {
                Properties props = new Properties();
                props.load(Files.newBufferedReader(SAVE_PATH));
                dailyTotal = Long.parseLong(props.getProperty("dailyTotal", "0"));
                lastDate = props.getProperty("lastDate", "");
                System.out.println("[NpcSellCounter] Loaded: total=" + dailyTotal + " date=" + lastDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            dailyTotal = 0;
            lastDate = "";
        }
    }

    public static long getDailyTotal() { return dailyTotal; }
    public static long getRemaining() { return NPC_LIMIT - dailyTotal; }
    public static double getUsagePercent() { return (dailyTotal * 100.0) / NPC_LIMIT; }

    public static void reset() {
        dailyTotal = 0;
        lastDate = getTodayGMT();
        save();
    }
}