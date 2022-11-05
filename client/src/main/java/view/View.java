package view;

import javafx.scene.Cursor;
import javafx.scene.Scene;

public abstract class View {

    protected Scene scene;


    public void startWaiting() {
        scene.setCursor(Cursor.WAIT);
    }

    public void stopWaiting() {
        scene.setCursor(Cursor.DEFAULT);
    }
}
