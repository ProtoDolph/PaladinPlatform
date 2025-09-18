package org.carter.PaladinPlatform;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Spike {

    int x;
    int y;
    int width;
    int height;
    BufferedImage image;
    Rectangle hitBox;

    public Spike(int x, int y,int width,int height,BufferedImage image){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        hitBox = new Rectangle(x,y,width,height);
    }

    public void draw(Graphics2D gtd){
        gtd.drawImage(image, x, y, width, height, null);
    }
}
