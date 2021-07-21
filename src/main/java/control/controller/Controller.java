package control.controller;

import com.google.gson.JsonObject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import utils.ViewUtility;
import view.View;

public abstract class Controller {

    protected View view;
    protected Timeline waitTimeline;


    public Controller() {
        MainController.setActiveController(this);
    }


    @SuppressWarnings("unused")
    public void setView(View view) {
        this.view = view;
    }


    public abstract void parseCommand(JsonObject command);


    protected void startWaiting() {
        if (waitTimeline != null) waitTimeline.stop();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(5), e -> {
            waitTimeline = null;
            view.stopWaiting();
            ViewUtility.showInformationAlert("Error", "Network Error", "Failed to get response from server");
        });
        waitTimeline = new Timeline(keyFrame);
        waitTimeline.setCycleCount(1);
        waitTimeline.play();
        view.startWaiting();
    }

    protected void stopWaiting() {
        waitTimeline.stop();
        waitTimeline = null;
        view.stopWaiting();
    }

    public boolean isWaiting() {
        return waitTimeline != null;
    }
}
