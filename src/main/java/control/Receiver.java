package control;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import control.controller.DataController;
import control.controller.MainController;

import java.io.DataInputStream;

public class Receiver extends Thread {

    private final DataInputStream dataInputStream;
    private boolean end;

    {
        end = false;
    }


    public Receiver(DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }


    @Override
    public void run() {
        while (!end) {
            try {
                String input = dataInputStream.readUTF();
                System.out.println(input);
                JsonObject command = new JsonParser().parse(input).getAsJsonObject();
                if ("data".equals(command.get("command_type").getAsString()))
                    DataController.getInstance().parseCommand(command);
                else MainController.getActiveController().parseCommand(command);
            } catch (Exception ignored) {
            }
        }
    }

    public void finish() {
        this.end = true;
    }
}
