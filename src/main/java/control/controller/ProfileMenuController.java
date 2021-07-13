package control.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import control.Sender;
import control.message.ProfileMenuMessage;
import model.User;
import view.MainView;
import view.ProfileMenuView;


public class ProfileMenuController {

    private final String token;
    private ProfileMenuView view;


    public ProfileMenuController(String token) {
        this.token = token;
    }


    public void setView(ProfileMenuView view) {
        this.view = view;
    }


    public User getUser() {
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


    public String getToken() {
        return this.token;
    }


    public final void changeNickname(String newNickname) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", token);
            infoObject.addProperty("new_nickname", newNickname);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "profile");
            commandObject.addProperty("command_name", "change_nickname");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            ProfileMenuMessage message = ProfileMenuMessage.valueOf(responseObject.get("message").getAsString());
            view.showChangeNicknameMessage(message, newNickname);
        } catch (Exception e) {
            view.showChangeNicknameMessage(ProfileMenuMessage.ERROR, newNickname);
        }
    }


    public final void changePassword(String currentPassword, String newPassword) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", token);
            infoObject.addProperty("current_password", currentPassword);
            infoObject.addProperty("new_password", newPassword);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "profile");
            commandObject.addProperty("command_name", "change_password");
            commandObject.add("info", infoObject);

            String response = Sender.sendAndGetResponse(commandObject.toString());
            if (response == null) {
                MainView.showNetworkError();
                return;
            }
            JsonObject responseObject = new JsonParser().parse(response).getAsJsonObject();
            ProfileMenuMessage message = ProfileMenuMessage.valueOf(responseObject.get("message").getAsString());
            view.showChangePasswordMessage(message);
        } catch (Exception e) {
            view.showChangePasswordMessage(ProfileMenuMessage.ERROR);
        }
    }
}
