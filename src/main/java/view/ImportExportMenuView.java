package view;

import control.DataManager;
import control.controller.ImportExportMenuController;
import control.controller.MainMenuController;
import control.message.ImportExportMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.template.CardTemplate;
import utils.ViewUtility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;


public class ImportExportMenuView {

    private final ImportExportMenuController controller;
    private Scene scene;

    private File selectedFile;
    private ChoiceBox<String> typeChoiceBox;


    public ImportExportMenuView(ImportExportMenuController controller) {
        this.controller = controller;
        controller.setView(this);
    }


    public void setImportExportScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/import-export.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeImportExportSceneButtons();
            initializeImportExportScene();
        } catch (IOException e) {
            System.out.println("Failed to load import export scene");
        }
    }

    private void initializeImportExportSceneButtons() {
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

        Button exportButton = (Button) scene.lookup("#export-btn");
        exportButton.setOnMouseClicked(e -> exportCard());
        exportButton.setOnAction(e -> exportCard());

        Button importButton = (Button) scene.lookup("#import-btn");
        importButton.setOnMouseClicked(e -> importCard());
        importButton.setOnAction(e -> importCard());
    }

    public void initializeImportExportScene() {
        typeChoiceBox = new ChoiceBox<>();
        typeChoiceBox.setId("type-box");
        typeChoiceBox.setValue("Select Card Type");
        typeChoiceBox.getItems().add("Select Card Type");
        typeChoiceBox.getItems().add("Monster");
        typeChoiceBox.getItems().add("Spell");
        typeChoiceBox.getItems().add("Trap");
        HBox choiceBoxContainer = (HBox) scene.lookup("#type-choice-box-container");
        choiceBoxContainer.getChildren().add(typeChoiceBox);

        Button fileButton = (Button) scene.lookup("#file-btn");
        fileButton.setOnMouseClicked(e -> openFileChooser(fileButton));
        fileButton.setOnAction(e -> openFileChooser(fileButton));
        fileButton.setOnDragOver(e -> {
            if (e.getDragboard().hasFiles()) e.acceptTransferModes(TransferMode.ANY);
        });
        fileButton.setOnDragDropped(e -> {
            selectedFile = e.getDragboard().getFiles().get(0);
            if (selectedFile.getName().matches(".*?\\.json")) fileButton.setText(selectedFile.getName());
            else ViewUtility.showInformationAlert("Import", "", "File type should be json");
        });
    }

    private void openFileChooser(Button fileButton) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Card");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        selectedFile = fileChooser.showOpenDialog(MainView.stage);
        if (selectedFile != null) fileButton.setText(selectedFile.getName());
    }


    private void exportCard() {
        try {
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Export Card");
            stage.initModality(Modality.APPLICATION_MODAL);

            Parent root = FXMLLoader.load(ViewUtility.class.getResource("/fxml/show-cards.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            FlowPane cardsContainer = (FlowPane) scene.lookup("#cards-container");
            Button backButton = (Button) scene.lookup("#back-btn");
            backButton.setOnMouseClicked(e -> stage.close());
            backButton.setOnAction(e -> stage.close());
            ArrayList<CardTemplate> cardTemplates = DataManager.getInstance().getCardTemplates();
            cardTemplates.sort(Comparator.comparing(CardTemplate::getName));
            cardTemplates.sort(Comparator.comparing(template -> template.getClass().getSimpleName()));
            for (CardTemplate template : cardTemplates) {
                ImageView cardImage = ViewUtility.getCardImageView(template.getName());
                cardImage.getStyleClass().add("card-image");
                cardImage.setFitWidth(184);
                cardImage.setFitHeight(300);
                cardImage.setOnMouseClicked(e -> {
                    controller.exportCard(template);
                    stage.close();
                });

                Button showButton = new Button("Show");
                showButton.getStyleClass().addAll("default-button", "show-button");
                showButton.setOnMouseClicked(e -> ViewUtility.showCard(template.getName()));
                showButton.setOnAction(e -> ViewUtility.showCard(template.getName()));

                VBox container = new VBox(2, cardImage, showButton);
                container.setPrefWidth(184);
                container.setPrefHeight(332);
                cardsContainer.getChildren().add(container);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void importCard() {
        if (selectedFile == null) {
            ViewUtility.showInformationAlert("Import", "", "Select a file first");
            return;
        }
        String type = typeChoiceBox.getValue();
        controller.importCard(selectedFile, type, true);
    }

    public void printImportExportCardMessage(ImportExportMessage message) {
        switch (message) {
            case NO_CARD_EXISTS:
                ViewUtility.showInformationAlert("Import/Export", "", "No card with this name exists");
                break;
            case INVALID_CARD_TYPE:
                ViewUtility.showInformationAlert("Import/Export", "", "Select card type first");
                break;
            case CARD_EXISTS:
                ViewUtility.showInformationAlert("Import/Export", "", "Card with entered name exists");
                break;
            case IMPORT_FAILED:
                ViewUtility.showInformationAlert("Import/Export", "", "Unable to import card");
                break;
            case IMPORT_SUCCESSFUL:
                ViewUtility.showInformationAlert("Import/Export", "", "Card imported successfully!");
                break;
            case EXPORT_SUCCESSFUL:
                ViewUtility.showInformationAlert("Import/Export", "", "Card exported successfully!");
                break;
            default:
                ViewUtility.showInformationAlert("Import/Export", "", "Unexpected error");
        }
    }
}
