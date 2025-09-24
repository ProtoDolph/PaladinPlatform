package org.carter.PaladinPlatform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Slime {
    public GamePanel panel;
    public int x;
    public int y;
    public double maxSpeed;
    public double fallSpeed;
    public double jumpSpeed;
    public double xSpeed;
    public double ySpeed;
    public int width;
    public int height;
    public boolean faceRight;
    public boolean launch;
    public boolean alive;
    public boolean dying;
    public Rectangle hitBox;
    public BufferedImage currFrame;
    public int frameNum;
    public BufferedImage[] slime = new BufferedImage[7];
    public BufferedImage[] death = new BufferedImage[12];

    /**
     * This creates a Slime into the game. Must be created in the GamePanel
     * and stored in the GamePanel. Spawns it in at the x and y coordinates in the arguments with the
     * GamePanel the object is spawned stored in panel as a reference
     * Then is proceeds to set all the values of the slime. Its speed, width, height, hitBox
     * alive, faceRight, its images, the current frame.
     * @param x
     * @param y
     * @param panel
     */
    public Slime(int x, int y, GamePanel panel){
        this.panel = panel;
        this.x = x;
        this.y = y;
        this.width = 32;
        this.height = 32;
        alive = true;
        dying = false;
        launch = false;
        maxSpeed = 4;
        jumpSpeed = -5;
        fallSpeed = 0.3;

        hitBox = new Rectangle(x,y,width,height);
        faceRight = true;

        loadImages();
        frameNum = 0;
        currFrame = slime[0];
    }

    /**
     * This is the slimes update methods.
     * Everytime this method is called the slime will update its position and speed
     * check collision and modify speed and position for smoothness
     * checks to see if the slime is alive
     * Checks to see if the slime should jump
     * Updates hitBox position.
     */
    public void update(){
        if (faceRight) {
            xSpeed += 1;
        } else {
            xSpeed -= 1;
        }

        if(!launch) {
            if (xSpeed > maxSpeed) {
                xSpeed = maxSpeed;
            }
            if (xSpeed < -maxSpeed) {
                xSpeed = -maxSpeed;
            }
        }


        ySpeed += fallSpeed;
        checkJump();
        checkCollisionY();
        checkCollisionX();

        if(x >= panel.screenWidth - width){
            faceRight = false;
            xSpeed = -1;
        } else if(x <= 0){
            faceRight = true;
            xSpeed = 1;
        }
        if(dying){
            xSpeed = 0;
        }

        x += xSpeed;
        y += ySpeed;
        hitBox.x = x;
        hitBox.y = y;
    }

    /**
     * Draws the slime onto the Frame using the Graphics2D gtd supplied in the arguments
     * Will modify the size of the image to make sure it fits in the slimes hitBox when drawn.
     * Also, will flip the image around so that the slime faces either left or right.
     * @param gtd the Graphics2D that will draw the slime's current Frame.
     */
    public void draw(Graphics2D gtd){
        //gtd.setColor(Color.RED);
        //gtd.drawRect(x, y, width, height);
        if (faceRight) {
            gtd.drawImage(currFrame, x, y - (currFrame.getHeight() - height), width, currFrame.getHeight(), null);
            //gtd.drawRect(x + width, y + height, width ,height);
        } else {
            gtd.drawImage(currFrame, x + width, y - (currFrame.getHeight() - height), -width, currFrame.getHeight(), null);
            //gtd.drawRect(x-width,y+height, width ,height);
        }
    }

    /**
     * This checks to see if the slime is still alive.
     * It checks to see if the player is attacking.
     * Then it creates the player's attacking hitBox
     * then it checks if that hit box intersects its own
     * if it does then the slime will start dying and doing
     * its death animation as it is no longer alive
     * @param player The player object on the GamePanel as the Slime.
     */
    public void checkAlive(Player player){
        if(player.attacking){
            Rectangle p1HitBox;
            if(player.facingLeft){
                p1HitBox = new Rectangle(player.x - player.width - player.width/2, player.y, 2*player.width, player.height);
            }else {
                p1HitBox = new Rectangle(player.x + player.width/2, player.y, player.width * 2, player.height);
            }
            if(p1HitBox.intersects(hitBox)){
                alive = false;
                dying = true;
                frameNum = 0;
            }
        }
    }

    /**
     * This gets the next frame in teh animation sequence for the slime.
     * First checks to see if teh slime is dying and needs to play its death animation.
     * If not it plays its normal movement animation.
     * Then it checks to make sure frameNum our list index counter does not exceed the
     * length of the animation list we want.
     * Then it sets the current frame to the image stored in the frameNum index of teh animation list we want
     * then it increases frameNum by 1 for the next image in the animation sequence.
     */
    public void nextFrame(){
        if(dying){
            if(frameNum >= death.length){
                frameNum = 0;
                dying = false;
                currFrame = death[-1];
            }else {
                currFrame = death[frameNum];
            }
            frameNum++;

        }else if(alive) {
            if (frameNum >= slime.length) {
                frameNum = 0;
            }
            currFrame = slime[frameNum];
            frameNum++;
        }
    }

    /**
     * This loads all of teh slime's animation and images
     * Note - all sub image values of x,y ,width, and height are hardcoded to ensure
     * the images remain in the hit box of the slime and remain centered.
     * All files must be present in the Resource Directory with the same file name
     * Surrounded in a try catch to pinpoint errors and exceptions and also to stop the game if
     * an error occurs.
     */
    public void loadImages(){
        try {
            BufferedImage ogSlime = ImageIO.read(getClass().getResourceAsStream("/slimeSprite/Green_Slime/Run.png"));
            slime[0] = ogSlime.getSubimage(28,128-height,width + 24,height);
            slime[1] = ogSlime.getSubimage(156,128-height,width + 24,height);
            slime[2] = ogSlime.getSubimage(282,128 -height,width + 30,height);
            slime[3] = ogSlime.getSubimage(410, 128-height,width +30,height);
            slime[4] = ogSlime.getSubimage(540,128-height,width+30,height);
            slime[5] = ogSlime.getSubimage(670,128-height,width+22, height);
            slime[6] = ogSlime.getSubimage(800,128-(height+2),width+20,height+2);

            BufferedImage ogDeath = ImageIO.read(getClass().getResourceAsStream("/slimeSprite/Green_Slime/Dead.png"));
            death[0] = ogDeath.getSubimage(38,128-height,width+16,height);
            death[1] = death[0];
            death[2] = death[0];
            death[3] = death[0];
            death[4] = ogDeath.getSubimage(164,128-height,width+18,height);
            death[5] = death[4];
            death[6] = death[4];
            death[7] = death[4];
            death[8] = ogDeath.getSubimage(290, 128-height, width +24, height);
            death[9] = death[8];
            death[10] = death[8];
            death[11] = death[8];

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This will check to see if teh slime should jump.
     * It does this by checking the tile diagonally in front of it.
     * So one forward and one down. IF that tile is empty the slime will jump.
     * Checks if it is empty by creating a temporary rectangle hit box and using the
     * Rectangle intersects method with teh rest of the tiles in the GamePanel to see if
     * any of them intersect. if none do the slime will jump.
     */

    public void checkJump(){
        if(alive) {
            int tempx;
            if (faceRight) {
                tempx = x + width;
            } else {
                tempx = x - width;
            }
            int tempy = y + height;
            Rectangle tempHitbox = new Rectangle(tempx, tempy, width, height);
            boolean pathGood = false;
            for (Wall wall : panel.walls) {
                if (wall.hitBox.intersects(tempHitbox)) {
                    pathGood = true;
                }
            }
            for (Wall wall1 : panel.walls) {
                hitBox.y++;
                if (wall1.hitBox.intersects(hitBox) && !pathGood) {
                    ySpeed = jumpSpeed;
                }
                hitBox.y--;
            }
        }
    }
    /**
     * Checks the collision of the slime in the Y direction
     * Does this by temporarily making the slime's hit box be where the bat will go
     * based off its ySpeed. Will prevent the slime from falling or flying up through tiles.
     * must be called after the ySpeed value has been determined, but before it is added to the y coordinate of
     * the slime.
     */
    public void checkCollisionY(){
        //bottom collision
        hitBox.y += ySpeed;
        for(Wall wall : panel.walls){
            if(hitBox.intersects(wall.hitBox)){
                hitBox.y -= ySpeed;
                while(!wall.hitBox.intersects(hitBox)){hitBox.y += Math.signum(ySpeed);}
                hitBox.y -= Math.signum(ySpeed);
                ySpeed = 0;
                launch = false;
                y = hitBox.y;
            }
        }
    }
    /**
     * Checks the collision of the slime in the X direction
     * Does this by temporarily making the slime's hit box be where the bat will go
     * based off its xSpeed. Will prevent the slime from moving through tiles.
     * must be called after the xSpeed value has been determined, but before it is added to the x coordinate of
     * the slime.
     */
    public void checkCollisionX(){
        hitBox.x += xSpeed;
        for(Wall wall : panel.walls){
            if(hitBox.intersects(wall.hitBox)){
                hitBox.x -= xSpeed;
                while(!wall.hitBox.intersects(hitBox)){hitBox.x += Math.signum(xSpeed);}
                hitBox.x -= Math.signum(xSpeed);
                xSpeed = 0;
                x = hitBox.x;
                faceRight = !faceRight;
            }
        }
    }

    /**
     * This sets the slimes faceRight boolean. The faceRight boolean
     * controls the direction the slime is facing.
     * @param faceRight the new value for this.faceRight
     */
    public void setFaceRight(boolean faceRight){
        this.faceRight = faceRight;
    }

    /**
     * this sets the slimes xSpeed value with a new xSpeed value dx.
     * Used for when the boss summons slimes.
     * @param dx the new xSpeed value
     */
    public void setXSpeed(int dx){
        xSpeed = dx;
    }


    /**
     * This sets the slime's ySpeed value with a new ySpeed value dy.
     * Used for when the boss summons slimes
     * @param dy the new ySpeed value
     */
    public void setYSpeed(double dy){
        ySpeed = dy;
    }

    /**
     * The launch boolean overrides teh max speed a slime can go.
     * used in teh boss fight when it summons and launches slimes at teh player.
     * This sets the this. Launch boolean to equal the launch boolean provided in the parameters.
     * @param launch the new boolean value for this. Launch
     */
    public void setLaunch(boolean launch){
        this.launch = launch;
    }
}
