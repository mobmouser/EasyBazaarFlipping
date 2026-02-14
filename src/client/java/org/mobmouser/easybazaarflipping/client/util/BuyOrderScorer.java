package org.mobmouser.easybazaarflipping.client.util;

import org.mobmouser.easybazaarflipping.client.bazaar.data.BazaarEvaluationResult;
import org.mobmouser.easybazaarflipping.client.bazaar.data.BazaarOffer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BuyOrderScorer {

    private static final double NPC_LIMIT = 500_000_000;
    private static final int INVENTORY_SIZE = 2240;

    private static int tripThreshold = 6;
    private static int tripHardLimit = 10;
    private static double tripLambda = 0.5;
    private static double volumeLambda = 0.3;

    public static void setTripThreshold(int value) { tripThreshold = value; }
    public static void setTripHardLimit(int value) { tripHardLimit = value; }
    public static void setTripLambda(double value) { tripLambda = value; }
    public static void setVolumeLambda(double value) { volumeLambda = value; }
    public static int getTripThreshold() { return tripThreshold; }
    public static int getTripHardLimit() { return tripHardLimit; }
    public static double getTripLambda() { return tripLambda; }
    public static double getVolumeLambda() { return volumeLambda; }

    public static List<BazaarEvaluationResult> evaluate(List<BazaarOffer> offers) {
        List<BazaarEvaluationResult> results = new ArrayList<>();

        for (BazaarOffer offer : offers) {
            double unitProfit = offer.getNpcSellPrice() - offer.getBuyOrder();
            if (unitProfit <= 0 || offer.getBuyOrder() <= 0) continue;

            // Buy Limit: 71680個買えないアイテムは除外
            if (!BuyLimitData.isFullBuyLimit(offer.getId())) continue;

            // マニピュレーション検出
            if (BazaarScorer.ManipulationDetector.isLikelyManipulated(
                    offer.getBuyOrder(), offer.getInstaBuy())) continue;

            double maxSellCount = NPC_LIMIT / offer.getNpcSellPrice();
            double totalProfit = unitProfit * maxSellCount;
            double trips = maxSellCount / INVENTORY_SIZE;

            if (trips > tripHardLimit) continue;

            double tripPenalty = trips > tripThreshold
                    ? Math.pow(trips - tripThreshold, 2) : 0;

            BazaarEvaluationResult result = new BazaarEvaluationResult(offer, totalProfit, trips);
            result.put("tripPenalty", tripPenalty);
            result.put("volumeBonus", offer.getDailyVolume());
            results.add(result);
        }

        if (results.isEmpty()) return results;

        double aMax = results.stream().mapToDouble(BazaarEvaluationResult::getTotalProfit).max().orElse(0);
        double aMin = results.stream().mapToDouble(BazaarEvaluationResult::getTotalProfit).min().orElse(0);
        double tripMax = results.stream().mapToDouble(r -> r.get("tripPenalty")).max().orElse(0);
        double volMax = results.stream().mapToDouble(r -> r.get("volumeBonus")).max().orElse(0);

        for (BazaarEvaluationResult r : results) {
            double aNorm = (aMax == aMin) ? 1.0 : (r.getTotalProfit() - aMin) / (aMax - aMin);
            double tNorm = (tripMax == 0) ? 0 : r.get("tripPenalty") / tripMax;
            double vNorm = (volMax == 0) ? 0 : r.get("volumeBonus") / volMax;

            r.setScore(aNorm - tripLambda * tNorm + volumeLambda * vNorm);
        }

        results.sort(Comparator.comparingDouble(BazaarEvaluationResult::getScore).reversed());
        return results;
    }
}