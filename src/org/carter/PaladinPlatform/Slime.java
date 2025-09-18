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
    public double xspeed;
    public double yspeed;
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

    public void update(){
        if (faceRight) {
            xspeed += 1;
        } else {
            xspeed -= 1;
        }

        if(!launch) {
            if (xspeed > maxSpeed) {
                xspeed = maxSpeed;
            }
            if (xspeed < -maxSpeed) {
                xspeed = -maxSpeed;
            }
        }


        yspeed += fallSpeed;
        checkJump();
        checkCollisionY();
        checkCollisionX();

        if(x >= panel.screenWidth - width){
            faceRight = false;
            xspeed = -1;
        } else if(x <= 0){
            faceRight = true;
            xspeed = 1;
        }
        if(dying){
            xspeed = 0;
        }

        x += xspeed;
        y += yspeed;
        hitBox.x = x;
        hitBox.y = y;
    }
    public void draw(Graphics2D gtd){
        gtd.setColor(Color.RED);
        gtd.drawRect(x, y, width, height);
        if (faceRight) {
            gtd.drawImage(currFrame, x, y - (currFrame.getHeight() - height), width, currFrame.getHeight(), null);
            gtd.drawRect(x + width, y + height, width ,height);
        } else {
            gtd.drawImage(currFrame, x + width, y - (currFrame.getHeight() - height), -width, currFrame.getHeight(), null);
            gtd.drawRect(x-width,y+height, width ,height);
        }
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
            }
        }
    }

    public void nextFrame(){
        if(dying){
            if(frameNum >= death.length){
                frameNum = 11;
                dying = false;
            }
            currFrame = death[frameNum];
            frameNum++;

        }else if(alive) {
            if (frameNum >= slime.length) {
                frameNum = 0;
            }
            currFrame = slime[frameNum];
            frameNum++;
        }
    }

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
                    yspeed = jumpSpeed;
                }
                hitBox.y--;
            }
        }
    }
    public void checkCollisionY(){
        //bottom collision
        hitBox.y += yspeed;
        for(Wall wall : panel.walls){
            if(hitBox.intersects(wall.hitBox)){
                hitBox.y -= yspeed;
                while(!wall.hitBox.intersects(hitBox)){hitBox.y += Math.signum(yspeed);}
                hitBox.y -= Math.signum(yspeed);
                yspeed = 0;
                launch = false;
                y = hitBox.y;
            }
        }
    }
    public void checkCollisionX(){
        hitBox.x += xspeed;
        for(Wall wall : panel.walls){
            if(hitBox.intersects(wall.hitBox)){
                hitBox.x -= xspeed;
                while(!wall.hitBox.intersects(hitBox)){hitBox.x += Math.signum(xspeed);}
                hitBox.x -= Math.signum(xspeed);
                xspeed = 0;
                x = hitBox.x;
                faceRight = !faceRight;
            }
        }
    }
    public void setFaceRight(boolean faceRight){
        this.faceRight = faceRight;
    }
    public void setXspeed(int dx){
        xspeed = dx;
    }
    public void setYspeed(double dy){
        yspeed = dy;
    }
    public void setLaunch(boolean launch){
        this.launch = launch;
    }
}
