package control.controller;

import com.google.gson.JsonObject;
import javafx.application.Platform;
import view.ScoreboardMenuView;

public class ScoreboardMenuController extends Controller {

    private ScoreboardMenuView view;


    public void setView(ScoreboardMenuView view) {
        this.view = view;
        super.view = view;
    }

    @Override
    public void parseCommand(JsonObject command) {
        String commandName = command.get("command_name").getAsString();
        if ("refresh_scoreboard".equals(commandName))
            Platform.runLater(() -> view.updateScoreboardScene());
    }
}
