package org.carter.PaladinPlatform;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Wall {

    int x;
    int y;
    int width;
    int height;

    BufferedImage image;
    Rectangle hitBox;

    /**
     * This is the wall class. It creates all the tiles and walls in teh game
     * for the player and other entities to stand on and not fall of the map.
     * All walls are stored in the GamePanel and do not update unless they are
     * deleted and remade. Creates a hitBox rectangle using the x, y, width, and height.
     * @param x the x coordinate of the tile
     * @param y the y coordinate of the tile
     * @param width the width of the tile. Recommended 32
     * @param height the height of the tile. Recommended 32
     * @param image the image of the tile. Customizable but needs to be a BufferedImage
     */
    public Wall(int x, int y, int width, int height, BufferedImage image){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        hitBox = new Rectangle(x,y,width,height);

    }

    /**
     * Sets the x coordinate of the tile with a new x coordinate
     * @param x the new x coordinate
     */
    public void setX(int x){
        this.x = x;
    }

    /**
     * Sets the y coordinate of teh tile with a new y coordinate
     * @param y the new y coordinate
     */
    public void setY(int y){
        this.y = y;
    }

    /**
     * Draws the tile onto the JFrame using Graphics2D gtd supplied through the GamePanel
     * @param gtd the GamePanel Graphics2D to draw the tile on.
     */
    public void draw(Graphics2D gtd){
        gtd.drawImage(image,x,y,null);
        gtd.setColor(Color.BLACK);
        gtd.draw(hitBox);

    }

}
