package utils;

import control.DataManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

public class ViewUtility {

    public static void showConfirmationAlert(String title, String header, String message, String cancelText, String confirmText, Listener listener) {
        if (showConfirmationAlertAndReturn(title, header, message, cancelText, confirmText)) listener.onConfirm();
        else listener.onCancel();
    }

    public static boolean showConfirmationAlertAndReturn(String title, String header, String message, String cancelText, String confirmText) {
        Alert alert = getAlert(title, header, message);

        ButtonType cancelButtonType = new ButtonType(cancelText, ButtonBar.ButtonData.NO);
        ButtonType confirmButtonType = new ButtonType(confirmText, ButtonBar.ButtonData.APPLY);
        alert.getButtonTypes().add(cancelButtonType);
        alert.getButtonTypes().add(confirmButtonType);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().equals(confirmButtonType);
    }

    public static String getOneOfValues(String title, String header, String message, String firstValue, String secondValue) {
        Alert alert = getAlert(title, header, message);

        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
        ButtonType firstButtonType = new ButtonType(firstValue);
        ButtonType secondButtonType = new ButtonType(secondValue);
        alert.getButtonTypes().add(cancelButtonType);
        alert.getButtonTypes().add(firstButtonType);
        alert.getButtonTypes().add(secondButtonType);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && !result.get().equals(cancelButtonType)) return result.get().getText();
        else return null;
    }

    public static void showInformationAlert(String title, String header, String message) {
        Alert alert = getAlert(title, header, message);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().add(okButtonType);

        alert.show();
    }

    private static Alert getAlert(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.initStyle(StageStyle.TRANSPARENT);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("game-alert");
        dialogPane.getStylesheets().add(ViewUtility.class.getResource("/css/alert.css").toExternalForm());
        return alert;
    }


    public static void showPromptAlert(String title, String message, String label, String okText, PromptListener listener) {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle(title);
        textInputDialog.setHeaderText(message);
        textInputDialog.setContentText(label);
        textInputDialog.getDialogPane().getStyleClass().add("game-alert");
        textInputDialog.getDialogPane().getStylesheets().add(ViewUtility.class.getResource("/css/alert.css").toExternalForm());

        textInputDialog.getDialogPane().getButtonTypes().clear();
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
        ButtonType confirmButtonType = new ButtonType(okText, ButtonBar.ButtonData.OK_DONE);
        textInputDialog.getDialogPane().getButtonTypes().add(cancelButtonType);
        textInputDialog.getDialogPane().getButtonTypes().add(confirmButtonType);

        Optional<String> result = textInputDialog.showAndWait();
        String input = result.orElse(null);
        if (input != null) listener.onOk(input);
        else listener.onCancel();
    }


    public static void showCard(String cardName) {
        try {
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle(cardName);
            stage.initModality(Modality.APPLICATION_MODAL);

            Parent root = FXMLLoader.load(ViewUtility.class.getResource("/fxml/card.fxml"));
            Scene scene = new Scene(root);
            VBox container = (VBox) scene.lookup("#container");

            ImageView cardImage = ViewUtility.getCardImageView(cardName);
            cardImage.setFitWidth(334);
            cardImage.setFitHeight(500);
            container.getChildren().add(0, cardImage);

            String description = DataManager.getInstance().getCardTemplateByName(cardName).detailedToString();
            TextArea descriptionArea = new TextArea(description);
            descriptionArea.getStyleClass().add("description-box");
            descriptionArea.setEditable(false);
            descriptionArea.setWrapText(true);
            descriptionArea.setMinHeight(150);
            descriptionArea.setMaxHeight(150);
            container.getChildren().add(1, descriptionArea);

            Button backButton = (Button) scene.lookup("#back-button");
            backButton.setOnMouseClicked(e -> stage.close());
            backButton.setOnAction(e -> stage.close());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Image getCardImage(String cardName) {
        String imageAddress = "/images/cards/" + cardName.replaceAll(" ", "_") + ".jpg";
        URL url = ViewUtility.class.getResource(imageAddress);
        if (url == null) url = ViewUtility.class.getResource("/images/cards/New_Card.png");
        return new Image(url.toExternalForm());
    }

    public static ImageView getCardImageView(String cardName) {
        return new ImageView(getCardImage(cardName));
    }
}
