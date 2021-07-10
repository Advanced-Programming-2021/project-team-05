package view;

import control.DataManager;
import control.controller.LoginMenuController;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainView extends Application {

    public static Stage stage;


    public static void main(String[] args) {
        DataManager.getInstance().loadData();
        launch(args);
    }


    @Override
    public void start(Stage stage) {
        initializeFonts();
        try {
            MainView.stage = stage;
            stage.setTitle("Yu-Gi-Oh");
            stage.setResizable(false);
            LoginMenuController controller = new LoginMenuController();
            LoginMenuView view = new LoginMenuView(controller);
            view.setWelcomeScene();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void stop() {
        DataManager.getInstance().saveData();
        System.exit(0);
    }


    private void initializeFonts() {
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Bold.ttf"), 20);
    }
}
