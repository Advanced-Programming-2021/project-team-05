package control.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import control.Sender;
import model.User;

public class MainController {

    private static String token;

    static {
        token = null;
    }


    public static String getToken() {
        return MainController.token;
    }

    public static void setToken(String token) {
        MainController.token = token;
    }


    public static User getUser() {
        JsonObject infoObject = new JsonObject();
        infoObject.addProperty("token", token);
        JsonObject commandObject = new JsonObject();
        commandObject.addProperty("command_type", "data");
        commandObject.addProperty("command_name", "get_user_by_token");
        commandObject.add("info", infoObject);

        String response = Sender.sendAndGetResponse(commandObject.toString());
        if (response == null) return null;
        JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
        return new Gson().fromJson(responseObject.get("data"), User.class);
    }


    public static void logOut() {
        if (token == null) return;
        JsonObject infoObject = new JsonObject();
        infoObject.addProperty("token", token);
        JsonObject commandObject = new JsonObject();
        commandObject.addProperty("command_type", "main");
        commandObject.addProperty("command_name", "logout_user");
        commandObject.add("info", infoObject);
        Sender.send(commandObject.toString());
    }
}
