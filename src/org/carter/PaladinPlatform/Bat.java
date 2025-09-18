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
    public BufferedImage[] attack = new BufferedImage[4];
    public boolean attacking;
    public boolean flyUP;
    public int count;
    public int sightCount;
    public int playerX;
    public int playerY;
    public int blindCount;
    public Line2D line1;
    public Line2D line2;
    public Line2D line3;
    public Line2D line4;

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

    public void update(){
        checkSight();
        System.out.println(sight);
        if(sight){
            sightCount++;
        } else {
            sightCount = 0;
        }
        if(sightCount >= 15){
            attacking = true;
            sightCount = 0;
            playerX = panel.player.x;
            playerY = panel.player.y;
        }
        if(attacking && !sight){
            blindCount++;
        }
        if(attacking && sight){
            blindCount = 0;
        }
        if(attacking && blindCount >= 20){
            attacking = false;
            blindCount = 0;
        }
        if(attacking){
            if(x > playerX){
                faceRight = false;
            } else {
                faceRight = true;
            }
            ySpeed += (playerY - y)/4;
            int tempDX = (playerX - x )/2;
            if(tempDX < 1 && tempDX > -1){
                if(tempDX > 0){
                    xSpeed += 1;
                } else {
                    xSpeed -= 1;
                }
            } else{
                xSpeed += tempDX;
            }
        }
        if(!attacking) {
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
                ySpeed += 0.33;
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
        if(dying){
            xSpeed = 0;
        }


        x += xSpeed;
        y += ySpeed;
        hitBox.x = x;
        hitBox.y = y;
        count++;
    }

    public void draw(Graphics2D gtd){
        gtd.setColor(Color.RED);
        gtd.fillRect(x, y, width, height);
        gtd.draw(line1);
        gtd.draw(line2);
        gtd.draw(line3);
        gtd.draw(line4);
        if (faceRight) {
            gtd.drawImage(currFrame, x  + width + (currFrame.getWidth() - width)/2, y, -currFrame.getWidth(), height, null);
        } else {
            gtd.drawImage(currFrame, x - (currFrame.getWidth()-width)/2, y, currFrame.getWidth(), height, null);
        }
    }
    public void checkSight(){
        Player player = panel.player;
        line1 = new Line2D.Double(x + width/4,y,player.x+1, player.y);
        line2 = new Line2D.Double(x + width/4, y+height, player.x+1, player.y +player.height-1);
        line3 = new Line2D.Double(x +width - width/4 , y, player.x + player.width -1, player.y);
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

    public void nextFrame(){
        if(frameNum >= idle.length){
            frameNum = 0;
        }
        currFrame = idle[frameNum];
        frameNum++;
    }

    public void loadImages(){
        try{
            int iWidth = 64;
            int iHeight = 32;
            BufferedImage ogImage = ImageIO.read(getClass().getResourceAsStream("/batSpriteSheet.png"));
            idle[0] = ogImage.getSubimage(214,334,iWidth+2,iHeight+8);
            idle[1] = ogImage.getSubimage(308,338,iWidth+12,iHeight+6);
            idle[2] = ogImage.getSubimage(408,346,iWidth+6,iHeight+8);
            idle[3] = ogImage.getSubimage(502, 342, iWidth+12, iHeight+2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
