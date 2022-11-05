package control.controller;

import com.google.gson.JsonObject;
import control.DataManager;
import control.Listener;
import control.Receiver;
import control.message.ChatRoomMessage;
import model.Message;
import model.User;

public class ChatRoomController {


    public static void parseCommand(String commandName, JsonObject infoObject, Receiver receiver) {
        JsonObject responseObject;
        switch (commandName) {
            case "send_message":
                responseObject = sendMessage(infoObject);
                break;
            case "delete_message":
                responseObject = deleteMessage(infoObject);
                break;
            default:
                responseObject = new JsonObject();
                responseObject.addProperty("message", String.valueOf(ChatRoomMessage.ERROR));
        }
        receiver.sendResponse(responseObject.toString());
    }


    public static JsonObject sendMessage(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "chat");
        responseObject.addProperty("command_name", "send_message_response");
        responseObject.add("info", responseInfoObject);

        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            responseInfoObject.addProperty("message", String.valueOf(ChatRoomMessage.ERROR));
            return responseObject;
        }
        String content = infoObject.get("message").getAsString();
        if (content.length() == 0) {
            responseInfoObject.addProperty("message", String.valueOf(ChatRoomMessage.FAILED));
            return responseObject;
        }
        Message message = new Message(user, content);
        dataManager.addMessage(message);
        responseInfoObject.addProperty("message", String.valueOf(ChatRoomMessage.SUCCESS));
        return responseObject;
    }


    public static JsonObject deleteMessage(JsonObject infoObject) {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "chat");
        responseObject.addProperty("command_name", "delete_message_response");
        responseObject.add("info", responseInfoObject);

        String token = infoObject.get("token").getAsString();
        DataManager dataManager = DataManager.getInstance();
        User user = dataManager.getUserByToken(token);
        if (user == null) {
            responseInfoObject.addProperty("message", String.valueOf(ChatRoomMessage.ERROR));
            return responseObject;
        }
        String messageId = infoObject.get("message_id").getAsString();
        Message message = dataManager.getMessageById(messageId);
        if (message == null || !user.getNickname().equals(message.getNickname())) {
            responseInfoObject.addProperty("message", String.valueOf(ChatRoomMessage.FAILED));
            return responseObject;
        }
        dataManager.removeMessage(message);
        responseInfoObject.addProperty("message", String.valueOf(ChatRoomMessage.SUCCESS));
        return responseObject;
    }


    public static void updateMessages() {
        JsonObject responseInfoObject = new JsonObject();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("command_type", "chat");
        responseObject.addProperty("command_name", "update_messages");
        responseObject.add("info", responseInfoObject);
        Listener.getListener().sendToAll(responseObject.toString());
    }
}
