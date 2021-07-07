package view;

import control.controller.ImportExportMenuController;
import control.controller.MainMenuController;
import control.message.ImportExportMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;


public class ImportExportMenuView {

    private final ImportExportMenuController controller;
    private Scene scene;


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
    }


    private void importOrExportCard(String[] command, boolean importCard) {
        String cardName = command[2].replace('_', ' ');
        if (importCard) {
            System.out.println("please enter card type: (monster/spell/trap)");
            String typeString;
            while (true) {
//                typeString = Utility.getNextLine();
//                if (!typeString.equals("monster") && !typeString.equals("spell") && !typeString.equals("trap")) {
//                    System.out.println("invalid type");
//                    continue;
//                }
//                break;
            }
//            controller.importCard(cardName, typeString, true);
        } else {
//            controller.exportCard(cardName);
        }
    }

    public void printImportExportCardMessage(ImportExportMessage message) {
        switch (message) {
            case NO_CARD_EXISTS:
                System.out.println("no card with this name exists");
                break;
            case INVALID_CARD_TYPE:
                System.out.println("card type is invalid");
                break;
            case CARD_EXISTS:
                System.out.println("card with entered name exists");
                break;
            case IMPORT_FAILED:
                System.out.println("unable to import card");
                break;
            case IMPORT_SUCCESSFUL:
                System.out.println("card imported successfully!");
                break;
            case EXPORT_SUCCESSFUL:
                System.out.println("card exported successfully!");
                break;
            default:
                System.out.println("unexpected error");
        }
    }


    public void showCurrentMenu() {
        System.out.println("Import/Export Menu");
    }


    public void showHelp() {
        System.out.print(
                "commands:\r\n" +
                        "\timport card <card name>\r\n" +
                        "\texport card <card name>\r\n" +
                        "\tmenu show-current\r\n" +
                        "\tmenu exit\r\n" +
                        "\tmenu help\r\n"
        );
    }
}
