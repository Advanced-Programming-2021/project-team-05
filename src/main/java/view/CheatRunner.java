package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public interface CheatRunner {

    void runCheat(String command);

    default void handleConsoleKeyEvent(KeyEvent keyEvent, CheatRunner cheatRunner) {
        if (keyEvent.isShiftDown() && keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.C)
            openConsole(cheatRunner);
    }

    default void openConsole(CheatRunner cheatRunner) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Console");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);

            Parent root = FXMLLoader.load(MainView.class.getResource("/fxml/cheat.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            TextField input = (TextField) scene.lookup("#input");
            input.requestFocus();

            TextArea output = (TextArea) scene.lookup("#output");
            output.setOnInputMethodTextChanged(e -> output.setScrollTop(0));

            scene.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    String command = input.getText().trim();
                    if (command.length() != 0) {
                        output.appendText(command + "\n");
                        input.setText("");
                        cheatRunner.runCheat(command);
                    }
                } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    stage.close();
                }
            });
        } catch (IOException e) {
            System.out.println("Failed to load console");
        }
    }
}
