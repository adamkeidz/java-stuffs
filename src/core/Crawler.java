package core;

import javafx.scene.canvas.GraphicsContext;
import libs.Configs;
import libs.Sprite;
import javafx.scene.image.Image;

import java.awt.*;
import java.util.Random;

public class Crawler extends Sprite {

    public int x, y;

    public Crawler()  {
        super.imgPath = "/core/rico.gif";
        super.setImage(new Image(imgPath));
    }

    public void moveUp(GraphicsContext gc){
        this.y = y - 4;
    }

    public void moveDown(GraphicsContext gc){
        this.y = y + 4;
    }

    public void moveLeft(GraphicsContext gc){
        this.x = x - 4;
    }

    public void moveRight(GraphicsContext gc) {
        this.x = x + 4;
    }
}
