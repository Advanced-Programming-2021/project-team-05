package control.controller;

import com.google.gson.JsonObject;
import control.message.ChatRoomMessage;
import control.message.ShopMenuMessage;
import javafx.application.Platform;
import view.ChatRoomView;

public class ChatRoomController extends Controller {

    private ChatRoomView view;


    public void setView(ChatRoomView view) {
        this.view = view;
        super.view = view;
    }


    public void sendMessage(String message) {
        try {
            JsonObject infoObject = new JsonObject();
            infoObject.addProperty("token", MainController.getToken());
            infoObject.addProperty("message", message);
            JsonObject commandObject = new JsonObject();
            commandObject.addProperty("command_type", "chat");
            commandObject.addProperty("command_name", "send_message");
            commandObject.add("info", infoObject);
            MainController.sendMessage(commandObject.toString());
            startWaiting();
        } catch (Exception e) {
            view.showSendMessage(ChatRoomMessage.ERROR);
        }
    }

    private void checkSendMessageResponse(JsonObject infoObject) {
        try {
            if (waitTimeline == null) return;
            else stopWaiting();
            ChatRoomMessage message = ChatRoomMessage.valueOf(infoObject.get("message").getAsString());
            view.showSendMessage(message);
        } catch (Exception e) {
            view.showSendMessage(ChatRoomMessage.ERROR);
        }
    }


    @Override
    public void parseCommand(JsonObject command) {
        String commandName = command.get("command_name").getAsString();
        JsonObject infoObject = command.get("info").getAsJsonObject();
        switch (commandName) {
            case "send_message_response":
                Platform.runLater(() -> checkSendMessageResponse(infoObject));
                break;
            case "update_messages":
                Platform.runLater(() -> view.updateChatScene());
                break;
        }
    }
}
