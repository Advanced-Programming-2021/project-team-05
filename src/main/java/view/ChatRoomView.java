package view;

import control.controller.ChatRoomController;
import control.controller.MainController;
import control.controller.MainMenuController;
import control.message.ChatRoomMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Message;
import model.User;
import utils.ViewUtility;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ChatRoomView extends View {

    private final ChatRoomController controller;


    public ChatRoomView(ChatRoomController controller) {
        this.controller = controller;
        controller.setView(this);
    }


    public void setChatScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/chat.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeChatScene();
            updateChatScene();
        } catch (IOException e) {
            System.out.println("Failed to load chat scene");
        }
    }

    private void initializeChatScene() {
        ScrollPane scrollPane = (ScrollPane) scene.lookup("#scroll-pane");
        VBox messageBox = (VBox) scene.lookup("#messages-box");
        messageBox.heightProperty().addListener(observable -> scrollPane.setVvalue(1D));

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

        Button sendButton = (Button) scene.lookup("#send-btn");
        sendButton.setOnAction(e -> {
            if (controller.isWaiting())
                ViewUtility.showInformationAlert("", "Error", "You can't do this now");
            else sendMessage();
        });

        TextField inputBox = (TextField) scene.lookup("#input-box");
        inputBox.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) sendMessage();
        });
        inputBox.textProperty().addListener((observable, oldValue, newValue) -> sendButton.setDisable(newValue.trim().length() == 0));
    }

    public void updateChatScene() {
        ArrayList<Message> messages = MainController.getMessages();
        User user = MainController.getUser();
        if (messages == null || user == null) {
            ViewUtility.showInformationAlert("Chat Room", "Error", "Failed to update messages");
            return;
        }
        VBox messagesBox = (VBox) scene.lookup("#messages-box");
        messagesBox.getChildren().clear();
        for (Message message : messages) {
            HBox messageContainer = new HBox();
            messageContainer.getStyleClass().add("message-container");

            Label messageLabel = new Label(message.getNickname() + " says:\n" + message.getContent());
            messageLabel.getStyleClass().add("message-label");
            if (user.getNickname().equals(message.getNickname())) {
                messageContainer.getStyleClass().add("self-message-container");
                messageLabel.getStyleClass().add("self-message-label");

                messageContainer.getChildren().add(messageLabel);
            } else {
                URL imagePath = getClass().getResource("/images/profile-pics/" + message.getProfilePicName());
                ImageView messageImage = new ImageView(new Image(imagePath.toExternalForm()));
                messageImage.getStyleClass().add("message-image");
                messageImage.setFitWidth(60);
                messageImage.setFitHeight(71);

                messageContainer.getChildren().addAll(messageImage, messageLabel);
            }
            messagesBox.getChildren().add(messageContainer);
        }
    }


    private void sendMessage() {
        TextField inputBox = (TextField) scene.lookup("#input-box");
        String message = inputBox.getText().trim();
        inputBox.setText("");
        controller.sendMessage(message);
    }

    public void showSendMessage(ChatRoomMessage message) {
        switch (message) {
            case SUCCESS:
                break;
            case FAILED:
                ViewUtility.showInformationAlert("Error", "Error", "Failed to send message");
                break;
            default:
                ViewUtility.showInformationAlert("Error", "Error", "Unexpected Error");
        }
    }
}
