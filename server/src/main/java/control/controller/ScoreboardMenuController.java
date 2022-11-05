package control.controller;

import com.google.gson.JsonObject;
import control.Listener;

public class ScoreboardMenuController {

    public static void refreshScoreboard() {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "scoreboard");
        responseObject.addProperty("command_name", "refresh_scoreboard");
        responseObject.add("info", responseInfoObject);
        Listener.getListener().sendToAll(responseObject.toString());
    }
}
