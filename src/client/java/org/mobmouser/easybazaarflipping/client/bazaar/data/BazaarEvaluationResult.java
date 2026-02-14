package org.mobmouser.easybazaarflipping.client.bazaar.data;

import java.util.HashMap;
import java.util.Map;

public class BazaarEvaluationResult {
    private final BazaarOffer offer;
    private final double totalProfit;
    private final double trips;
    private double score;
    private final Map<String, Double> penalties;

    public BazaarEvaluationResult(BazaarOffer offer, double totalProfit, double trips) {
        this.offer = offer;
        this.totalProfit = totalProfit;
        this.trips = trips;
        this.score = 0;
        this.penalties = new HashMap<>();
    }

    public BazaarOffer getOffer() { return offer; }
    public double getTotalProfit() { return totalProfit; }
    public double getTrips() { return trips; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    // 汎用ペナルティ/ボーナス管理
    public void put(String key, double value) { penalties.put(key, value); }
    public double get(String key) { return penalties.getOrDefault(key, 0.0); }
    public boolean has(String key) { return penalties.containsKey(key); }
}