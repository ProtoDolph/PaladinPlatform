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
                    tempDX = 1;
                } else if (tempDX < 0){
                    tempDX = -1;
                }
                tempDY = -.2;
                if(ySpeed < -2){
                    ySpeed = -2;
                }
                xSpeed = tempDX;
                ySpeed += tempDY;
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
        if(dying){
            xSpeed = 0;
            ySpeed = 1;
        }

        int dx = (int) Math.round(xSpeed);
        int dy = (int) Math.round(ySpeed);
        x += dx;
        y += dy;
        hitBox.x = x;
        hitBox.y = y;
        count++;
    }

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
        if(dying){
            if(frameNum >= death.length){
                dying = false;
                frameNum = 0;
            }
            currFrame = death[frameNum];

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
                attacking = false;
            }
        }
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
