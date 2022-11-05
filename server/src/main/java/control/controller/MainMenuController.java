package control.controller;

import com.google.gson.JsonObject;
import control.DataManager;
import control.Receiver;
import control.message.LoginMenuMessage;

public class MainMenuController {


    public static void parseCommand(String commandName, JsonObject infoObject, Receiver receiver) {
        JsonObject responseObject;
        if ("logout_user".equals(commandName)) {
            responseObject = logoutUser(infoObject);
        } else {
            responseObject = new JsonObject();
            responseObject.addProperty("message", String.valueOf(LoginMenuMessage.ERROR));
        }
        if (responseObject != null) receiver.sendResponse(responseObject.toString());
    }


    @SuppressWarnings("SameReturnValue")
    private static JsonObject logoutUser(JsonObject infoObject) {
        JsonObject responseObject = new JsonObject();
        String token = infoObject.get("token").getAsString();
        responseObject.addProperty("token", token);
        DataManager.getInstance().removeLoggedInUser(token);
        return null;
    }
}
