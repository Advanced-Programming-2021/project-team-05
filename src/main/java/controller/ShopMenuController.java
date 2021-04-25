package controller;

import model.User;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;

public class ShopMenuController {

    private final User user;


    public ShopMenuController(User user) {
        this.user = user;
    }


    public User getUser() {
        return this.user;
    }


    public final ShopMenuMessage buyCard(String cardName) {
        CardTemplate cardTemplate = DataManager.getInstance().getCardTemplateByName(cardName);
        Card card;

        if (cardTemplate == null) {
            return ShopMenuMessage.NO_CARD_EXISTS;
        } else if (cardTemplate.getPrice() > user.getMoney()) {
            return ShopMenuMessage.NOT_ENOUGH_MONEY;
        } else if (cardTemplate instanceof MonsterTemplate) {
            card = new Monster((MonsterTemplate) cardTemplate);
        } else if (cardTemplate instanceof SpellTemplate) {
            card = new Spell((SpellTemplate) cardTemplate);
        } else {
            card = new Trap((TrapTemplate) cardTemplate);
        }

        DataManager.getInstance().addCard(card);
        user.purchaseCard(card);
        return ShopMenuMessage.CARD_SUCCESSFULLY_PURCHASED;
    }
}
