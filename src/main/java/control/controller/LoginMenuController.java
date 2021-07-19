package control.controller;

import com.google.gson.JsonObject;
import control.message.LoginMenuMessage;
import javafx.application.Platform;
import view.LoginMenuView;

    public class LoginMenuController extends Controller {

        private LoginMenuView view;


        public void setView(LoginMenuView view) {
            this.view = view;
            super.view = view;
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
            MainController.sendMessage(commandObject.toString());
            startWaiting();
        } catch (Exception e) {
            view.showRegisterMessage(LoginMenuMessage.ERROR);
        }
    }

    private void checkCreateUserResponse(JsonObject infoObject) {
        try {
            if (waitTimeline == null) return;
            else stopWaiting();
            LoginMenuMessage message = LoginMenuMessage.valueOf(infoObject.get("message").getAsString());
            view.showRegisterMessage(message);
        } catch (Exception e) {
            view.showRegisterMessage(LoginMenuMessage.ERROR);
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
            MainController.sendMessage(commandObject.toString());
            startWaiting();
        } catch (Exception e) {
            view.showLoginMessage(LoginMenuMessage.ERROR);
        }
    }

    private void checkLoginUserResponse(JsonObject infoObject) {
        try {
            if (waitTimeline == null) return;
            else stopWaiting();
            LoginMenuMessage message = LoginMenuMessage.valueOf(infoObject.get("message").getAsString());
            if (message == LoginMenuMessage.LOGGED_IN) {
                String token = infoObject.get("token").getAsString();
                MainController.setToken(token);
            }
            view.showLoginMessage(message);
        } catch (Exception e) {
            view.showLoginMessage(LoginMenuMessage.ERROR);
        }
    }


    @Override
    public void parseCommand(JsonObject command) {
        String commandName = command.get("command_name").getAsString();
        JsonObject infoObject = command.get("info").getAsJsonObject();
        switch (commandName) {
            case "create_user_response":
                Platform.runLater(() -> checkCreateUserResponse(infoObject));
                break;
            case "login_user_response":
                Platform.runLater(() -> checkLoginUserResponse(infoObject));
                break;
        }
    }
}
