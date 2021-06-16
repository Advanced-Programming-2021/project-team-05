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
import view.ShopMenuView;


public class ShopMenuController {

    private ShopMenuView view;
    private final User user;


    public ShopMenuController(User user) {
        this.user = user;
    }


    public void setView(ShopMenuView view) {
        this.view = view;
    }


    public User getUser() {
        return this.user;
    }


    public final void buyCard(String cardName) {
        DataManager dataManager = DataManager.getInstance();
        CardTemplate cardTemplate = dataManager.getCardTemplateByName(cardName);
        if (cardTemplate == null) {
            view.printBuyCardMessage(ShopMenuMessage.NO_CARD_EXISTS);
            return;
        }
        if (cardTemplate.getPrice() > user.getMoney()) {
            view.printBuyCardMessage(ShopMenuMessage.NOT_ENOUGH_MONEY);
            return;
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
        view.printBuyCardMessage(ShopMenuMessage.CARD_SUCCESSFULLY_PURCHASED);
    }


    public void increaseMoney(long amount) {
        user.increaseMoney(amount);
        view.showMoneyIncreased();
    }
}
