package view;

import control.controller.MainController;
import control.controller.MainMenuController;
import control.controller.ShopMenuController;
import control.message.ShopMenuMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import model.ShopItem;
import model.User;
import model.template.CardTemplate;
import utils.ViewUtility;

import java.io.IOException;
import java.util.ArrayList;

public class ShopMenuView extends View implements CheatRunner {

    private final ShopMenuController controller;


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
            updateShopScene(MainController.getUser());
            scene.setOnKeyPressed(keyEvent -> handleConsoleKeyEvent(keyEvent, this));
        } catch (IOException e) {
            System.out.println("Failed to load shop scene");
        }
    }

    private void initializeShopSceneButtons() {
        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnAction(e -> {
            if (controller.isWaiting())
                ViewUtility.showInformationAlert("", "Error", "You can't do this now");
            else {
                MainMenuController mainMenuController = new MainMenuController(MainController.getUser());
                MainMenuView mainMenuView = new MainMenuView(mainMenuController);
                mainMenuView.setMainMenuScene();
            }
        });
    }

    public void updateShopScene(User user) {
        if (user == null) {
            ViewUtility.showInformationAlert("Shop", "Error", "Failed to load shop");
            return;
        }
        Label moneyLabel = (Label) scene.lookup("#money-label");
        moneyLabel.setText("Money: " + user.getMoney());

        FlowPane cardsContainer = (FlowPane) scene.lookup("#cards-container");
        cardsContainer.getChildren().clear();
        ArrayList<ShopItem> shopItems = MainController.getShopItems();
        if (shopItems == null) {
            ViewUtility.showInformationAlert("Shop", "Error", "Failed to load shop");
            return;
        }
        for (ShopItem shopItem : shopItems) {
            CardTemplate template = shopItem.getCardTemplate();
            ImageView cardImage = ViewUtility.getCardImageView(template.getName());
            cardImage.getStyleClass().add("shop-image");
            cardImage.setFitWidth(184);
            cardImage.setFitHeight(300);
            cardImage.setOnMouseClicked(e -> ViewUtility.showCard(template.getName()));

            Label countLabel = new Label("Purchased: " + shopItem.getPurchasedCount());
            countLabel.getStyleClass().addAll("default-label", "number-label");

            Button buyButton = new Button("Buy (" + template.getPrice() + ")");
            buyButton.getStyleClass().addAll("default-button", "buy-button");
            if (user.getMoney() < template.getPrice()) buyButton.setDisable(true);

            buyButton.setOnAction(event -> {
                if (controller.isWaiting())
                    ViewUtility.showInformationAlert("", "Error", "You can't do this now");
                else {
                    controller.buyCard(template.getName());
                    updateShopScene(MainController.getUser());
                }
            });

            VBox container = new VBox(2, cardImage, countLabel, buyButton);
            container.setPrefWidth(184);
            container.setPrefHeight(364);
            cardsContainer.getChildren().add(container);
        }
    }

    public void showBuyCardMessage(ShopMenuMessage message) {
        switch (message) {
            case NOT_ENOUGH_MONEY:
                ViewUtility.showInformationAlert("Shop", "Buy Card", "Not enough money");
                break;
            case CARD_SUCCESSFULLY_PURCHASED:
                break;
            default:
                ViewUtility.showInformationAlert("Shop", "Buy Card", "Unexpected error");
        }
    }


    @Override
    public void runCheat(String command) {
        if (command.matches("^increase --money \\d+$"))
            increaseMoney(command.split("\\s"));
    }

    public void increaseMoney(String[] command) {
        long amount;
        try {
            amount = Long.parseLong(command[2]);
        } catch (NumberFormatException e) {
            return;
        }
        controller.increaseMoney(amount);
    }
}
