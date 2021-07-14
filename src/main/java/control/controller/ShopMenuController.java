package control.controller;

import com.google.gson.*;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import control.Sender;
import control.message.ShopMenuMessage;
import model.ShopItem;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import view.MainView;
import view.ShopMenuView;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ShopMenuController {

    private ShopMenuView view;


    public void setView(ShopMenuView view) {
        this.view = view;
    }


    private RuntimeTypeAdapterFactory<CardTemplate> getCardTemplateAdapter() {
        return RuntimeTypeAdapterFactory
                .of(CardTemplate.class, "card_template_type")
                .registerSubtype(MonsterTemplate.class, MonsterTemplate.class.getName())
                .registerSubtype(SpellTemplate.class, SpellTemplate.class.getName())
                .registerSubtype(TrapTemplate.class, TrapTemplate.class.getName());
    }


    public ArrayList<ShopItem> getShopItems() {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "data");
            commandObject.addProperty("command_name", "get_shop_items");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return null;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            JsonObject data = responseObject.get("data").getAsJsonObject();
            JsonArray templatesArray = data.get("templates").getAsJsonArray();
            JsonArray purchasedCounts = data.get("purchased_counts").getAsJsonArray();

            RuntimeTypeAdapterFactory<CardTemplate> cardTemplateAdapter = getCardTemplateAdapter();
            Gson gson = new GsonBuilder().registerTypeAdapterFactory(cardTemplateAdapter).create();
            Type templateType = CardTemplate.class;
            ArrayList<ShopItem> shopItems = new ArrayList<>();
            for (int i = 0; i < templatesArray.size(); i++) {
                JsonObject templateObject = templatesArray.get(i).getAsJsonObject();
                CardTemplate template = gson.fromJson(templateObject, templateType);
                int count = purchasedCounts.get(i).getAsInt();
                shopItems.add(new ShopItem(template, count));
            }
            return shopItems;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public final void buyCard(String cardName) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("card_name", cardName);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "shop");
            commandObject.addProperty("command_name", "buy_card");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            ShopMenuMessage message = ShopMenuMessage.valueOf(responseObject.get("message").getAsString());
            view.showBuyCardMessage(message);
        } catch (Exception e) {
            view.showBuyCardMessage(ShopMenuMessage.ERROR);
        }
    }


    public void increaseMoney(long amount) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("amount", amount);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "shop");
            commandObject.addProperty("command_name", "increase_money");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            ShopMenuMessage message = ShopMenuMessage.valueOf(responseObject.get("message").getAsString());
            view.showBuyCardMessage(message);
        } catch (Exception ignored) {
        }
        view.updateShopScene(MainController.getUser());
    }
}
