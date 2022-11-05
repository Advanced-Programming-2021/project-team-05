package control.controller;

import com.google.gson.JsonObject;
import control.DataManager;
import control.Receiver;
import control.message.LoginMenuMessage;
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


    public static void parseCommand(String commandName, JsonObject infoObject, Receiver receiver) {
        JsonObject responseObject;
        switch (commandName) {
            case "buy_card":
                responseObject = buyCard(infoObject);
                break;
            case "increase_money":
                responseObject = increaseMoney(infoObject);
                break;
            default:
                responseObject = new JsonObject();
                responseObject.addProperty("message", String.valueOf(LoginMenuMessage.ERROR));
        }
        if (responseObject != null) receiver.sendResponse(responseObject.toString());
    }


    public static JsonObject buyCard(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "shop");
        responseObject.addProperty("command_name", "buy_card_response");
        responseObject.add("info", responseInfoObject);
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            responseInfoObject.addProperty("message", String.valueOf(ShopMenuMessage.ERROR));
            return responseObject;
        }
        String cardName = infoObject.get("card_name").getAsString();
        CardTemplate cardTemplate = dataManager.getCardTemplateByName(cardName);
        if (cardTemplate == null) {
            responseInfoObject.addProperty("message", String.valueOf(ShopMenuMessage.NO_CARD_EXISTS));
            return responseObject;
        }
        if (cardTemplate.getPrice() > user.getMoney()) {
            responseInfoObject.addProperty("message", String.valueOf(ShopMenuMessage.NOT_ENOUGH_MONEY));
            return responseObject;
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
        responseInfoObject.addProperty("message", String.valueOf(ShopMenuMessage.CARD_SUCCESSFULLY_PURCHASED));
        return responseObject;
    }


    @SuppressWarnings("SameReturnValue")
    public static JsonObject increaseMoney(JsonObject infoObject) {
        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) return null;
        long amount = infoObject.get("amount").getAsLong();
        user.increaseMoney(amount);
        return null;
    }
}
