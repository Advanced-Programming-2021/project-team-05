package view;

import control.DataManager;
import control.controller.MainMenuController;
import control.controller.ShopMenuController;
import control.message.ShopMenuMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import model.template.CardTemplate;
import utils.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;


public class ShopMenuView {

    private final ShopMenuController controller;
    private Scene scene;


    public ShopMenuView(ShopMenuController controller) {
        this.controller = controller;
        controller.setView(this);
    }


    public void setShopScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/shop.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeShopSceneButtons();
        } catch (IOException e) {
            System.out.println("Failed to load shop scene");
        }
    }

    private void initializeShopSceneButtons() {
        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnMouseClicked(e -> {
            MainMenuController mainMenuController = new MainMenuController(controller.getUser());
            MainMenuView mainMenuView = new MainMenuView(mainMenuController);
            mainMenuView.setMainMenuScene();
        });
    }


    public void buyCard(String[] command) {
        String cardName = command[2].replace('_', ' ');
        controller.buyCard(cardName);
    }

    public void printBuyCardMessage(ShopMenuMessage message) {
        switch (message) {
            case NO_CARD_EXISTS:
                System.out.println("there is no card with this name");
                break;
            case NOT_ENOUGH_MONEY:
                System.out.println("not enough money");
                break;
            case CARD_SUCCESSFULLY_PURCHASED:
                System.out.println("card bought successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void showAllCards() {
        DataManager dataManager = DataManager.getInstance();
        ArrayList<CardTemplate> allTemplates = dataManager.getCardTemplates();
        allTemplates.sort(Comparator.comparing(CardTemplate::getName));
        for (CardTemplate template : allTemplates) {
            System.out.println(template.getName() + ": " + template.getPrice());
        }
    }


    public void showCard(String[] command) {
        String cardName = command[2].replace('_', ' ');
        DataManager dataManager = DataManager.getInstance();
        CardTemplate template = dataManager.getCardTemplateByName(cardName);
        if (template == null) {
            System.out.println("invalid card name");
        } else {
            System.out.println(template.detailedToString());
        }
    }


    public void showCurrentMenu() {
        System.out.println("Shop Menu");
    }


    public void showHelp() {
        System.out.print(
                "commands:\r\n" +
                        "\tshop buy <card name>\r\n" +
                        "\tshop show --all\r\n" +
                        "\tcard show <card name>\r\n" +
                        "\tmenu show-current\r\n" +
                        "\tmenu exit\r\n" +
                        "\tmenu help\r\n"
        );
    }


    public void increaseMoney(String[] command) {
        long amount;
        try {
            amount = Long.parseLong(command[2]);
        } catch (NumberFormatException e) {
            System.out.println("invalid command");
            return;
        }
        controller.increaseMoney(amount);
    }

    public void showMoneyIncreased() {
        System.out.println("money increased!");
    }
}
