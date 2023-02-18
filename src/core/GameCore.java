package core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import libs.Configs;
import libs.CoreFunc;
import libs.GameText;
import java.util.Random;

import java.util.ArrayList;

public class GameCore implements CoreFunc {

    // Main Game variables should be here
    Crawler crawler = new Crawler();
    Background background = new Background();
    Wall wall = new Wall();
    ArrayList<Fruit> fruits = new ArrayList<>();

    int score = 0;
    int i=0;

    @Override
    public void init(GraphicsContext gc) {
        // initialize objects (initial position, initial size, etc)
        background.resize(Configs.appWidth, Configs.appHeight);

        wall.x = 200;
        wall.y = 200;
        wall.resize(400, 400);

        crawler.x = 600;
        crawler.y = 600;
        crawler.resize(1.5);

        //Polymorphism
        Fruit fruit[] = new Fruit[5];

        fruit[0]=new Fruit();
        fruit[1]=new Fruit();
        fruit[2]=new Fruit();
        fruit[3]=new Fruit();
        fruit[4]=new Fruit();

        //fruit no 1
        fruit[0].x=720;
        fruit[0].y=720;
        fruit[0].resize(0.2);
        fruits.add(fruit[0]);
        fruit[0].render(gc, 720, 720);

        //fruit no 2
        fruit[1].x=300;
        fruit[1].y=50;
        fruit[1].resize(0.2);
        fruits.add(fruit[1]);
        fruit[1].render(gc, 540, 50);

        //fruit no 3
        fruit[2].x=20;
        fruit[2].y=369;
        fruit[2].resize(0.2);
        fruits.add(fruit[2]);
        fruit[2].render(gc, 20, 369);

        //fruit no 4
        fruit[3].x=600;
        fruit[3].y=140;
        fruit[3].resize(0.2);
        fruits.add(fruit[3]);
        fruit[3].render(gc, 600, 1400);

        //fruit no 5
        fruit[4].x=270;
        fruit[4].y=600;
        fruit[4].resize(0.2);
        fruits.add(fruit[4]);
        fruit[4].render(gc, 270, 549);
    }

    @Override
    public void animate(GraphicsContext gc, int time, ArrayList input) {
        // any animations and keyboard controls should be here
        background.render(gc, 0, 0);

        //render the wall in the center of background
        wall.render(gc, wall.x, wall.y);

        //spawn the fruit
        for (int i = 0; i < fruits.size(); i++) {
            Fruit tmp = fruits.get(i);
            //if the sprite collide the fruit, the fruit is removed from the game map
            if (crawler.collide(tmp)) {
                fruits.remove(tmp);
                score++;
            }
            tmp.render(gc, tmp.x, tmp.y);
        }

        if (score==5){
            GameText gameText = new GameText(Color.YELLOW,Color.YELLOW);
            gameText.setText(gc,"YOU WIN" ,40,311,412);
        }

        if (input.contains("UP")) {
            if (crawler.y > 0 && !crawler.collide(wall)) {
                crawler.moveUp(gc);
            }
            //if sprite collide the wall and within the area of collision, the sprite still can move up to escape the collision
            else if(crawler.y<200 && crawler.y>0 && crawler.collide(wall)){
                crawler.moveUp(gc);
            }
        }

        if (input.contains("DOWN")) {
            if (crawler.y < 700 && !crawler.collide(wall)) {
                crawler.moveDown(gc);
            }
            //if sprite collide the wall and within the area of collision, the sprite still can move down to escape the collision
            else if(crawler.y>590 && crawler.y<700 && crawler.collide(wall)){
                crawler.moveDown(gc);
            }
        }

        if (input.contains("LEFT")) {
            if (crawler.x > 0 && !crawler.collide(wall)) {
                crawler.moveLeft(gc);
            }
            //if sprite collide the wall and within the area of collision, the sprite still can move left to escape the collision
            else if (crawler.x < 200 && crawler.x > 0 && crawler.collide(wall)) {
                crawler.moveLeft(gc);
            }
        }

        if (input.contains("RIGHT")) {
            if (crawler.x < 720 && !crawler.collide(wall)) {
                crawler.moveRight(gc);
            }
            //if sprite collide the wall and within the area of collision, the sprite still can move right to escape the collision
            else if(crawler.x<720 && crawler.x>590 && crawler.collide(wall)){
                crawler.moveRight(gc);
            }
        }

        crawler.render(gc, crawler.x, crawler.y);

    }


    @Override
    public void mouseClick(MouseEvent e) {
        // mouse click event here
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        // mouse move event here
    }

}