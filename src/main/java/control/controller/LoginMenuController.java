package control.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import control.Sender;
import control.message.LoginMenuMessage;
import view.LoginMenuView;
import view.MainView;

public class LoginMenuController {

    private LoginMenuView view;


    public void setView(LoginMenuView view) {
        this.view = view;
    }


    public final void createUser(String username, String password, String nickname) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("username", username);
            infoObject.addProperty("password", password);
            infoObject.addProperty("nickname", nickname);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "login");
            commandObject.addProperty("command_name", "create_user");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            LoginMenuMessage message = LoginMenuMessage.valueOf(responseObject.get("message").getAsString());
            view.showRegisterMessage(message, username, nickname);
        } catch (Exception e) {
            view.showRegisterMessage(LoginMenuMessage.ERROR, username, nickname);
        }
    }


    public final void loginUser(String username, String password) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("username", username);
            infoObject.addProperty("password", password);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "login");
            commandObject.addProperty("command_name", "login_user");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            LoginMenuMessage message = LoginMenuMessage.valueOf(responseObject.get("message").getAsString());
            view.showLoginMessage(message);
            if (message == LoginMenuMessage.LOGGED_IN) {
                JsonObject responseInfoObject = responseObject.get("info").getAsJsonObject();
                String token = responseInfoObject.get("token").getAsString();
                MainController.setToken(token);
            }
        } catch (Exception e) {
            view.showLoginMessage(LoginMenuMessage.ERROR);
        }
    }
}
