package control.controller;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import control.Sender;
import control.message.DeckMenuMessage;
import javafx.application.Platform;
import model.Deck;
import model.DeckInfo;
import model.card.Card;
import model.card.Monster;
import model.card.Spell;
import model.card.Trap;
import model.template.MonsterTemplate;
import model.template.SpellTemplate;
import model.template.TrapTemplate;
import view.DeckMenuView;
import view.MainView;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DeckMenuController extends Controller {

    private DeckMenuView view;


    public void setView(DeckMenuView view) {
        this.view = view;
        super.view = view;
    }


    public final void createDeck(String deckName) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "deck");
            commandObject.addProperty("command_name", "create_deck");
            commandObject.add("info", infoObject);
            MainController.sendMessage(commandObject.toString());
            startWaiting();
        } catch (Exception e) {
            view.showCreateDeckMessage(DeckMenuMessage.ERROR);
        }
    }

    private void checkCreateDeckResponse(JsonObject infoObject) {
        try {
            if (waitTimeline == null) return;
            else stopWaiting();
            DeckMenuMessage message = DeckMenuMessage.valueOf(infoObject.get("message").getAsString());
            view.showCreateDeckMessage(message);
        } catch (Exception e) {
            view.showCreateDeckMessage(DeckMenuMessage.ERROR);
        }
    }


    public final void deleteDeck(String deckName) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "deck");
            commandObject.addProperty("command_name", "delete_deck");
            commandObject.add("info", infoObject);
            MainController.sendMessage(commandObject.toString());
            startWaiting();
        } catch (Exception e) {
            view.showDeleteDeckMessage(DeckMenuMessage.ERROR);
        }
    }

    private void checkDeleteDeckResponse(JsonObject infoObject) {
        try {
            if (waitTimeline == null) return;
            else stopWaiting();
            DeckMenuMessage message = DeckMenuMessage.valueOf(infoObject.get("message").getAsString());
            view.showDeleteDeckMessage(message);
        } catch (Exception e) {
            view.showDeleteDeckMessage(DeckMenuMessage.ERROR);
        }
    }


    public final void activateDeck(String deckName) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "deck");
            commandObject.addProperty("command_name", "activate_deck");
            commandObject.add("info", infoObject);
            MainController.sendMessage(commandObject.toString());
            startWaiting();
        } catch (Exception e) {
            view.showActivateDeckMessage(DeckMenuMessage.ERROR);
        }
    }

    private void checkActivateDeckResponse(JsonObject infoObject) {
        try {
            if (waitTimeline == null) return;
            else stopWaiting();
            DeckMenuMessage message = DeckMenuMessage.valueOf(infoObject.get("message").getAsString());
            view.showActivateDeckMessage(message);
        } catch (Exception e) {
            view.showActivateDeckMessage(DeckMenuMessage.ERROR);
        }
    }


    public final void addCard(String deckName, String cardName, boolean isSideDeck) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            infoObject.addProperty("card_name", cardName);
            infoObject.addProperty("is_side", isSideDeck);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "deck");
            commandObject.addProperty("command_name", "add_card");
            commandObject.add("info", infoObject);
            MainController.sendMessage(commandObject.toString());
            startWaiting();
        } catch (Exception e) {
            view.showAddCardMessage(DeckMenuMessage.ERROR);
        }
    }

    private void checkAddCardResponse(JsonObject infoObject) {
        try {
            if (waitTimeline == null) return;
            else stopWaiting();
            DeckMenuMessage message = DeckMenuMessage.valueOf(infoObject.get("message").getAsString());
            view.showAddCardMessage(message);
        } catch (Exception e) {
            view.showAddCardMessage(DeckMenuMessage.ERROR);
        }
    }


    public final void removeCard(String deckName, String cardName, boolean isSideDeck) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("deck_name", deckName);
            infoObject.addProperty("card_name", cardName);
            infoObject.addProperty("is_side", isSideDeck);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "deck");
            commandObject.addProperty("command_name", "remove_card");
            commandObject.add("info", infoObject);
            MainController.sendMessage(commandObject.toString());
            startWaiting();
        } catch (Exception e) {
            view.showRemoveCardMessage(DeckMenuMessage.ERROR);
        }
    }

    private void checkRemoveCardResponse(JsonObject infoObject) {
        try {
            if (waitTimeline == null) return;
            else stopWaiting();
            DeckMenuMessage message = DeckMenuMessage.valueOf(infoObject.get("message").getAsString());
            view.showRemoveCardMessage(message);
        } catch (Exception e) {
            view.showRemoveCardMessage(DeckMenuMessage.ERROR);
        }
    }


    @Override
    public void parseCommand(JsonObject command) {
        String commandName = command.get("command_name").getAsString();
        JsonObject infoObject = command.get("info").getAsJsonObject();
        switch (commandName) {
            case "create_deck_response":
                Platform.runLater(() -> checkCreateDeckResponse(infoObject));
                break;
            case "delete_deck_response":
                Platform.runLater(() -> checkDeleteDeckResponse(infoObject));
                break;
            case "activate_deck_response":
                Platform.runLater(() -> checkActivateDeckResponse(infoObject));
                break;
            case "add_card_response":
                Platform.runLater(() -> checkAddCardResponse(infoObject));
                break;
            case "remove_card_response":
                Platform.runLater(() -> checkRemoveCardResponse(infoObject));
                break;
        }
    }
}
