package org.carter.PaladinPlatform;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Flag {

    int x;
    int y;
    int width;
    int height;
    BufferedImage image;


    Rectangle hitBox;

    public Flag(int x, int y, int width, int height, BufferedImage image){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;

        hitBox = new Rectangle(x,y,width,height);

    }

    public void setX(int x){
        this.x = x;
    }

    public void draw(Graphics2D gtd){
        gtd.drawImage(image,x,y,null);
    }

    public boolean check(Rectangle playerHitBox){
        return hitBox.intersects(playerHitBox);
    }
}
