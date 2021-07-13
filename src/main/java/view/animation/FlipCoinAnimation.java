package view.animation;

import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import utils.CoinSide;


public class FlipCoinAnimation extends Transition {

    private final ImageView imageView;
    private CoinSide coinSide;
    private boolean isCoinSideChanged;

    public FlipCoinAnimation(ImageView imageView) {
        this.imageView = imageView;
        this.coinSide = CoinSide.HEADS;
        this.isCoinSideChanged = false;
        this.setCycleDuration(Duration.millis(200));
    }

    @Override
    protected void interpolate(double frac) {
        int frame = (int) Math.floor(frac * 10) + 1;
        if (frame > 9) {
            frame = 0;
            if (!isCoinSideChanged) {
                coinSide = coinSide == CoinSide.HEADS ? CoinSide.TAILS : CoinSide.HEADS;
                isCoinSideChanged = true;
            }
        }
        if (frame == 1) {
            isCoinSideChanged = false;
        }

        String imagePath = "/images/coins/" + coinSide.getName() + "_" + frame + ".png";
        Image image = new Image(getClass().getResource(imagePath).toExternalForm());
        imageView.setImage(image);
        centerImage();
    }


    public void centerImage() {
        Image img = imageView.getImage();
        if (img != null) {
            double width;
            double height;

            double ratioX = imageView.getFitWidth() / img.getWidth();
            double ratioY = imageView.getFitHeight() / img.getHeight();

            double reduceValue;
            reduceValue = Math.min(ratioX, ratioY);

            width = img.getWidth() * reduceValue;
            height = img.getHeight() * reduceValue;

            imageView.setX((imageView.getFitWidth() - width) / 2);
            imageView.setY((imageView.getFitHeight() - height) / 2);
        }
    }
}
