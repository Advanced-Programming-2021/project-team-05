package control.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import control.Sender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ScoreboardItem;
import view.MainView;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ScoreboardMenuController {

    public ObservableList<ScoreboardItem> getScoreboardItems() {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "data");
            commandObject.addProperty("command_name", "get_scoreboard_items");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return null;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            JsonArray scoreboardItems = responseObject.get("data").getAsJsonArray();

            Type itemType = new TypeToken<ArrayList<ScoreboardItem>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<ScoreboardItem> parsedItems = gson.fromJson(scoreboardItems, itemType);
            ObservableList<ScoreboardItem> scoreboardItemObservableList = FXCollections.observableArrayList();
            scoreboardItemObservableList.addAll(parsedItems);
            return scoreboardItemObservableList;
        } catch (Exception e) {
            return null;
        }
    }
}
