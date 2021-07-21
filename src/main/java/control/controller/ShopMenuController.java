package control.controller;

import com.google.gson.JsonObject;
import control.message.ShopMenuMessage;
import javafx.application.Platform;
import view.ShopMenuView;

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
        if ("buy_card_response".equals(commandName))
            Platform.runLater(() -> checkBuyCardResponse(infoObject));
    }
}
