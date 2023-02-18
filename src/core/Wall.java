package core;

import javafx.scene.canvas.GraphicsContext;
import libs.Configs;
import libs.Sprite;
import javafx.scene.image.Image;

import java.util.Random;

public class Wall extends Sprite{

    public int x=0, y=0;

    public Wall() {
        super.imgPath = "/core/Wall.png";
        super.setImage(new Image(imgPath));
    }

}
