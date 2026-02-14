package org.mobmouser.easybazaarflipping.client.bazaar.data;

public class BazaarOffer extends ItemInfo {
    private final double buyOrder;
    private final double instaBuy;
    private final int amount;
    private final double profit;
    private final double sellMovingWeek;

    public BazaarOffer(String id, String name, double npcSellPrice,
                       double buyOrder, double instaBuy, int amount, double sellMovingWeek) {
        super(id, name, npcSellPrice);
        this.buyOrder = buyOrder;
        this.instaBuy = instaBuy;
        this.amount = amount;
        this.profit = npcSellPrice - instaBuy;
        this.sellMovingWeek = sellMovingWeek;
    }

    public double getBuyOrder() { return buyOrder; }
    public double getInstaBuy() { return instaBuy; }
    public int getAmount() { return amount; }
    public double getProfit() { return profit; }
    public double getSellMovingWeek() { return sellMovingWeek; }

    // 1日あたりの取引量
    public double getDailyVolume() { return sellMovingWeek / 7.0; }
}
