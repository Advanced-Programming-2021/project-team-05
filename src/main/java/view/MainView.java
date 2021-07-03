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
    public void start(Stage stage) throws Exception {
        MainView.stage = stage;
        stage.setTitle("Yu-Gi-Oh");
        stage.setResizable(false);
        LoginMenuView.setController(new LoginMenuController());
        new LoginMenuView().setWelcomeScene();
        stage.show();
    }
}
