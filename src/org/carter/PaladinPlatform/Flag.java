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

    /**
     * Creates the flag object on to the GamePanel. Necessary for the player to progress
     * through levels.
     * @param x int x coordinate the flag is spawned at.
     * @param y int y coordinate teh flag is spawned at.
     * @param width int width of the flag.
     * @param height int height of the flag
     * @param image the image of the flag. BufferedImage
     */
    public Flag(int x, int y, int width, int height, BufferedImage image){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;

        hitBox = new Rectangle(x,y,width,height);

    }

    /**
     * Sets the x coordinate of the flag with a new x Coordinate.
     * @param x the new x coordinate
     */
    public void setX(int x){
        this.x = x;
    }

    /**
     * Draws the flag onto the JFrame. Draws it with its image.
     * @param gtd the Graphics2D used to draw on the frame.
     */
    public void draw(Graphics2D gtd){
        gtd.drawImage(image,x,y,null);
    }

    /**
     * This checks to see if the player has reached the flag.
     * If the player's hit box intersects the flag's hit box it will return true
     * else it will return false;
     * @param playerHitBox The player's hit box.
     * @return Boolean, Returns whether the player's hit box intersects the flag's hit box
     */
    public boolean check(Rectangle playerHitBox){
        return hitBox.intersects(playerHitBox);
    }
}
