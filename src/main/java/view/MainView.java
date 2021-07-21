package view;

import control.DataManager;
import control.controller.LoginMenuController;
import control.controller.MainController;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class MainView extends Application {

    public static Stage stage;
    public static MediaPlayer player;

    public static void main(String[] args) {
        DataManager.getInstance().loadData();
        launch(args);
    }


    @Override
    public void start(Stage stage) {
        initializeFonts();
        // TODO: 2021-07-13 uncomment
//        playMusic();
        if (!MainController.initializeNetwork()) {
            stop();
            return;
        }
        try {
            MainView.stage = stage;
            stage.setTitle("Yu-Gi-Oh");
            stage.setResizable(false);
            stage.getIcons().add(new Image(getClass().getResource("/images/icon.png").toExternalForm()));
            LoginMenuController controller = new LoginMenuController();
            LoginMenuView view = new LoginMenuView(controller);
            view.setWelcomeScene();
            stage.show();
        } catch (Exception e) {
            System.out.println("Failed to initialize stage");
        }
    }


    @Override
    public void stop() {
        MainController.finish();
        System.exit(0);
    }


    private void initializeFonts() {
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Bold.ttf"), 20);
    }


    private void playMusic() {
        try {
            String path = "src/main/resources/music/main-music.mp3";
            Media media = new Media(Paths.get(path).toUri().toString());
            player = new MediaPlayer(media);
            player.autoPlayProperty().setValue(true);
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.play();
        } catch (Exception e) {
            System.out.println("music error");
        }
    }
}
