package control.controller;

import com.google.gson.JsonObject;
import control.Receiver;
import control.Sender;
import javafx.collections.ObservableList;
import model.*;
import model.card.Card;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class MainController {

    private static String token;
    private static Controller activeController;
    private static Sender sender;
    private static Receiver receiver;

    static {
        token = null;
    }


    public static String getToken() {
        return MainController.token;
    }

    public static void setToken(String token) {
        MainController.token = token;
    }


    public static Controller getActiveController() {
        return MainController.activeController;
    }

    public static void setActiveController(Controller activeController) {
        MainController.activeController = activeController;
    }


    public static User getUser() {
        DataController dataController = DataController.getInstance();
        dataController.updateUser();
        long currentTimeMillis = System.currentTimeMillis();
        long end = currentTimeMillis + 5000;
        while (System.currentTimeMillis() < end) {
            if (dataController.getUser() != null) break;
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        return dataController.getUser();
    }

    public static ArrayList<ShopItem> getShopItems() {
        DataController dataController = DataController.getInstance();
        dataController.updateShopItems();
        long currentTimeMillis = System.currentTimeMillis();
        long end = currentTimeMillis + 5000;
        while (System.currentTimeMillis() < end) {
            if (dataController.getShopItems() != null) break;
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        return dataController.getShopItems();
    }

    public static ArrayList<Deck> getDecks() {
        DataController dataController = DataController.getInstance();
        dataController.updateDecks();
        long currentTimeMillis = System.currentTimeMillis();
        long end = currentTimeMillis + 5000;
        while (System.currentTimeMillis() < end) {
            if (dataController.getDecks() != null) break;
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        return dataController.getDecks();
    }

    public static DeckInfo getDeckInfo(String deckName) {
        DataController dataController = DataController.getInstance();
        dataController.updateDeckInfo(deckName);
        long currentTimeMillis = System.currentTimeMillis();
        long end = currentTimeMillis + 5000;
        while (System.currentTimeMillis() < end) {
            if (dataController.getDeckInfo() != null) break;
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        return dataController.getDeckInfo();
    }

    public static ArrayList<Card> getAddableCards(String deckName) {
        DataController dataController = DataController.getInstance();
        dataController.updateAddableCards(deckName);
        long currentTimeMillis = System.currentTimeMillis();
        long end = currentTimeMillis + 5000;
        while (System.currentTimeMillis() < end) {
            if (dataController.getAddableCards() != null) break;
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        return dataController.getAddableCards();
    }

    public static ObservableList<ScoreboardItem> getScoreboardItems() {
        DataController dataController = DataController.getInstance();
        dataController.updateScoreboardItems();
        long currentTimeMillis = System.currentTimeMillis();
        long end = currentTimeMillis + 5000;
        while (System.currentTimeMillis() < end) {
            if (dataController.getScoreboardItems() != null) break;
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        return dataController.getScoreboardItems();
    }

    public static ArrayList<Message> getMessages() {
        DataController dataController = DataController.getInstance();
        dataController.updateMessages();
        long currentTimeMillis = System.currentTimeMillis();
        long end = currentTimeMillis + 5000;
        while (System.currentTimeMillis() < end) {
            if (dataController.getMessages() != null) break;
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }
        return dataController.getMessages();
    }


    public static boolean initializeNetwork() {
        try {
            Socket socket = new Socket("localhost", 7355);
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            sender = new Sender(socket, dataOutputStream);
            receiver = new Receiver(dataInputStream);
            receiver.start();
            return true;
        } catch (IOException e) {
            System.out.println("Failed to connect to server");
            return false;
        }
    }

    public static void sendMessage(String message) {
        sender.send(message);
    }


    public static void finish() {
        logOut();
        if (receiver != null) receiver.finish();
        if (sender != null) sender.finish();
    }

    public static void logOut() {
        if (token == null) return;
        JsonObject infoObject = new JsonObject();
        infoObject.addProperty("token", token);
        JsonObject commandObject = new JsonObject();
        commandObject.addProperty("command_type", "main");
        commandObject.addProperty("command_name", "logout_user");
        commandObject.add("info", infoObject);
        sender.send(commandObject.toString());
    }
}
