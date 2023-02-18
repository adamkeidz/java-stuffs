package core;

import javafx.scene.canvas.GraphicsContext;
import libs.Configs;
import libs.Sprite;
import javafx.scene.image.Image;

import java.util.Random;

public class Fruit extends Sprite {


    public int x, y;

    public Fruit() {
        super.imgPath = "/core/Fruit.png";
        super.setImage(new Image(imgPath));
    }


}