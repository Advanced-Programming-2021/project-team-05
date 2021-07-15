package control.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import control.Sender;
import control.message.ProfileMenuMessage;
import view.MainView;
import view.ProfileMenuView;

public class ProfileMenuController {

    private ProfileMenuView view;


    public void setView(ProfileMenuView view) {
        this.view = view;
    }


    public final void changeNickname(String newNickname) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
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
            infoObject.addProperty("token", MainController.getToken());
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
