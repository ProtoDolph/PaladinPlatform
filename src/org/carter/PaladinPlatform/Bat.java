package org.carter.PaladinPlatform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Bat {
    public GamePanel panel;
    public int x;
    public int y;
    public int width;
    public int height;
    public double xSpeed;
    public double maxSpeed;
    public double ySpeed;
    public boolean sight;
    public boolean dying;
    public boolean alive;
    public Rectangle hitBox;
    public boolean faceRight;
    public BufferedImage currFrame;
    public int frameNum;
    public BufferedImage[] idle = new BufferedImage[4];
    public BufferedImage[] death = new BufferedImage[10];
    public BufferedImage[] attack = new BufferedImage[9];
    public boolean attacking;
    public boolean flyUP;
    public int count;
    public int sightCount;
    public int playerX;
    public int playerY;
    public int blindCount;
    public int attackCount;
    public Line2D line1;
    public Line2D line2;
    public Line2D line3;
    public Line2D line4;

    /**
     * This is the Bat entity in the platform. This is a flying entity
     * that uses sight lines to control is behavior. If the bat is able to see the player it will
     * start attacking it. Otherwise, it will fly back and forth left to right.
     * @param x The x coordinate the bat spawns at
     * @param y the Y coordinate that bat spawns at
     * @param panel the GamePanel the bat is a part of. used as reference to find other entities.
     */

    public Bat(int x, int y, GamePanel panel){
        this.x = x;
        this.y = y;
        this.panel = panel;
        width = 64;
        height = 32;
        faceRight = true;
        sight = false;
        hitBox = new Rectangle(x,y,width, height);
        maxSpeed = 5;
        frameNum = 0;
        alive = true;
        dying = false;
        flyUP = false;
        loadImages();
        currFrame = idle[0];
        count = 0;
        attacking = false;
    }

    /**
     * This is the update method for the bat.
     * Everytime this method is called the Bat;s position will be updated,
     * its speed will be updated, its collision will be checked. Its behavior is updated
     * It will check to see if it can see the player and see if its attacking
     * and how long it has been attacking. It will also to check to see if the bat is still alive.
     */

    public void update(){
        checkAlive(panel.player);
        checkSight();
        if(sight){
            sightCount++;
        } else {
            sightCount = 0;
        }
        if(sightCount >= 15){
            attacking = true;
            sightCount = 0;
            playerX = panel.player.x + panel.player.width/2;
            playerY = panel.player.y;
        }
        if(attacking && !sight){
            blindCount++;
        }
        if(attacking && sight){
            blindCount = 0;
            attackCount++;
        }
        if(attacking && blindCount >= 20){
            attacking = false;
            blindCount = 0;
        }
        if(!alive){
            attacking = false;
        }
        if(!attacking){
            attackCount = 0;
        }
        if(attacking){
            if(attackCount >= 120) {
                if(attackCount == 120){
                    frameNum = 0;
                }
                if (x > playerX) {
                    faceRight = false;
                } else {
                    faceRight = true;
                }
                double tempDY = (playerY - y) / 2;
                double tempDX = (playerX - (x + (width / 2))) / 2;
                if (tempDY < 1 && tempDY > -1) {
                    if (count > 12) {
                        flyUP = !flyUP;
                        ySpeed = 0.02;
                        count = 0;
                    }
                    if (flyUP) {
                        ySpeed -= 0.2;
                    } else {
                        ySpeed += 0.2;
                    }
                } else {
                    ySpeed += tempDY;
                }
                if (tempDX < 1 && tempDX > -1) {
                    if (tempDX > 0) {
                        xSpeed += 1;
                    } else {
                        xSpeed -= 1;
                    }
                } else {
                    xSpeed += tempDX;
                }
            } else {
                if (x > playerX) {
                    faceRight = false;
                } else {
                    faceRight = true;
                }
                double tempDY = (playerY - y) / 2;
                double tempDX = (playerX - (x + (width / 2))) / 2;
                if(tempDX > 0){
                    tempDX = -1;
                } else if (tempDX <= 0){
                    tempDX = 1;
                }
                tempDY = -.1;
                if(ySpeed < -1){
                    ySpeed = -1;
                }
                xSpeed = tempDX;
                ySpeed += tempDY;
            }
            if(panel.player.hit){
                attackCount = 0;
            }
            if(attackCount >= 300){
                attackCount = 0;
            }
            attackCount++;


        } else {
            if (faceRight) {
                xSpeed += 1;
            } else {
                xSpeed -= 1;
            }
            if (count > 12) {
                flyUP = !flyUP;
                ySpeed = 0.02;
                count = 0;
            }
            if (flyUP) {
                ySpeed -= 0.2;
            } else {
                ySpeed += 0.2;
            }
        }
        if (ySpeed >= maxSpeed) {
            ySpeed = maxSpeed;
        } else if (ySpeed <= -maxSpeed) {
            ySpeed = -maxSpeed;
        }
        if (xSpeed >= maxSpeed) {
            xSpeed = maxSpeed;
        } else if (xSpeed <= -maxSpeed) {
            xSpeed = -maxSpeed;
        }

        checkCollisionY();
        checkCollisionX();

        if(x >= panel.screenWidth - width){
            faceRight = false;
            xSpeed = -1;
        } else if(x <= 0){
            faceRight = true;
            xSpeed = 1;
        }
        if(y<=0) {
            ySpeed = 1;
        }
        if(dying){
            xSpeed = 0;
            ySpeed = 2;
        }

        int dx = (int) Math.round(xSpeed);
        int dy = (int) Math.round(ySpeed);
        x += dx;
        y += dy;
        hitBox.x = x;
        hitBox.y = y;
        count++;
    }

    /**
     * This draws the bat onto the JFrame using the Graphics2D gtd that is provided.
     * It will draw the bat based off the image stored in the currFrame variable.
     * the current frame is then drawn based on what direction the bat is facing
     * and the size of the bat and the size of the image to avoid distortion while
     * remains the size of the bat/
     * @param gtd the graphics/ frame that we are drawing the abt on.
     */
    public void draw(Graphics2D gtd){
        /*
        gtd.setColor(Color.RED);
        gtd.fillRect(x, y, width, height);
        gtd.draw(line1);
        gtd.draw(line2);
        gtd.draw(line3);
        gtd.draw(line4);
         */


        if (faceRight) {
            gtd.drawImage(currFrame, x  + width + (currFrame.getWidth() - width)/2, y - (currFrame.getHeight()-height)/2, -currFrame.getWidth(), (currFrame.getHeight() - height)/2 + height, null);
        } else {
            gtd.drawImage(currFrame, x - (currFrame.getWidth()-width)/2, y - (currFrame.getHeight()-height)/2, currFrame.getWidth(), (currFrame.getHeight() - height)/2 + height, null);
        }
    }

    /**
     * This checks to see if the bat can see the player.
     * It uses 4 line 2d from each of the 4 corners of the bats head and the 4 corners of the player.
     * bat top left ot player top left, bat bottom right to player bottom right etc…
     * Then it checks if these lines intersect any tiles.
     * If 3 of the lines do not intersect any tiles on the map then the bat sees the player
     * and the sight boolean is set to true otherwise it is set to false;
     */
    public void checkSight(){
        Player player = panel.player;
        line1 = new Line2D.Double(x + width/4,y+1,player.x+1, player.y);
        line2 = new Line2D.Double(x + width/4, y+height, player.x+1, player.y +player.height-1);
        line3 = new Line2D.Double(x +width - width/4 , y+1, player.x + player.width -1, player.y);
        line4 = new Line2D.Double(x+ width - width/4, y+height,player.x+player.width-1, player.y + player.height-1);
        boolean sight1 = true;
        boolean sight2 = true;
        boolean sight3 = true;
        boolean sight4 = true;
        for(Wall wall : panel.walls){
            if(line1.intersects(wall.hitBox)){
                sight1 = false;
            }
            if(line2.intersects(wall.hitBox)){
                sight2 = false;
            }
            if(line3.intersects(wall.hitBox)){
                sight3 = false;
            }
            if(line4.intersects(wall.hitBox)){
                sight4 = false;
            }
        }
        int sc = 0;
        if(sight1){
            sc++;
        }
        if(sight2){
            sc++;
        }
        if(sight3){
            sc++;
        }
        if(sight4){
            sc++;
        }
        if(sc >= 3){
            sight = true;
        } else {
            sight = false;
        }
    }

    /**
     * This gets the next frame to be drawn on teh frame for the bat.
     * Using currFrame as the variable that stores the current frame.
     * and frameNum to keep track of the index number in the BufferedImage[] list.
     * It first checks to make sure frameNum is a valid index of said image list.
     * If it isn't frameNum is set to zero and certain animation action booleans are set to false.
     * Otherwise, it will set currFrame to equal the animation[frameNum] depending on the action the bat is doing.
     * then it increases frameNum by 1.
     */
    public void nextFrame(){
        if(dying){
            if(frameNum >= death.length){
                dying = false;
                frameNum = 0;
                currFrame = death[death.length - 1];
            } else {
                currFrame = death[frameNum];
            }

        } else if(attacking && attackCount >= 120){
            if(frameNum >= attack.length){
                frameNum = 0;
            }
            currFrame = attack[frameNum];
        }else {
            if (frameNum >= idle.length) {
                frameNum = 0;
            }
            currFrame = idle[frameNum];
        }
        frameNum++;
    }

    /**
     * This checks to see if the bat is still alive.
     * Basically it takes the player supplied in teh arguments.
     * Player must be part of the same GamePanel as the bat.
     * Will check if the player is attacking and if its attacking hit box will hit the bat
     * if it does the bat dies and starts its death animation. Otherwise, it will keep on living.
     * @param player the player that is on the same GamePanel as the bat.
     */
    public void checkAlive(Player player){
        if(player.attacking){
            Rectangle p1HitBox;
            if(player.facingLeft){
                p1HitBox = new Rectangle(player.x - player.width - player.width/2, player.y-1, 2*player.width, player.height+2);
            }else {
                p1HitBox = new Rectangle(player.x + player.width/2, player.y-1, player.width * 2, player.height+2);
            }
            if(p1HitBox.intersects(hitBox)){
                alive = false;
                dying = true;
                frameNum = 0;
                attacking = false;
            }
        }
    }

    /**
     * This loads all the images for all the animations the bat has.
     * The bat has an idle/flying animation, an attack animation, and a death animation.
     * This method must be called in the constructor or before any draw() or nextFrame() methods are called.
     * All frames are sub-images of an image that must be in teh Resources directory with teh correct name.
     * All sub-images are hard coded with the values needed to make each frame have minimal distortion and
     * correct placement based off the bat's coordinates and size.
     */
    public void loadImages(){
        try{
            int iWidth = 64;
            int iHeight = 32;
            BufferedImage ogImage = ImageIO.read(getClass().getResourceAsStream("/batSpriteSheet.png"));
            idle[0] = ogImage.getSubimage(214,334,iWidth+2,iHeight+8);
            idle[1] = ogImage.getSubimage(308,338,iWidth+12,iHeight+6);
            idle[2] = ogImage.getSubimage(408,346,iWidth+6,iHeight+8);
            idle[3] = ogImage.getSubimage(502, 342, iWidth+12, iHeight+2);

            death[0] = ogImage.getSubimage(214, 138, iWidth +2, iHeight+8);
            death[1] = death[0];
            death[2] = ogImage.getSubimage(308, 128, iWidth, iHeight+20);
            death[3] = death[2];
            death[4] = ogImage.getSubimage(408, 132, iWidth, iHeight+18);
            death[5] = death[4];
            death[6] = ogImage.getSubimage(508, 148, iWidth, iHeight);
            death[7] = death[6];
            death[8] = death[6];
            death[9] = death[6];

            attack[0] = ogImage.getSubimage(214,40,iWidth+2, iHeight+8);
            attack[1] = ogImage.getSubimage(314,55,iWidth,iHeight);
            attack[2] = ogImage.getSubimage(410,58,iWidth+4,iHeight);
            attack[3] = ogImage.getSubimage(506,55,iWidth+8,iHeight);
            attack[4] = attack[3];
            attack[5] = attack[3];
            attack[6] = attack[2];
            attack[7] = attack[1];
            attack[8] = attack[0];

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks the collision in the bat in the Y direction
     * Does this by temporarily making the bats hit box be where the bat will go
     * based off its ySpeed. Will prevent the bat from falling or flying up through tiles.
     * must be called after the ySpeed value has been determined, but before it is added to the y coordinate of
     * the bat.
     * Pre-Conditions:
     *      - Bat and walls must be on the same GamePanel panel.
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
                flyUP = !flyUP;
                count = 0;
                y = hitBox.y;
            }
        }
    }
    /**
     * Checks the collision in the bat in the Y direction
     * Does this by temporarily making the bats hit box be where the bat will go
     * based off its ySpeed. Will prevent the bat from falling or flying up through tiles.
     * must be called after the ySpeed value has been determined, but before it is added to the y coordinate of
     * the bat.
     * Pre-Conditions:
     *      - Bat and walls must be on the same GamePanel panel.
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
}
