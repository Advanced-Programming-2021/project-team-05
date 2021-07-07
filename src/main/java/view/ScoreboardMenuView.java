package view;

import control.DataManager;
import control.controller.MainMenuController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.ScoreboardItem;
import model.User;

import java.io.IOException;


public class ScoreboardMenuView {

    private final User user;
    private Scene scene;


    public ScoreboardMenuView(User user) {
        this.user = user;
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
        } catch (IOException e) {
            System.out.println("Failed to load main menu scene");
        }
    }

    private void initializeScoreboardSceneButtons() {
        Button logoutButton = (Button) scene.lookup("#back-btn");
        logoutButton.setOnMouseClicked(e -> {
            MainMenuController mainMenuController = new MainMenuController(user);
            MainMenuView mainMenuView = new MainMenuView(mainMenuController);
            mainMenuView.setMainMenuScene();
        });
    }

    public void updateScoreboardScene() {
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
        tableView.setItems(DataManager.getInstance().getScoreboardItems());
        tableView.setEditable(true);
        tableView.setRowFactory(tv -> new TableRow<ScoreboardItem>() {
            @Override
            public void updateItem(ScoreboardItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) return;
                if (user.getNickname().equals(item.getNickname())) setId("current-user-row");
                else setId(null);
            }
        });

        HBox scoreboardContainer = (HBox) scene.lookup("#scoreboard-container");
        scoreboardContainer.getChildren().clear();
        scoreboardContainer.getChildren().add(tableView);
    }
}
