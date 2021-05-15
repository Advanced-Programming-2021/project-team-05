package control.controller;

import control.DataManager;
import control.message.ShopMenuMessage;
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
        DataManager dataManager = DataManager.getInstance();
        CardTemplate cardTemplate = dataManager.getCardTemplateByName(cardName);

        if (cardTemplate == null) {
            return ShopMenuMessage.NO_CARD_EXISTS;
        }
        if (cardTemplate.getPrice() > user.getMoney()) {
            return ShopMenuMessage.NOT_ENOUGH_MONEY;
        }

        Card card;
        if (cardTemplate instanceof MonsterTemplate) {
            card = new Monster((MonsterTemplate) cardTemplate);
        } else if (cardTemplate instanceof SpellTemplate) {
            card = new Spell((SpellTemplate) cardTemplate);
        } else {
            card = new Trap((TrapTemplate) cardTemplate);
        }

        dataManager.addCard(card);
        user.purchaseCard(card);
        user.decreaseMoney(cardTemplate.getPrice());
        return ShopMenuMessage.CARD_SUCCESSFULLY_PURCHASED;
    }
}
