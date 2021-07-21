package control;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Sender {

    private final Socket socket;
    private final DataOutputStream dataOutputStream;


    public Sender(Socket socket, DataOutputStream dataOutputStream) {
        this.socket = socket;
        this.dataOutputStream = dataOutputStream;
    }


    public void send(String message) {
        try {
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
        } catch (IOException e) {
            System.out.println("Failed to send message to server");
        }
    }

    public void finish() {
        try {
            dataOutputStream.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Failed to close socket");
        }
    }
}
