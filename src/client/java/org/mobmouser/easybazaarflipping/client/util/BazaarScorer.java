package org.mobmouser.easybazaarflipping.client.util;

import org.mobmouser.easybazaarflipping.client.bazaar.data.BazaarEvaluationResult;
import org.mobmouser.easybazaarflipping.client.bazaar.data.BazaarOffer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BazaarScorer {

    private static final double NPC_LIMIT = 500_000_000;
    private static final int INVENTORY_SIZE = 2240;

    private static int tripThreshold = 6;
    private static int tripHardLimit = 10;
    private static double tripLambda = 0.5;

    public static void setTripThreshold(int value) { tripThreshold = value; }
    public static void setTripHardLimit(int value) { tripHardLimit = value; }
    public static void setTripLambda(double value) { tripLambda = value; }
    public static int getTripThreshold() { return tripThreshold; }
    public static int getTripHardLimit() { return tripHardLimit; }
    public static double getTripLambda() { return tripLambda; }

    public static List<BazaarEvaluationResult> evaluate(List<BazaarOffer> offers) {
        List<BazaarEvaluationResult> results = new ArrayList<>();

        for (BazaarOffer offer : offers) {
            if (offer.getProfit() <= 0 || offer.getInstaBuy() <= 0) continue;

            // Buy Limit: 71680個買えないアイテムは除外
            if (!BuyLimitData.isFullBuyLimit(offer.getId())) continue;

            // マニピュレーション検出: 不自然な価格差は除外
            if (ManipulationDetector.isLikelyManipulated(
                    offer.getBuyOrder(), offer.getInstaBuy())) continue;

            double maxSellCount = NPC_LIMIT / offer.getNpcSellPrice();
            double totalProfit = offer.getProfit() * maxSellCount;
            double trips = maxSellCount / INVENTORY_SIZE;

            if (trips > tripHardLimit) continue;

            double tripPenalty = trips > tripThreshold
                    ? Math.pow(trips - tripThreshold, 2) : 0;

            BazaarEvaluationResult result = new BazaarEvaluationResult(offer, totalProfit, trips);
            result.put("tripPenalty", tripPenalty);
            results.add(result);
        }

        if (results.isEmpty()) return results;

        double aMax = results.stream().mapToDouble(BazaarEvaluationResult::getTotalProfit).max().orElse(0);
        double aMin = results.stream().mapToDouble(BazaarEvaluationResult::getTotalProfit).min().orElse(0);
        double tripMax = results.stream().mapToDouble(r -> r.get("tripPenalty")).max().orElse(0);

        for (BazaarEvaluationResult r : results) {
            double aNorm = (aMax == aMin) ? 1.0 : (r.getTotalProfit() - aMin) / (aMax - aMin);
            double tNorm = (tripMax == 0) ? 0 : r.get("tripPenalty") / tripMax;

            r.setScore(aNorm - tripLambda * tNorm);
        }

        results.sort(Comparator.comparingDouble(BazaarEvaluationResult::getScore).reversed());
        return results;
    }

    public static class ManipulationDetector {

        public static boolean isLikelyManipulated(double buyOrderPrice, double instaBuyPrice) {
            if (buyOrderPrice <= 0 || instaBuyPrice <= 0) return false;
            return instaBuyPrice > buyOrderPrice + buyOrderPrice * ((100.0 / (buyOrderPrice + 12)) + 0.2);
        }
    }
}