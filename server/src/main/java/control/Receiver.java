package control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import control.controller.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@SuppressWarnings("SameReturnValue")
public class Receiver extends Thread {

    private final Socket socket;
    private final DataInputStream dataInputStream;
    private final DataOutputStream dataOutputStream;


    public Receiver(Socket socket, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        super();
        this.socket = socket;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }


    @Override
    public void run() {
        System.out.println("Client connected");
        try {
            while (true) {
                String input = dataInputStream.readUTF();
                System.out.println("Input received, input: " + input);
                boolean end = parseInput(input);
                if (end) break;
            }
            dataInputStream.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Client disconnected");
        } catch (Exception e) {
            System.out.println("An error Occurred");
        }
        Listener.getListener().removeReceiver(this);
    }


    private boolean parseInput(String input) {
        try {
            JsonObject commandObject = new JsonParser().parse(input).getAsJsonObject();
            String commandType = commandObject.get("command_type").getAsString();
            String commandName = commandObject.get("command_name").getAsString();
            JsonObject infoObject = commandObject.get("info").getAsJsonObject();
            switch (commandType) {
                case "data":
                    DataController.parseCommand(commandName, infoObject, this);
                    break;
                case "login":
                    LoginMenuController.parseCommand(commandName, infoObject, this);
                    break;
                case "main":
                    MainMenuController.parseCommand(commandName, infoObject, this);
                    break;
                case "profile":
                    ProfileMenuController.parseCommand(commandName, infoObject, this);
                    break;
                case "shop":
                    ShopMenuController.parseCommand(commandName, infoObject, this);
                    break;
                case "deck":
                    DeckMenuController.parseCommand(commandName, infoObject, this);
                    break;
                case "chat":
                    ChatRoomController.parseCommand(commandName, infoObject, this);
                    break;
                default:
                    throw new Exception("Invalid command type");
            }
            return false;
        } catch (Exception e) {
            JsonObject response = new JsonObject();
            response.addProperty("message", "ERROR");
            sendResponse(response.toString());
            return false;
        }
    }


    public void sendResponse(String response) {
        try {
            System.out.println("Response sent, response: " + response);
            dataOutputStream.writeUTF(response);
            dataOutputStream.flush();
        } catch (IOException e) {
            System.out.println("Failed to send response");
        }
    }
}
