import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utils.ViewUtility;

public class RunTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Regular.ttf"), 20);
        Font.loadFont(getClass().getResourceAsStream("/font/Merienda-Bold.ttf"), 20);

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/scoreboard.fxml"));
        Scene scene = new Scene(root);
        ViewUtility.addScoreboardToScene(scene, "test5");
        stage.setScene(scene);
        stage.setTitle("Yo-Gi-Oh!");
        stage.setResizable(false);
        stage.show();
    }
}
