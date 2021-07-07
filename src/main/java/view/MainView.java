package view;

import control.DataManager;
import control.controller.LoginMenuController;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainView extends Application {

    public static Stage stage;


    public static void main(String[] args) {
        DataManager.getInstance().loadData();
        launch(args);
    }


    @Override
    public void start(Stage stage) {
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

    public static void initializeStage(Stage stage) {
        stage.setTitle("Yu-Gi-Oh");
        stage.setResizable(false);
    }


    @Override
    public void stop() {
        System.exit(0);
    }
}
