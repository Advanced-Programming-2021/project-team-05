package control.controller;

import com.google.gson.JsonObject;
import control.DataManager;
import control.Receiver;
import control.message.LoginMenuMessage;
import model.User;

import java.util.UUID;

public class LoginMenuController {


    public static void parseCommand(String commandName, JsonObject infoObject, Receiver receiver) {
        JsonObject responseObject;
        switch (commandName) {
            case "create_user":
                responseObject = createUser(infoObject);
                break;
            case "login_user":
                responseObject = loginUser(infoObject);
                break;
            default:
                responseObject = new JsonObject();
                responseObject.addProperty("message", String.valueOf(LoginMenuMessage.ERROR));
        }
        receiver.sendResponse(responseObject.toString());
    }


    public static JsonObject createUser(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "login");
        responseObject.addProperty("command_name", "create_user_response");
        responseObject.add("info", responseInfoObject);
        String username = infoObject.get("username").getAsString();
        if (username.contains(" ")) {
            responseInfoObject.addProperty("message", String.valueOf(LoginMenuMessage.USERNAME_CONTAIN_SPACE));
            return responseObject;
        }
        String nickname = infoObject.get("nickname").getAsString();
        if (nickname.contains(" ")) {
            responseInfoObject.addProperty("message", String.valueOf(LoginMenuMessage.NICKNAME_CONTAIN_SPACE));
            return responseObject;
        }
        String password = infoObject.get("password").getAsString();
        if (password.contains(" ")) {
            responseInfoObject.addProperty("message", String.valueOf(LoginMenuMessage.PASSWORD_CONTAIN_SPACE));
            return responseObject;
        }
        DataManager dataManager = DataManager.getInstance();
        if (dataManager.getUserByUsername(username) != null) {
            responseInfoObject.addProperty("message", String.valueOf(LoginMenuMessage.USERNAME_EXISTS));
            return responseObject;
        }
        if (dataManager.getUserByNickname(nickname) != null) {
            responseInfoObject.addProperty("message", String.valueOf(LoginMenuMessage.NICKNAME_EXISTS));
            return responseObject;
        }
        User user = new User(username, password, nickname);
        dataManager.addUser(user);
        responseInfoObject.addProperty("message", String.valueOf(LoginMenuMessage.USER_CREATED));
        return responseObject;
    }


    public static JsonObject loginUser(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "login");
        responseObject.addProperty("command_name", "login_user_response");
        responseObject.add("info", responseInfoObject);
        String username = infoObject.get("username").getAsString();
        if (username.contains(" ")) {
            responseInfoObject.addProperty("message", String.valueOf(LoginMenuMessage.USERNAME_CONTAIN_SPACE));
            return responseObject;
        }
        String password = infoObject.get("password").getAsString();
        if (password.contains(" ")) {
            responseInfoObject.addProperty("message", String.valueOf(LoginMenuMessage.PASSWORD_CONTAIN_SPACE));
            return responseObject;
        }
        User user = DataManager.getInstance().getUserByUsername(username);
        if (user == null || !password.equals(user.getPassword())) {
            responseInfoObject.addProperty("message", String.valueOf(LoginMenuMessage.NO_MATCH));
            return responseObject;
        }
        DataManager dataManager = DataManager.getInstance();
        String token = UUID.randomUUID().toString();
        dataManager.addLoggedInUser(token, username);

        responseInfoObject.addProperty("message", String.valueOf(LoginMenuMessage.LOGGED_IN));
        responseInfoObject.addProperty("token", token);
        return responseObject;
    }
}
