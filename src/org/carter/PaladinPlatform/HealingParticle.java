package org.carter.PaladinPlatform;

import java.awt.*;
import java.util.Random;

/**
 * The particles used in when the player is healing.
 */
public class HealingParticle {
    int x;
    int y;
    int size;
    int time;
    Random rand;

    /**
     * Constructs teh healing particle based on the values given in the arguments.
     * The values are meant to be randomly generated but I left it open for other uses.
     * @param x the x coordinate of the particle
     * @param y the y coordinate of the particle
     * @param size the size of the particle
     */
    public HealingParticle(int x, int y, int size){
        this.x = x;
        this.y = y;
        this.size = size;
        rand = new Random();
    }

    /**
     * Updates the particles position so that it floats up from where it is spawned
     * With a little random variance in the x direction.
     * Also increases its time counter which keeps track of how long the particle has been alive.
     * When it gets over a certain amount the particle will be removed.
     */
    public void update(){
        y -= 1;
        x += rand.nextInt(-2,3);
        time++;
    }

    /**
     * Draws the particle based on its size and its locations.
     * Randomly drawn as either a + or an X to give it a sparkly effect.
     * @param gtd The graphics the particle is being drawn on and through.
     */

    public void draw(Graphics2D gtd){
        gtd.setColor(Color.GREEN);
        gtd.setStroke(new BasicStroke(1));
        if(time%2 == 0) {
            gtd.drawLine(x, y, x+size, y+size);
            gtd.drawLine(x+size, y, x, y+size);
        } else{
            gtd.drawLine(x + size/2, y, x+size/2, y+size);
            gtd.drawLine(x, y + size/2, x + size, y+size/2);
        }
        gtd.setStroke(new BasicStroke(1));
    }

}
