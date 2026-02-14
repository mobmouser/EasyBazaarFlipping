package org.mobmouser.easybazaarflipping.client.bazaar.data;

import org.mobmouser.easybazaarflipping.client.bazaar.api.GetBuyPrice;
import org.mobmouser.easybazaarflipping.client.bazaar.api.GetSellPrice;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemMerger {
    public static List<BazaarOffer> mergeAll() {
        List<ItemInfo> sellItems = GetSellPrice.getSellPrice();
        Map<String, Double> buyOrderPrices = GetBuyPrice.getBuyOrderPrices();
        Map<String, double[]> instaBuyData = GetBuyPrice.getInstaBuyPrices();
        Map<String, Double> volumeData = GetBuyPrice.getSellMovingWeek();

        List<BazaarOffer> result = new ArrayList<>();

        if (sellItems == null) return result;

        for (ItemInfo item : sellItems) {
            String id = item.getId();
            double buyOrder = buyOrderPrices.getOrDefault(id, 0.0);
            double[] instaBuyInfo = instaBuyData.getOrDefault(id, new double[]{0.0, 0});
            double instaBuy = instaBuyInfo[0];
            int amount = (int) instaBuyInfo[1];
            double volume = volumeData.getOrDefault(id, 0.0);

            if (item.getNpcSellPrice() > 0 && instaBuy > 0) {
                result.add(new BazaarOffer(id, item.getName(), item.getNpcSellPrice(),
                        buyOrder, instaBuy, amount, volume));
            }
        }

        return result;
    }
}