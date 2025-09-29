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

    /**
     * This creates a spike object. A spike is a tile that will deal damage to the player if they collide
     * Creates a hit box based of the x, y, width, and height values provided. the Image parameter must be a bufferedImage.
     * @param x the x coordinate of the spike
     * @param y the y coordinate of the spike
     * @param width the width of the spike, Recommended 32
     * @param height the height of the spike recommended 32
     * @param image the image of the spike.
     */
    public Spike(int x, int y,int width,int height,BufferedImage image){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        hitBox = new Rectangle(x,y,width,Math.abs(height));
    }

    /**
     * Draws the spike onto the JFrame through the GamePanel and its Graphics2D gtd
     * Draws the spike's image at the coordinates of the spike with the dimensions of the spike.
     * @param gtd the GamePanel graphics that will draw the spike.
     */
    public void draw(Graphics2D gtd){
        if(height < 0){
            gtd.drawImage(image, x, y -height, width, height, null);
        } else {
            gtd.drawImage(image, x, y, width, height, null);
        }
    }
}
