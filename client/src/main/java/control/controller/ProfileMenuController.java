package control.controller;

import com.google.gson.JsonObject;
import control.message.ProfileMenuMessage;
import javafx.application.Platform;
import view.ProfileMenuView;

public class ProfileMenuController extends Controller {

    private ProfileMenuView view;


    public void setView(ProfileMenuView view) {
        this.view = view;
        super.view = view;
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
            MainController.sendMessage(commandObject.toString());
            startWaiting();
        } catch (Exception e) {
            view.showChangeNicknameMessage(ProfileMenuMessage.ERROR);
        }
    }

    private void checkChangeNicknameResponse(JsonObject infoObject) {
        try {
            if (waitTimeline == null) return;
            else stopWaiting();
            ProfileMenuMessage message = ProfileMenuMessage.valueOf(infoObject.get("message").getAsString());
            view.showChangeNicknameMessage(message);
        } catch (Exception e) {
            view.showChangeNicknameMessage(ProfileMenuMessage.ERROR);
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
            MainController.sendMessage(commandObject.toString());
            startWaiting();
        } catch (Exception e) {
            view.showChangePasswordMessage(ProfileMenuMessage.ERROR);
        }
    }

    private void checkChangePasswordResponse(JsonObject infoObject) {
        try {
            if (waitTimeline == null) return;
            else stopWaiting();
            ProfileMenuMessage message = ProfileMenuMessage.valueOf(infoObject.get("message").getAsString());
            view.showChangePasswordMessage(message);
        } catch (Exception e) {
            view.showChangePasswordMessage(ProfileMenuMessage.ERROR);
        }
    }


    @Override
    public void parseCommand(JsonObject command) {
        String commandName = command.get("command_name").getAsString();
        JsonObject infoObject = command.get("info").getAsJsonObject();
        switch (commandName) {
            case "change_nickname_response":
                Platform.runLater(() -> checkChangeNicknameResponse(infoObject));
                break;
            case "change_password_response":
                Platform.runLater(() -> checkChangePasswordResponse(infoObject));
                break;
        }
    }
}
