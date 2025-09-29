package org.carter.PaladinPlatform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The player class that contains all of its data including
 * coordinates, dimensions, hit box coordinates and dimensions
 * collisions, animations, the current frame, the frame number,
 * velocity, attacking, jumping, hostile collisions.
 * HP, if its dying and if it's alive.
 */
public class Player {
    GamePanel panel;
    int x;
    int y;
    int width;
    int height;
    int iWidth;
    double xSpeed;
    double ySpeed;

    double moveSpeed;
    double maxSpeed;
    double stopSpeed;
    double jumpSpeed;
    double fallSpeed;

    Rectangle hitBox;

    boolean keyLeft;
    boolean keyRight;
    boolean keyUp;
    boolean keyDown;
    boolean keySpace;
    boolean spacePressed;
    boolean attacking;
    boolean jumping;
    boolean hit;
    boolean invulnerable;
    double playerHP;
    int maxHP;
    boolean alive;
    boolean healing;
    int healCounter;
    boolean dying;

    //frames
    BufferedImage[] idle = new BufferedImage[12];
    BufferedImage[] walk = new BufferedImage[8];
    BufferedImage[] run = new BufferedImage[7];
    BufferedImage[] attack = new BufferedImage[4];
    BufferedImage[] jump = new BufferedImage[11];
    BufferedImage[] hurt = new BufferedImage[6];
    BufferedImage[] dead = new BufferedImage[16];
    BufferedImage[] heal = new BufferedImage[10];

    int motion;  // idle  = 0;  walk = 1; jump = 2; attack = 3;
    boolean facingLeft;
    int frameNum;
    BufferedImage currFrame;

    /**
     * The constructor of the player. Creates the player at a specified x and y coordinate
     * With the GamePanel so the player can access other entities hit boxes through the GamePanel
     * Creates the hit Box, sets the width, height, the maxSpeed, the moveSpeed, the fallSpeed,
     * Sets the proper booleans to false or true. Sets player HP. Loads the player images for animation
     * through loadPlayerImages(); sets the current frame and frame num.
     * @param x the x coordinate teh player is spawning in at.
     * @param y the y coordinate the player is spawning in at.
     * @param panel the GamePanel the player is a part of so the player class can reference it.
     */
    public Player(int x, int y, GamePanel panel){

        this.panel = panel;
        this.x = x;
        this.y = y;
        spacePressed = false;

        moveSpeed = 1;
        maxSpeed = 5;
        stopSpeed = 0.7;
        jumpSpeed = -7.2;
        fallSpeed = 0.3;
        maxHP = 100;
        playerHP = maxHP;

        hit = false;
        invulnerable = false;
        motion = 0;

        width = 32;
        iWidth = 44;
        height = 64;
        hitBox = new Rectangle(x, y, width, height);

        jumping = false;
        attacking = false;
        loadPlayerImages();
        frameNum = 0;
        currFrame = idle[0];
        alive = true;
        dying = false;
    }

    /**
     * This updates the player every frame in the gamePanel
     * Updates its position. Checks key input for movement jumping or attacking
     * Updates its animation. Checks collision and updates velocity, Checks if it is hit by hostile hit boxes.
     * Updates velocities and then updates position and hitBox position.
     * Preconditions:
     *  - Player must be a constructed object.
     *  - player must have its loadPlayerImages(); called.
     *  - key input must have a listener processing it. Won't cause an error. Just won't update
     *  - Must be part of the GamePanel
     *  Post:
     *  - Player is updated based on its movement and actions
     *  - collisions is checked to avoid the player from passing through things.
     *  - hostile collisions are checked to see if the player gets hit.
     */
    public void set(){

        checkCollisionX();
        //left and right movement
        if ((keyLeft && keyRight) || (!keyLeft && !keyRight)) {
            xSpeed *= stopSpeed;
            if (xSpeed > 0 && xSpeed < 0.75) xSpeed = 0;
            if (xSpeed < 0 && xSpeed > -0.75) xSpeed = 0;
        } else if (keyLeft && !keyRight) {
            facingLeft = true;
            xSpeed -= moveSpeed;
        } else if (keyRight && !keyLeft) {
            facingLeft = false;
            xSpeed += moveSpeed;
        }

        if (xSpeed > maxSpeed) {
            xSpeed = maxSpeed;
        }
        if (xSpeed < -maxSpeed) {
            xSpeed = -maxSpeed;
        }

        // healing.
        if(keyDown && !keyRight && !keyLeft && !jumping && !keyUp && !keySpace && !attacking && !hit){
            hitBox.y++;
            //Checks to see if we are on the ground.
            for(Wall wall : panel.walls){
                if(wall.hitBox.intersects(hitBox)){
                    healing = true;
                    if(healCounter == 0){
                        frameNum = 0;
                    }
                }
            }
            hitBox.y--;
        }else {
            healing = false;
        }

        //Jumping
        if (keyUp) {
            hitBox.y++;
            for (Wall wall : panel.walls) {
                if (wall.hitBox.intersects(hitBox)) {
                    jumping = true;
                }
            }
            hitBox.y--;
            hitBox.y--;
            for (Wall wall : panel.walls) {
                if (wall.hitBox.intersects(hitBox)) {
                    jumping = false;
                }
            }
            hitBox.y++;
            if (jumping) {
                hitBox.y++;
                for (Wall wall : panel.walls) {
                    if (wall.hitBox.intersects(hitBox)) {
                        frameNum = 0;
                        ySpeed = jumpSpeed;
                    }
                }
                hitBox.y--;
            }
        }
        if(jumping && !keyUp){
            if(ySpeed < 0) {
                ySpeed += 0.3;
            }
        }

        if (keySpace) {
            if (!attacking && !hit) {
                attacking = true;
                frameNum = 0;
            }
        }

        if (xSpeed != 0) {
            motion = 1;
        } else {
            motion = 0;
        }
        if (jumping) {
            motion = 2;
        }
        if(healing){
            xSpeed = 0;
            healCounter++;
            if(healCounter >= 30){
                playerHP += .2;
            }
            if(playerHP >= maxHP){
                playerHP = maxHP;
            }
        } else {
            healCounter = 0;
        }

        if(dying){
            xSpeed = 0;
        }
        //falling
        ySpeed += fallSpeed;

        checkHit();
        checkCollisionY();
        checkCollisionX();
        checkDead();
        x += xSpeed;
        y += ySpeed;

        hitBox.x = x;
        hitBox.y = y;
    }

    /**
     * The draw method for the player. Will draw the player based on the current frame the player is in
     * Will draw the current frame facing left or right depending on the facingLeft boolean.
     * Optional collision box being drawn. It is commented out.
     * Will also draw the player's hp bar.
     * @param gtd The graphics base the player will be drawn on.
     */
    public void draw(Graphics2D gtd){
        //Frame check
        // Collision Box and Attack Hit Box.
/*
        gtd.setColor(Color.YELLOW);
        gtd.fillRect(x,y,width,height);

        if(attacking){
            gtd.setColor(Color.RED);
            if(facingLeft) {
                gtd.fillRect(x - (width + width/2), y, 2*width, height);
            }else{
                gtd.fillRect(x + width/2, y, 2*width, height);
            }
        }
*/


        if(facingLeft){
            if(currFrame == attack[3]){
                gtd.drawImage(currFrame,x+width+21,y - (currFrame.getHeight() -height),-currFrame.getWidth(),currFrame.getHeight(),null);

            } else{
                gtd.drawImage(currFrame,x+width+6,y - (currFrame.getHeight() -height),-currFrame.getWidth(),currFrame.getHeight(),null);
            }
        }else{
            if(currFrame == attack[3]){
                gtd.drawImage(currFrame, x-21,y - (currFrame.getHeight() -height),currFrame.getWidth(),currFrame.getHeight(),null);
            }else{
                gtd.drawImage(currFrame, x-6,y - (currFrame.getHeight() -height),currFrame.getWidth(),currFrame.getHeight(),null);
            }
        }
        gtd.setColor(Color.RED);
        int hpWidth = (int) Math.round(playerHP * 2);
        gtd.fillRect(0,0,hpWidth, 32);
        gtd.setColor(Color.BLACK);
        gtd.setStroke(new BasicStroke(5));
        gtd.drawRect(0,0,maxHP*2,32);
        gtd.setStroke(new BasicStroke(1));
    }

    /**
     * Checks the player's collision in the Y direction.
     * If the player is falling and hits a tile it will make it so that the player stops and doesn't fall through
     * Same for if the Player is jumping up through tile.
     * It does this by taking the hit box of the player and temporarily putting it where the player will go with teh current
     * ySpeed. It then checks if the new hit box will intersect any tiles. If it does it will reduce the ySpeed of the player
     * until the HitBox doesn't intersect with any walls or tiles.
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
                jumping = false;
                y = hitBox.y;
            }
        }
    }
    /**
     * Checks the player's collision in the X direction.
     * If the player is moving left or right This will make sure the Player doesn't walk through any tiles or walls.
     * It does this by taking the hit box of the player and temporarily putting it where the player will go with the current
     * xSpeed of the player. It then checks if the new hit box will intersect any tiles.
     * If it does it will reduce the xSpeed of the player
     * until the HitBox doesn't intersect with any walls or tiles.
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
            }
        }
    }

    /**
     * Sets the x coordinate of the Player with a new x value.
     * Used to reset the player's position in between levels and parts of levels.
     * @param x the new X coordinate of the player.
     */
    public void setX(int x){this.x = x;}

    /**
     * Sets the y coordinate of the Player with a new y value.
     * Used to reset the Player's position in between levels and parts of levels.
     * @param y the new Y coordinate of the player.
     */
    public void setY(int y) {this.y = y;}

    /**
     * This method checks if the player is hit by any hostile entities.
     * That being slimes, bats, bosses, and spikes.
     * Using its GamePanel reference it will check the lists of all the entities
     * then will check if that entity is still alive
     * then checks if that entity's hit box is intersecting the player's hit box
     * then will make sure the player is not already in teh hurt animation to avoid infinite damage.
     * IF the player is not hit and does intersect a hostile entity's hit box it will
     * set hit to true, the frameNum to 0 to start the hurt animation. and give teh player a little jolt in jump speed.
     */
    public void checkHit(){
        for(Slime slime : panel.slimes){
            if(slime.alive && alive) {
                if (hitBox.intersects(slime.hitBox)) {
                    if (!hit) {
                        frameNum = 0;
                        playerHP -= 10;
                        ySpeed = 3 * (jumpSpeed / 4);
                        attacking = false;
                        if (facingLeft) {
                            xSpeed -= 2 * jumpSpeed;
                        } else {
                            xSpeed += 2 * jumpSpeed;
                        }
                    }
                    hit = true;
                    healing = false;
                }
            }
        }
        for(Bat bat : panel.bats){
            if(bat.alive && alive) {
                if (hitBox.intersects(bat.hitBox)) {
                    if (!hit) {
                        frameNum = 0;
                        playerHP -= 10;
                        attacking = false;
                        if (facingLeft) {
                            xSpeed -= 2 * jumpSpeed;
                        } else {
                            xSpeed += 2 * jumpSpeed;
                        }
                    }
                    hit = true;
                    healing = false;
                }
            }
        }
        for(Spike spike: panel.spikes){
            if(alive) {
                if (hitBox.intersects(spike.hitBox)) {
                    if (!hit) {
                        frameNum = 0;
                        playerHP -= 10;
                        ySpeed = 3 * (jumpSpeed / 4);
                        attacking = false;
                        if (facingLeft) {
                            xSpeed -= 2 * jumpSpeed;
                        } else {
                            xSpeed += 2 * jumpSpeed;
                        }
                    }
                    hit = true;
                    healing = false;
                }
            }
        }
        for(SlimeBoss boss : panel.bosses){
            if(alive){
                if (hitBox.intersects(boss.hitBox)) {
                    if (!hit) {
                        frameNum = 0;
                        playerHP -= 10;
                        ySpeed = 3 * (jumpSpeed / 4);
                        attacking = false;
                        if (facingLeft) {
                            xSpeed -= 2 * jumpSpeed;
                        } else {
                            xSpeed += 2 * jumpSpeed;
                        }
                    }
                    hit = true;
                    healing = false;
                }
                if(boss.attack1){
                    if(hitBox.intersects(boss.atk1HitBox) && boss.currFrame == boss.atk1[7]){
                        if (!hit) {
                            frameNum = 0;
                            playerHP -= 10;
                            ySpeed = 3 * (jumpSpeed / 4);
                            attacking = false;
                            if (facingLeft) {
                                xSpeed -= 2 * jumpSpeed;
                            } else {
                                xSpeed += 2 * jumpSpeed;
                            }
                        }
                        hit = true;
                        healing =  false;
                    }
                }
                if(boss.attack3){
                    if(hitBox.intersects(boss.atk3HitBox)){
                        if (!hit) {
                            frameNum = 0;
                            playerHP -= 10;
                            ySpeed = 3 * (jumpSpeed / 4);
                            attacking = false;
                            if (facingLeft) {
                                xSpeed -= 2 * jumpSpeed;
                            } else {
                                xSpeed += 2 * jumpSpeed;
                            }
                        }
                        hit = true;
                        healing = false;
                    }
                }
            }
        }
    }

    /**
     * This method checks to see if the player is still alive.
     * First it checks to see if the player's hp is less than zero
     * if it is then it sets the player alive boolean to false and dying to true
     * to start the death animation. Then when the death animation is finished dying is set to false
     *  and the game ends.
     */
    public void checkDead(){
        if(playerHP <= 0 && alive){
            alive = false;
            dying = true;
        }
    }
    /**
     * Loads all the images for animation of the knight.
     * Surrounded in a try catch to prevent errors
     * images are hard coded in so do not change values of x,y,width,height, or file name.
     */
    public void loadPlayerImages(){
        try{
            //idle
            BufferedImage ogIdle = ImageIO.read(getClass().getResourceAsStream("/Knight_1/Idle.png"));
            idle[0] = ogIdle.getSubimage(12,128-height,iWidth,height);
            idle[1] = idle[0];
            idle[2] = idle[0];
            idle[3] = ogIdle.getSubimage(140,128-height,iWidth,height);
            idle[4] = idle[3];
            idle[5] = idle[3];
            idle[6] = ogIdle.getSubimage(268, 128-height,iWidth,height);
            idle[7] = idle[6];
            idle[8] = idle[6];
            idle[9] = ogIdle.getSubimage(396,128-height,iWidth,height);
            idle[10] = idle[9];
            idle[11] = idle[9];

            //walk
            BufferedImage ogWalk = ImageIO.read(getClass().getResourceAsStream("/Knight_1/Walk.png"));
            walk[0] = ogWalk.getSubimage(12, 128-height, iWidth, height);
            walk[1] = ogWalk.getSubimage(144,128-height,iWidth,height);
            walk[2] = ogWalk.getSubimage(270, 128-height,iWidth+6,height);
            walk[3] = ogWalk.getSubimage(398,128-height,iWidth+3,height);
            walk[4] = ogWalk.getSubimage(530,128-height,iWidth+2,height);
            walk[5] = ogWalk.getSubimage(655,128-height,iWidth,height);
            walk[6] = ogWalk.getSubimage(782,128-height,iWidth,height);
            walk[7] = ogWalk.getSubimage(910,128-height, iWidth, height);

            BufferedImage ogJump = ImageIO.read(getClass().getResourceAsStream("/Knight_1/Jump.png"));
            jump[0] = ogJump.getSubimage(20,128-height,iWidth+5,height);
            jump[1] = jump[0];
            jump[2] = ogJump.getSubimage(145, 47, iWidth+5,height);
            jump[3] = jump[2];
            jump[4] = ogJump.getSubimage(264,47,iWidth+2,height);
            jump[5] = jump[4];
            jump[6] = jump[4];
            jump[7] = jump[2];
            jump[8] = jump[2];
            jump[9] = jump[0];
            jump[10] = jump[0];

            BufferedImage ogRun = ImageIO.read(getClass().getResourceAsStream("/Knight_1/Run.png"));
            run[0] = ogRun.getSubimage(20,128-height,iWidth+5,height);
            run[1] = ogRun.getSubimage(128,128-height,iWidth+10,height);
            run[2] = ogRun.getSubimage(258,64,iWidth+2,height);
            run[3] = ogRun.getSubimage(398,64,iWidth+3,height);
            run[4] = ogRun.getSubimage(531,64,iWidth+3,height);
            run[5] = ogRun.getSubimage(640,64,iWidth+10,height);
            run[6] = ogRun.getSubimage(770,64,iWidth+5,height);

            BufferedImage ogAttack = ImageIO.read(getClass().getResourceAsStream("/Knight_1/Attack 2.png"));
            attack[0] = ogAttack.getSubimage(14,64,iWidth,height);
            attack[1] = ogAttack.getSubimage(142,58,iWidth,height+6);
            attack[2] = ogAttack.getSubimage(272,56,iWidth+50,height+8);
            attack[3] = ogAttack.getSubimage(386,64,iWidth+22,height);

            BufferedImage ogHurt = ImageIO.read(getClass().getResourceAsStream("/Knight_1/Hurt.png"));
            hurt[0] = ogHurt.getSubimage(16,64,iWidth+2,height);
            hurt[1] = ogHurt.getSubimage(0,0,1,1);
            hurt[2] = hurt[0];
            hurt[3] = ogHurt.getSubimage(144,64,iWidth+2,height);
            hurt[4] = hurt[1];
            hurt[5] = hurt[3];

            BufferedImage ogDead = ImageIO.read(getClass().getResourceAsStream("/Knight_1/Dead.png"));
            dead[0] = ogDead.getSubimage(12,64,iWidth+2,height);
            dead[1] = dead[0];
            dead[2] = ogDead.getSubimage(134,64,iWidth+4,height);
            dead[3] = dead[2];
            dead[4] = ogDead.getSubimage(276,64,iWidth+4,height);
            dead[5] = dead[4];
            dead[6] = ogDead.getSubimage(402,64, iWidth+8,height);
            dead[7] = dead[6];
            dead[8] = ogDead.getSubimage(530,64,iWidth+16,height);
            dead[9] = dead[8];
            dead[10] = ogDead.getSubimage(670,64,iWidth+16,height);
            dead[11] = dead[10];
            dead[12] = dead[10];
            dead[13] = dead[10];
            dead[14] = dead[10];
            dead[15] = dead[10];

            BufferedImage ogHeal = ImageIO.read(getClass().getResourceAsStream("/Knight_1/Defend.png"));
            heal[0] = ogHeal.getSubimage(16,64, iWidth, height);
            heal[1] = ogHeal.getSubimage(144,64, iWidth, height);
            heal[2] = ogHeal.getSubimage(271,64, iWidth, height);
            heal[3] = ogHeal.getSubimage(399,64, iWidth, height);
            heal[4] = ogHeal.getSubimage(528,64, iWidth, height);
            heal[5] = heal[4];
            heal[6] = heal[3];
            heal[7] = heal[2];
            heal[8] = heal[1];
            heal[9] = heal[0];


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * This gets the next frame of whatever action the player is in.
     * Using the currFrame variable to store the current frame. and frameNUm to store
     * the integer of the frame needed in each list of BufferedImage for each action.
     * First it checks if the player is dying as the death animation takes priority.
     * Next in priority is teh hurt animation. Then the attacking animation
     * then the Jumping animation. Then the walking animation. Finally, the last in priority is the idle animation.
     * First it checks if the frameNUm is greater than or equal to the length of the BufferedImage[] for the current action.
     * if frameNum is grater than the length it will set FrameNum to 0 and on some animation set the action boolean
     * for that animation to false; Then after finding the next frame it sets currFrame to equal it. Then it increases frameNum by 1
     * for the next time nextFrame() is called.
     */
    public void nextFrame(){
        if(dying){
            if(frameNum >= dead.length){
                dying = false;
                frameNum = 0;
                currFrame = dead[dead.length -1];
            }else {
                currFrame = dead[frameNum];
            }
            frameNum++;
        } else if(hit){
            if(frameNum >= hurt.length){
                hit = false;
                frameNum = 0;
            }
            currFrame = hurt[frameNum];
            frameNum++;
        }else if(healing){
            if(frameNum >= heal.length){
                frameNum = 0;
            }
            currFrame = heal[frameNum];
            frameNum++;
        }else if(attacking){
            if(frameNum >= attack.length){
                attacking = false;
                frameNum = 0;
                keySpace = false;
            }
            currFrame = attack[frameNum];
            frameNum++;
        } else if(jumping) {
            if(frameNum >= jump.length) {
                jumping = false;
                frameNum = 0;
            }
            currFrame = jump[frameNum];
            frameNum++;
        }else if(motion == 0){
            if (frameNum >= idle.length){
                frameNum = 0;
            }
            currFrame = idle[frameNum];
            frameNum++;
        } else if(motion == 1){
            if(frameNum >= run.length){
                frameNum = 0;
            }
            currFrame = run[frameNum];
            frameNum++;
        }
    }
}
