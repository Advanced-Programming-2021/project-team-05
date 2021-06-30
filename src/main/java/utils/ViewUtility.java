package utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import model.ScoreboardItem;
import model.User;

import java.util.Optional;

public class ViewUtility {

    public static void showConfirmationAlert(String title, String header, String message, String cancelText, String confirmText, Listener listener) {
        Alert alert = getAlert(title, header, message);

        ButtonType cancelButtonType = new ButtonType(cancelText, ButtonBar.ButtonData.NO);
        ButtonType confirmButtonType = new ButtonType(confirmText, ButtonBar.ButtonData.APPLY);
        alert.getButtonTypes().add(cancelButtonType);
        alert.getButtonTypes().add(confirmButtonType);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get().equals(confirmButtonType)) listener.onConfirm();
        else listener.onCancel();
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

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("game-alert");
        dialogPane.getStylesheets().add(ViewUtility.class.getResource("/css/alert.css").toExternalForm());
        return alert;
    }


    public static void addScoreboardToScene(Scene scene, String userNickname) {
        // TODO: 2021-06-26 delete this
        ObservableList<ScoreboardItem> scoreboardItems = FXCollections.observableArrayList();
        for (int i = 0; i < 20; i++) {
            scoreboardItems.add(new ScoreboardItem(String.valueOf(i + 1), "test" + i, String.valueOf(20 - i)));
        }

        TableColumn<ScoreboardItem, String> rankColumn = new TableColumn<>("Rank");
        rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
        rankColumn.setId("rank-column");
        rankColumn.setPrefWidth(100);
        rankColumn.setResizable(false);
        rankColumn.setSortable(false);

        TableColumn<ScoreboardItem, String> nicknameColumn = new TableColumn<>("Nickname");
        nicknameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        nicknameColumn.setId("nickname-column");
        nicknameColumn.setPrefWidth(230);
        nicknameColumn.setResizable(false);
        nicknameColumn.setSortable(false);

        TableColumn<ScoreboardItem, String> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreColumn.setId("score-column");
        scoreColumn.setPrefWidth(150);
        scoreColumn.setResizable(false);
        scoreColumn.setSortable(false);

        TableView<ScoreboardItem> tableView = new TableView<>();
        tableView.setId("scoreboard-table");
        tableView.getColumns().addAll(rankColumn, nicknameColumn, scoreColumn);
        // TODO: 2021-06-26 delete next line, uncomment other line
        tableView.setItems(scoreboardItems);
//        tableView.setItems(DataManager.getInstance().getScoreboardItems());
        tableView.setEditable(true);
        tableView.setRowFactory(tv -> new TableRow<ScoreboardItem>() {
            @Override
            public void updateItem(ScoreboardItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) return;
                if (userNickname.equals(item.getNickname())) setId("current-user-row");
                else setId(null);
            }
        });

        HBox scoreboardContainer = (HBox) scene.lookup("#scoreboard-container");
        scoreboardContainer.getChildren().add(tableView);
    }


    public static void setProfileValues(Scene scene, User user) {
        Label usernameLabel = (Label) scene.lookup("#username-label");
        usernameLabel.setText("Username: " + user.getUsername());

        Label nicknameLabel = (Label) scene.lookup("#nickname-label");
        nicknameLabel.setText("Nickname: " + user.getNickname());

        String imagePath = "/images/profile-pics/" + user.getProfilePictureName();
        ImageView imageView = new ImageView(new Image(ViewUtility.class.getResource(imagePath).toExternalForm()));
        imageView.setId("profile-pic");
        HBox profilePicContainer = (HBox) scene.lookup("#profile-pic-container");
        profilePicContainer.getChildren().add(imageView);
    }
}
