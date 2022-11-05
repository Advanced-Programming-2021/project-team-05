package model;

import model.template.CardTemplate;

public class ShopItem {

    private final CardTemplate card;
    private final int purchasedCount;


    public ShopItem(CardTemplate card, int purchasedCount) {
        this.card = card;
        this.purchasedCount = purchasedCount;
    }


    public CardTemplate getCardTemplate() {
        return this.card;
    }

    public int getPurchasedCount() {
        return this.purchasedCount;
    }
}
