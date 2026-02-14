package org.mobmouser.easybazaarflipping.client.bazaar.data;

public class ItemInfo {
    private String id;
    private String name;
    private double npcSellPrice;

    public ItemInfo() {
    }

    public ItemInfo(String id, String name, double npcSellPrice) {
        this.id = id;
        this.name = name;
        this.npcSellPrice = npcSellPrice;
    }

    //----set----
    public void setId(String id) { this.id = id; }
    public void setName(String name) {
        this.name = name;
    }
    public void setNpcSellPrice(double npcSellPrice) { this.npcSellPrice = npcSellPrice; }

    //----get----
    public String getId() {
        return id;
    }
    public String getName() { return name; }
    public double getNpcSellPrice() {
        return npcSellPrice;
    }
}
