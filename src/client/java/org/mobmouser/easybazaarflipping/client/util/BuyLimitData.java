package org.mobmouser.easybazaarflipping.client.util;

import java.util.HashMap;
import java.util.Map;

public class BuyLimitData {

    private static final int DEFAULT_LIMIT = 71680;
    private static final Map<String, Integer> CUSTOM_LIMITS = new HashMap<>();

    static {
        int L = 256;
        String[] limited = {
                "BOOSTER_COOKIE", "HOT_POTATO_BOOK", "FUMING_POTATO_BOOK",
                "COMPACTOR", "DWARVEN_COMPACTOR", "SUPER_COMPACTOR_3000",
                "SUMMONING_EYE", "MAGMA_BUCKET", "PLASMA_BUCKET",
                "RECOMBOBULATOR_3000", "HARDENED_WOOD", "ROCK_GEMSTONE",
                "RARE_DIAMOND", "JERRY_STONE", "RED_NOSE", "SHINY_PRISM",
                "SPIRIT_DECOY", "CANDY_CORN", "SEARINHG_STONE", "LAPIS_CRYSTAL",
                "RED_SCARF", "OPTICAL_LENS", "AOTE_STONE", "JADERALD",
                "DRAGON_CLAW", "BLAZE_WAX", "NECROMANCER_BROOCH", "SALMON_OPAL",
                "PREMIUM_FLESH", "SUSPICIOUS_VIAL", "METEOR_SHARD", "MIDAS_JEWEL",
                "DIAMOND_ATOM", "ONYX", "ENDSTONE_GEODE", "MOLTEN_CUBE",
                "DIAMONITE", "DRAGON_SCALE", "PURE_MITHRIL", "AMBER_MATERIAL",
                "PRECURSOR_GEAR", "WITHER_BLOOD", "DEEP_SEA_ORB", "BULKY_STONE",
                "KUUDRA_MANDIBLE", "SADAN_BROOCH", "BLESSED_FRUIT",
                "PETRIFIED_STARFALL", "GIANT_TOOTH", "DRAGON_HORN",
                "REFINED_AMBER", "LUCKY_DICE", "GOLDEN_BALL", "HOT_STUFF",
                "LUXURIOUS_SPOOL", "ROCK_CANDY", "END_STONE_SHULKER",
                "OBSIDIAN_TABLET", "DARK_ORB", "FURBALL", "ENDER_MONOCLE",
                "ACACIA_BIRDHOUSE", "BEATING_HEART", "MANDRAA", "MAGMA_URCHIN",
                "HORNS_OF_TORMENT", "PRECIOUS_PEARL", "ECCENTRIC_PAINTING",
                "HAZMAT_ENDERMAN", "VITAMIN_DEATH", "SCORCHED_BOOKS",
                "JUNGLE_HEART", "ENDERMAN_CORTEX_REWRITER",
                "FUEL_TANK", "TIGHTLY_TIED_HAY_BALE"
        };
        for (String id : limited) {
            CUSTOM_LIMITS.put(id, L);
        }
    }

    public static int getLimit(String itemId) {
        return CUSTOM_LIMITS.getOrDefault(itemId, DEFAULT_LIMIT);
    }

    public static boolean isFullBuyLimit(String itemId) {
        return getLimit(itemId) == DEFAULT_LIMIT;
    }
}