package view.animation;

import javafx.animation.Transition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class ThrowCoinAnimation extends Transition {

    private final ImageView imageView;

    public ThrowCoinAnimation(ImageView imageView, double duration) {
        this.imageView = imageView;
        this.setCycleDuration(Duration.millis(duration));
    }

    @Override
    protected void interpolate(double frac) {
        int frame = (int) Math.floor(frac * 19);
        if (frame > 18) frame = 18;

        int displacement = (9 - Math.abs(frame - 9)) * 30;
        imageView.getParent().setLayoutY(400 - displacement);
    }
}
