package control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Sender {

    private static Socket socket;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;


    public static boolean initialize() {
        try {
            socket = new Socket("localhost", 7355);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException e) {
            System.out.println("Failed to connect to server");
            return false;
        }
    }


    public static void send(String message) {
        try {
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
        } catch (IOException e) {
            System.out.println("Failed to send message to server");
            e.printStackTrace();
        }
    }

    public static String sendAndGetResponse(String message) {
        try {
            send(message);
            return dataInputStream.readUTF();
        } catch (IOException e) {
            System.out.println("Failed to get response from server");
            return null;
        }
    }
}
