package view;

import control.controller.MainMenuController;
import control.controller.ScoreboardMenuController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import model.ScoreboardItem;
import utils.ViewUtility;

import java.io.IOException;

public class ScoreboardMenuView {

    private final ScoreboardMenuController controller;
    private Scene scene;
    private Timeline refreshTimeline;


    public ScoreboardMenuView(ScoreboardMenuController controller) {
        this.controller = controller;
    }


    public void setScoreboardScene() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/scoreboard.fxml"));
            Parent root = loader.load();
            scene = new Scene(root);
            MainView.stage.setScene(scene);
            initializeScoreboardSceneButtons();
            updateScoreboardScene();

            KeyFrame refreshKeyFrame = new KeyFrame(Duration.seconds(5), e -> updateScoreboardScene());
            refreshTimeline = new Timeline(refreshKeyFrame);
            refreshTimeline.setCycleCount(Animation.INDEFINITE);
            refreshTimeline.play();
        } catch (IOException e) {
            System.out.println("Failed to load main menu scene");
        }
    }

    private void initializeScoreboardSceneButtons() {
        Button backButton = (Button) scene.lookup("#back-btn");
        backButton.setOnMouseClicked(e -> {
            refreshTimeline.stop();
            MainMenuController mainMenuController = new MainMenuController();
            MainMenuView mainMenuView = new MainMenuView(mainMenuController);
            mainMenuView.setMainMenuScene();
        });
        backButton.setOnAction(e -> {
            refreshTimeline.stop();
            MainMenuController mainMenuController = new MainMenuController();
            MainMenuView mainMenuView = new MainMenuView(mainMenuController);
            mainMenuView.setMainMenuScene();
        });

        Button refreshButton = (Button) scene.lookup("#refresh-btn");
        refreshButton.setOnMouseClicked(e -> updateScoreboardScene());
        refreshButton.setOnAction(e -> updateScoreboardScene());
    }

    public void updateScoreboardScene() {
        ObservableList<ScoreboardItem> scoreboardItems = controller.getScoreboardItems();
        if (scoreboardItems == null) {
            ViewUtility.showInformationAlert("Scoreboard", "Error", "Failed to get data from server");
            return;
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
        tableView.getColumns().add(rankColumn);
        tableView.getColumns().add(nicknameColumn);
        tableView.getColumns().add(scoreColumn);
        tableView.setItems(scoreboardItems);
        tableView.setEditable(true);
        tableView.setRowFactory(tv -> new TableRow<ScoreboardItem>() {
            @Override
            public void updateItem(ScoreboardItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) return;

                if (item.isCurrentUser()) setId("current-user-row");
                else if (item.isOnline()) setId("online-user-row");
                else setId(null);
            }
        });

        HBox scoreboardContainer = (HBox) scene.lookup("#scoreboard-container");
        scoreboardContainer.getChildren().clear();
        scoreboardContainer.getChildren().add(tableView);
    }
}
