package org.carter.PaladinPlatform;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

public class SlimeBoss {
    public GamePanel panel;
    public int x;
    public int y;
    public int width;
    public int height;
    public double xSpeed;
    public double ySpeed;
    public Rectangle hitBox;
    public double maxSpeed;
    public double fallSpeed;
    public double jumpSpeed;
    public boolean attack1;
    public Rectangle atk1HitBox;
    public BufferedImage[] atk1 = new BufferedImage[14];
    public boolean attack2;
    public BufferedImage[] atk2 = new BufferedImage[10];
    public boolean attack3;
    public BufferedImage[] atk3 = new BufferedImage[8];
    public Rectangle atk3HitBox;
    public BufferedImage[] idle = new BufferedImage[8];
    public BufferedImage[] death = new BufferedImage[9];
    public BufferedImage[] hurt = new BufferedImage[5];
    public BufferedImage currFrame;
    public BufferedImage flash;
    public int frameNum;
    public boolean faceRight;
    public int hp;
    public boolean dying;

    public int playerX;
    public int count;
    public boolean hit;
    public int hurtCounter;
    public boolean alive = true;

    public SlimeBoss(int x, int y, GamePanel panel){
        this.panel = panel;
        this.x = x;
        this.y = y;
        height = 64;
        width = 96;
        hitBox = new Rectangle(x,y,width,height);
        dying = false;
        hp = 100;
        faceRight = true;
        attack1 = false;
        atk1HitBox = new Rectangle(x,y,96, height);
        attack2 = false;
        attack3 = false;
        atk3HitBox = new Rectangle(x-32,y, 136,height-12);
        count = 0;
        hurtCounter = 0;
        hit = false;

        frameNum = 0;
        fallSpeed = 0.3;
        jumpSpeed = -9;
        maxSpeed = 6;
        playerX = panel.player.x;

        loadImages();
        currFrame = idle[0];
    }

    public void update(){
        if(alive) {
            //attack check.
            if (!hit && !attack1 && !attack2 && !attack3 && !dying) {
                if (count > 90) {
                    count = 0;
                    Random rand = new Random();
                    int r = rand.nextInt(3);
                    frameNum = 0;
                    checkPlayer();
                    if (r == 0) {
                        attack1 = true;
                    } else if (r == 1) {
                        attack2 = true;
                        ySpeed = jumpSpeed;
                        xSpeed = (playerX - (x + (width / 2)));
                    } else if (r == 2) {
                        attack3 = true;
                        if (faceRight) {
                            xSpeed = maxSpeed;
                        } else {
                            xSpeed = -maxSpeed;
                        }
                    }
                } else {
                    xSpeed = 0;
                }
            }
            if (attack1 && count > 70) {
                attack1 = false;
                count = 0;
            }
            if (attack3 && count > 144) {
                attack3 = false;
                count = 0;
            }
            if (attack2) {
                xSpeed = (playerX - (x + (width / 2)));
            }

            if (xSpeed > maxSpeed) {
                xSpeed = maxSpeed;
            }
            if (xSpeed < -maxSpeed) {
                xSpeed = -maxSpeed;
            }
            if(attack1){
                xSpeed = 0;
            }
            if(attack3){
                if(faceRight){
                    xSpeed = maxSpeed;
                } else {
                    xSpeed = -maxSpeed;
                }
            }
        }
        ySpeed += fallSpeed;
        checkCollisionY();
        if(attack2 && checkJump()){
            attack2 = false;
            count = 0;
        }
        checkCollisionX();

        if(x >= panel.screenWidth - width){
            faceRight = false;
            xSpeed = -xSpeed;
        } else if(x <= 0){
            faceRight = true;
            xSpeed = -xSpeed;
        }

        if(!alive){
            attack1 = false;
            attack2 = false;
            attack3 = false;
        }

        if(dying){
            xSpeed = 0;
        }

        x += xSpeed;
        y += ySpeed;
        if(attack1){
            atk1HitBox.x = x;
            atk1HitBox.y = y;
        } else {
            atk1HitBox.x = -1000;
            atk1HitBox.y = -1000;
        }
        if(attack3){
            atk3HitBox.x = x - 24;
            atk3HitBox.y = y+12;
        } else {
            atk3HitBox.x = -1000;
            atk3HitBox.y = -1000;
        }
        hitBox.x = x;
        hitBox.y = y;
        count++;
        checkHit();
        checkAlive();
        if(hit) {
            hurtCounter++;
            if(hurtCounter >= 60){
                hit = false;
                hurtCounter = 0;
            }
        }


    }
    public void draw(Graphics2D gtd){

        //gtd.setColor(Color.RED);
        //gtd.drawRect(x, y, width, height);

        if(attack1){
            if (faceRight) {
                if(currFrame == atk1[7] || currFrame == atk1[8]){
                    //gtd.setColor(Color.RED);
                    //gtd.fillRect(atk1HitBox.x + width, atk1HitBox.y, atk1HitBox.width, atk1HitBox.height);
                    gtd.drawImage(currFrame, x - 4, y - ((2 * currFrame.getHeight() - height)), 3 * currFrame.getWidth(), 2*currFrame.getHeight(), null);

                }else if (currFrame == atk1[9]){
                    gtd.drawImage(currFrame, x - 4, y - ((2 * currFrame.getHeight() - height)), (int) (2.5 * currFrame.getWidth()), 2*currFrame.getHeight(), null);
                    //gtd.drawRect(x + width, y + height, width, height);
                } else{
                    gtd.drawImage(currFrame, x - 4, y - ((2 * currFrame.getHeight() - height)), 2 * currFrame.getWidth(), 2*currFrame.getHeight(), null);
                }
            } else {
                if(currFrame == atk1[7] || currFrame == atk1[8]) {
                    //gtd.setColor(Color.RED);
                    //gtd.fillRect(atk1HitBox.x - atk1HitBox.width, atk1HitBox.y, atk1HitBox.width, atk1HitBox.height);
                    gtd.drawImage(currFrame, x + width + 4, y - ((2 * currFrame.getHeight() - height)), -3 * currFrame.getWidth(), 2*currFrame.getHeight(), null);
                } else if (currFrame == atk1[9]){
                    gtd.drawImage(currFrame, x + width + 4, y - ((2 * currFrame.getHeight() - height)), (int) (-2.5 * currFrame.getWidth()), 2*currFrame.getHeight(), null);
                }else {
                    gtd.drawImage(currFrame, x + width + 4, y - ((2 * currFrame.getHeight() - height)), -(2 * currFrame.getWidth()), 2*currFrame.getHeight(), null);
                }
            }
        }
        else if(attack2){
            if (faceRight) {
                gtd.drawImage(currFrame, x, y - ((2*(currFrame.getHeight()) - height)), 2*currFrame.getWidth(), 2*currFrame.getHeight(), null);
            } else {
                gtd.drawImage(currFrame, x + width, y - ((2*(currFrame.getHeight()) - height)), -(2*currFrame.getWidth()), 2*currFrame.getHeight(), null);
            }
        }
        else if(attack3){
            //gtd.setColor(Color.RED);
            //gtd.fillRect(atk3HitBox.x, atk3HitBox.y, atk3HitBox.width, atk3HitBox.height);
            if (frameNum < 5) {
                if(currFrame == atk3[0] || currFrame == atk3[1]){
                    gtd.drawImage(currFrame, x-6, y - ((2 * (currFrame.getHeight()) - height)), 2 * currFrame.getWidth(), 2*currFrame.getHeight(), null);
                } else {
                    gtd.drawImage(currFrame, x, y - ((2 * (currFrame.getHeight()) - height)), 2 * currFrame.getWidth(), 2*currFrame.getHeight(), null);
                }
            } else {
                if(currFrame == atk3[7] || currFrame == atk3[6]){
                    gtd.drawImage(currFrame, x + width+6, y - ((2 * (currFrame.getHeight()) - height)), -(2 * currFrame.getWidth()), 2*currFrame.getHeight(), null);
                } else {
                    gtd.drawImage(currFrame, x+width, y - ((2 * (currFrame.getHeight()) - height)), -(2 * currFrame.getWidth()), 2*currFrame.getHeight(), null);
                }
            }
        }
        else {
            if (faceRight) {
                if(currFrame == hurt[2]){
                    gtd.drawImage(currFrame, x-21, y - (2 * (currFrame.getHeight()) - height), 2 * currFrame.getWidth(), 2 * currFrame.getHeight(), null);
                }else if(currFrame == hurt[3]){
                    gtd.drawImage(currFrame, x-60, y - (2 * (currFrame.getHeight()) - height), 2 * currFrame.getWidth(), 2 * currFrame.getHeight(), null);

                }else if(currFrame == hurt[4]){
                    gtd.drawImage(currFrame, x-68, y - (2 * (currFrame.getHeight()) - height), 2 * currFrame.getWidth(), 2 * currFrame.getHeight(), null);
                }else {
                    gtd.drawImage(currFrame, x, y - (2 * (currFrame.getHeight()) - height), 2 * currFrame.getWidth(), 2 * currFrame.getHeight(), null);
                }
            } else {
                if (currFrame == hurt[2]) {
                    gtd.drawImage(currFrame, x+width + 21, y - (2 * (currFrame.getHeight()) - height), -2 * currFrame.getWidth(), 2 * currFrame.getHeight(), null);
                }else if(currFrame == hurt[3]){
                    gtd.drawImage(currFrame, x + width +60, y - (2 * (currFrame.getHeight()) - height), -2 * currFrame.getWidth(), 2 * currFrame.getHeight(), null);
                }else if(currFrame == hurt[4]){
                    gtd.drawImage(currFrame, x+width+68, y - (2 * (currFrame.getHeight()) - height), -2 * currFrame.getWidth(), 2 * currFrame.getHeight(), null);
                }else{
                    gtd.drawImage(currFrame, x + width, y - (2 * (currFrame.getHeight()) - height), -(2 * currFrame.getWidth()), 2 * currFrame.getHeight(), null);
                }
            }
        }
    }

    public void checkPlayer(){
        Player player = panel.player;
        playerX = player.x + player.width/2;
        if(playerX < x){
            faceRight = false;
        } else{
            faceRight = true;
        }
    }

    public void nextFrame(){
        if(dying){
            if(frameNum >= death.length){
                frameNum = 0;
                dying = false;
                alive = false;
                panel.bosses.remove(this);
            }
            currFrame = death[frameNum];

        }
        else if(attack1){
            if(frameNum >= atk1.length){
                frameNum = 0;
            }
            currFrame = atk1[frameNum];
            if(frameNum == 7){
                Slime slime = new Slime(x, y, panel);
                slime.setFaceRight(faceRight);
                slime.setLaunch(true);
                if(faceRight){
                    slime.setXspeed(7);
                } else{
                    slime.setXspeed(-7);
                }
                slime.setYspeed(-3);
                panel.slimes.add(slime);
            }
        }
        else if(attack2){
            if(frameNum >= atk2.length){
                frameNum = 0;
            }
            currFrame = atk2[frameNum];
        }
        else if(attack3){
            if(frameNum >= atk3.length){
                frameNum = 0;
            }
            currFrame = atk3[frameNum];

        }else if(hit){
            if(frameNum >= hurt.length){
                frameNum = 0;
            }
            currFrame = hurt[frameNum];
        }else{
            if(frameNum >= idle.length){
                frameNum = 0;
            }
            currFrame = idle[frameNum];
        }
        frameNum++;
    }

    public void loadImages(){
        try {
            int iWidth = 48;
            int iHeight = 32;
            BufferedImage ogIdle = ImageIO.read(getClass().getResourceAsStream("/slimeSprite/Red_Slime/Idle.png"));
            idle[0] = ogIdle.getSubimage(38,96, iWidth, iHeight);
            idle[1] = ogIdle.getSubimage(166,96, iWidth, iHeight);
            idle[2] = ogIdle.getSubimage(294,96, iWidth, iHeight);
            idle[3] = ogIdle.getSubimage(422,96, iWidth, iHeight);
            idle[4] = ogIdle.getSubimage(550,96, iWidth, iHeight);
            idle[5] = ogIdle.getSubimage(678,96, iWidth, iHeight);
            idle[6] = ogIdle.getSubimage(806,96, iWidth, iHeight);
            idle[7] = ogIdle.getSubimage(934, 96, iWidth, iHeight);

            BufferedImage ogAtk12 = ImageIO.read(getClass().getResourceAsStream("/slimeSprite/Red_Slime/Attack_2.png"));
            BufferedImage ogAtk1 = ImageIO.read(getClass().getResourceAsStream("/slimeSprite/Red_Slime/Attack_1.png"));
            atk1[0] = ogAtk1.getSubimage(27,96, iWidth+1, iHeight);
            atk1[1] = ogAtk1.getSubimage(153,96,iWidth+4,iHeight);
            atk1[2] = ogAtk1.getSubimage(284,94, iWidth+4, iHeight+2);
            atk1[3] = atk1[1];
            atk1[4] = atk1[0];
            atk1[5] = atk1[1];
            atk1[6] = atk1[2];

            // hit box appears.
            atk1[7] = ogAtk1.getSubimage(420,96, iWidth+18, iHeight);
            atk1[8] = atk1[7];

            atk1[9] = ogAtk12.getSubimage(32,96,iWidth+5, iHeight);
            atk1[10] = ogAtk12.getSubimage(32,96,iWidth+5, iHeight);
            atk1[11] = atk1[2];
            atk1[12] = atk1[1];
            atk1[13] = atk1[0];

            BufferedImage ogAtk3 = ImageIO.read(getClass().getResourceAsStream("/slimeSprite/Red_Slime/Attack_3.png"));
            //atk3[0] = ogAtk3.getSubimage(37, 94, iWidth+5, iHeight+2);
            atk3[0] = ogAtk3.getSubimage(159,94,iWidth+10,iHeight+2);
            atk3[1] = ogAtk3.getSubimage(290,94,iWidth+20,iHeight+2);
            //attack
            atk3[2] = ogAtk3.getSubimage(422, 94, iWidth+22, iHeight+2);

            atk3[3] = ogAtk3.getSubimage(548, 94, iWidth+20, iHeight+2);
            atk3[4] = atk3[3];
            //attack
            atk3[5] = atk3[2];
            atk3[6] = atk3[1];
            atk3[7] = atk3[0];

            BufferedImage ogAtk2 = ImageIO.read(getClass().getResourceAsStream("/slimeSprite/Red_Slime/Run+Attack.png"));
            atk2[0] = ogAtk2.getSubimage(32, 92, iWidth, iHeight+4);
            atk2[1] = ogAtk2.getSubimage(158,92, iWidth, iHeight+4);
            atk2[2] = ogAtk2.getSubimage(284,94, iWidth, iHeight+2);
            atk2[3] = ogAtk2.getSubimage(415, 86, iWidth+2, iHeight +10);
            atk2[4] = ogAtk2.getSubimage(542, 82, iWidth, iHeight+14);
            atk2[5] = ogAtk2.getSubimage(670, 72, iWidth, iHeight+14);
            atk2[6] = ogAtk2.getSubimage(800, 72, iWidth, iHeight+16);
            atk2[7] = ogAtk2.getSubimage(929, 74, iWidth, iHeight+17);
            atk2[8] = ogAtk2.getSubimage(1060, 80, iWidth, iHeight+16);
            atk2[9] = ogAtk2.getSubimage(1192, 80, iWidth, iHeight+16);

            BufferedImage ogDeath = ImageIO.read(getClass().getResourceAsStream("/slimeSprite/Red_Slime/Dead.png"));
            BufferedImage ogHurt = ImageIO.read(getClass().getResourceAsStream("/slimeSprite/Red_Slime/Hurt.png"));
            death[0] = ogHurt.getSubimage(44,96,iWidth, iHeight);
            death[1] = ogHurt.getSubimage(170,96,iWidth+2,iHeight);
            death[2] = ogHurt.getSubimage(286, 92, iWidth+14, iHeight+4);
            death[3] = ogHurt.getSubimage(396,94, iWidth+32, iHeight+2);
            death[4] = ogHurt.getSubimage(518,96,iWidth+38,iHeight);
            death[5] = ogHurt.getSubimage(644, 96, iWidth+38, iHeight);
            death[6] = ogDeath.getSubimage(38, 96, iWidth, iHeight);
            death[7] = ogDeath.getSubimage(166, 96, iWidth, iHeight);
            death[8] = ogDeath.getSubimage(292, 96, iWidth+6, iHeight);


            hurt[0] = death[0];
            flash = ogHurt.getSubimage(0,0,2,2);
            hurt[1] = death[1];
            //hurt[3] = hurt[1];
            hurt[2] = death[2];
            //hurt[5] = hurt[1];
            hurt[3] = death[3];
            //hurt[7] = hurt[1];
            hurt[4] = death[4];
            //hurt[9] = hurt[1];
            //hurt[5] = death[5];
            //hurt[11] = hurt[1];

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void checkAlive(){
        if(hp <= 0 && !dying){
            dying = true;
            frameNum = 0;
            alive = false;
        }
    }
    public void checkHit(){
        Player player = panel.player;
        if(player.attacking){
            Rectangle p1HitBox;
            if(player.facingLeft){
                p1HitBox = new Rectangle(player.x - player.width - player.width/2, player.y, 2*player.width, player.height);
            }else {
                p1HitBox = new Rectangle(player.x + player.width/2, player.y, player.width * 2, player.height);
            }
            if(!attack3 && !hit) {
                if (p1HitBox.intersects(hitBox)) {
                    if(player.x < x){
                        faceRight = false;
                    } else {
                        faceRight = true;
                    }
                    hp -= 10;
                    ySpeed = -3;
                    hit = true;
                }
            }
        }
    }
    public boolean checkJump(){
        boolean onGround = false;
        for (Wall wall : panel.walls) {
            hitBox.y++;
            if (wall.hitBox.intersects(hitBox)) {
                onGround = true;
            }
            hitBox.y--;
        }
        return onGround;
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
