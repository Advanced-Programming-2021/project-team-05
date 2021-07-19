package control.controller;

import com.google.gson.*;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import control.Sender;
import control.message.ShopMenuMessage;
import javafx.application.Platform;
import model.ShopItem;
import model.template.CardTemplate;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import view.MainView;
import view.ShopMenuView;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ShopMenuController extends Controller {

    private ShopMenuView view;


    public void setView(ShopMenuView view) {
        this.view = view;
        super.view = view;
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
            MainController.sendMessage(commandObject.toString());
            startWaiting();
        } catch (Exception e) {
            e.printStackTrace();
            view.showBuyCardMessage(ShopMenuMessage.ERROR);
        }
    }

    private void checkBuyCardResponse(JsonObject infoObject) {
        try {
            if (waitTimeline == null) return;
            else stopWaiting();
            ShopMenuMessage message = ShopMenuMessage.valueOf(infoObject.get("message").getAsString());
            view.showBuyCardMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
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
            MainController.sendMessage(commandObject.toString());
        } catch (Exception ignored) {
        }
        view.updateShopScene(MainController.getUser());
    }


    @Override
    public void parseCommand(JsonObject command) {
        String commandName = command.get("command_name").getAsString();
        JsonObject infoObject = command.get("info").getAsJsonObject();
        switch (commandName) {
            case "buy_card_response":
                Platform.runLater(() -> checkBuyCardResponse(infoObject));
                break;
        }
    }
}
