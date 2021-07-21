package view;

import control.controller.CreateCardController;
import control.controller.MainMenuController;
import control.message.CreateCardMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import model.template.property.MonsterAttribute;
import model.template.property.MonsterType;
import utils.ViewUtility;

import java.io.IOException;

public class CreateCardView {

    private final CreateCardController controller;
    private Scene scene;

    private Label moneyLabel;
    private TextField nameField;
    private TextArea descriptionArea;
    private TextField attackField;
    private TextField defenseField;
    private ChoiceBox<String> typeChoiceBox;
    private ChoiceBox<String> attributeChoiceBox;
    private Label priceLabel;
    private Label levelLabel;


    public CreateCardView(CreateCardController controller) {
        this.controller = controller;
        controller.setView(this);
    }


    public void setCreateCardScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/create-card.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeCreateCardScene();
        } catch (IOException e) {
            System.out.println("Failed to load create card scene");
        }
    }

    private void initializeCreateCardScene() {
        Button createButton = (Button) scene.lookup("#create-btn");
        createButton.setOnMouseClicked(e -> createCard());
        createButton.setOnAction(e -> createCard());

        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnMouseClicked(e -> {
            MainMenuController mainMenuController = new MainMenuController(controller.getUser());
            MainMenuView mainMenuView = new MainMenuView(mainMenuController);
            mainMenuView.setMainMenuScene();
        });
        backButton.setOnAction(e -> {
            MainMenuController mainMenuController = new MainMenuController(controller.getUser());
            MainMenuView mainMenuView = new MainMenuView(mainMenuController);
            mainMenuView.setMainMenuScene();
        });

        attackField = (TextField) scene.lookup("#attack-field");
        attackField.textProperty().addListener((observable, oldValue, newValue) -> updatePriceAndLevel());
        defenseField = (TextField) scene.lookup("#defense-field");
        defenseField.textProperty().addListener((observable, oldValue, newValue) -> updatePriceAndLevel());

        typeChoiceBox = new ChoiceBox<>();
        typeChoiceBox.setValue("Warrior");
        for (MonsterType type : MonsterType.values()) {
            typeChoiceBox.getItems().add(type.getName());
        }
        HBox typeBoxContainer = (HBox) scene.lookup("#type-box-container");
        typeBoxContainer.getChildren().clear();
        typeBoxContainer.getChildren().add(typeChoiceBox);

        attributeChoiceBox = new ChoiceBox<>();
        attributeChoiceBox.setValue("EARTH");
        for (MonsterAttribute type : MonsterAttribute.values()) {
            attributeChoiceBox.getItems().add(type.getName());
        }
        HBox attributeBoxContainer = (HBox) scene.lookup("#attribute-box-container");
        attributeBoxContainer.getChildren().clear();
        attributeBoxContainer.getChildren().add(attributeChoiceBox);

        moneyLabel = (Label) scene.lookup("#money-label");
        nameField = (TextField) scene.lookup("#name-field");
        descriptionArea = (TextArea) scene.lookup("#description-area");
        priceLabel = (Label) scene.lookup("#price-label");
        levelLabel = (Label) scene.lookup("#level-label");
        updateMoney();
    }

    private void updatePriceAndLevel() {
        try {
            int attack = Integer.parseInt(attackField.getText());
            int defense = Integer.parseInt(defenseField.getText());
            if (attack < 0 || defense < 0) {
                priceLabel.setText("Price: ?");
                levelLabel.setText("Level: ?");
            } else {
                priceLabel.setText("Price: " + controller.getMonsterPrice(attack, defense));
                levelLabel.setText("Level:" + controller.getMonsterLevel(attack, defense));
            }
        } catch (NumberFormatException e) {
            priceLabel.setText("Price: ?");
            levelLabel.setText("Level: ?");
        }
    }

    public void updateMoney() {
        moneyLabel.setText("Money: " + controller.getUser().getMoney());
    }


    private void createCard() {
        try {
            int attack = Integer.parseInt(attackField.getText());
            int defense = Integer.parseInt(defenseField.getText());
            if (attack < 0 || defense < 0) {
                ViewUtility.showInformationAlert("Create Card", "", "Attack and defense are not valid");
                return;
            }
            String name = nameField.getText();
            if (name.length() == 0 || name.length() != name.trim().length()) {
                ViewUtility.showInformationAlert("Create Card", "", "Entered name is not valid");
                return;
            }
            String description = descriptionArea.getText();
            if (description.length() == 0 || description.length() != description.trim().length()) {
                ViewUtility.showInformationAlert("Create Card", "", "Entered description is not valid");
                return;
            }
            MonsterType type = MonsterType.getMonsterTypeByName(typeChoiceBox.getValue());
            MonsterAttribute attribute = MonsterAttribute.getMonsterAttributeByName(attributeChoiceBox.getValue());
            controller.createMonster(name, description, type, attribute, attack, defense);
        } catch (NumberFormatException e) {
            ViewUtility.showInformationAlert("Create Card", "", "Attack and defense are not valid");
        }
    }

    public void showCreateCardMessage(CreateCardMessage message) {
        switch (message) {
            case NAME_EXISTS:
                ViewUtility.showInformationAlert("Create Card", "", "Card with this name exists");
                return;
            case ERROR:
                ViewUtility.showInformationAlert("Create Card", "", "Couldn't create card");
                return;
            default:
                ViewUtility.showInformationAlert("Create Card", "", "Unexpected error");
        }
    }
}
