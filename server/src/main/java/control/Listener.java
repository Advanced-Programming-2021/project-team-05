package control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Listener {

    private static Listener listener;
    private final ArrayList<Receiver> receivers;
    private ServerSocket serverSocket;

    {
        receivers = new ArrayList<>();
    }


    public Listener(int port) {
        listener = this;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Failed to create server socket");
        }
    }


    public static Listener getListener() {
        return listener;
    }


    public void addReceiver(Receiver receiver) {
        synchronized (receivers) {
            receivers.add(receiver);
        }
    }

    public void removeReceiver(Receiver receiver) {
        synchronized (receivers) {
            receivers.remove(receiver);
        }
    }


    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                Receiver receiver = new Receiver(socket, dataInputStream, dataOutputStream);
                addReceiver(receiver);
                receiver.start();
            } catch (IOException e) {
                System.out.println("Failed to connect to client");
            }
        }
    }


    public void sendToAll(String message) {
        synchronized (receivers) {
            for (Receiver receiver : receivers) {
                receiver.sendResponse(message);
            }
        }
    }
}
