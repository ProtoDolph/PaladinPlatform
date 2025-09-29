package org.carter.PaladinPlatform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GamePanel extends javax.swing.JPanel implements ActionListener {

    boolean running;
    //player
    Player player;
    //dimensions
    int screenWidth = 672;
    int screenHeight = 672;

    int blockWidth = 32;
    int blockHeight = 32;

    //levels
    int level = 1;
    boolean newLv;
    int screenX = 0;
    int screenY = 0;
    Graphics2D gtd;

    //walls
    ArrayList<Wall> walls = new ArrayList<>();
    BufferedImage brickImage;
    //slimes
    ArrayList<Slime> slimes = new ArrayList<>();
    //bats
    ArrayList<Bat> bats = new ArrayList<>();
    //flag
    Flag flag;
    BufferedImage flagImage;
    boolean flagVisible;
    //background
    BufferedImage background;
    BufferedImage wKey;
    BufferedImage aKey;
    BufferedImage sKey;
    BufferedImage dKey;
    BufferedImage spaceKey;
    boolean keyPress;
    int keyCount;
    // healing
    ArrayList<HealingParticle> heals = new ArrayList<>();
    Random rand = new Random();
    //Spike
    ArrayList<Spike> spikes = new ArrayList<>();
    BufferedImage spikeImage;
    //boss
    ArrayList<SlimeBoss> bosses = new ArrayList<>();
    boolean bossWin;

    //performance
    int fps = 60;
    Timer gameTimer;
    int count;

    /**
     * This GamePanel handles every single entity, update, compiles the graphics, creates the levels.
     * It stores every tile, bat, slime, boss, spike and player. And it updates them and tells the MainFrame to draw them.
     * This keeps tracks of every Level through the level methods.
     * It uses a game timer to handle all the updates at a 60 fps.
     * It updates each frame at 12 FPS.
     * It takes the KeyChecker from the MainFrame checks what key was pressed and if it is important to update player.
     * Uses multiple BufferedImages, ArrayLists,  boolean, doubles and integers to keep track of everything in the game.
     * There are multiple instances of in line comments explaining what stuff does since as soon as this is called as a constructor
     * to a JFrame the game will run.
     */
    public GamePanel(){

        //Creates the player
        player = new Player(35,512,this);

        //loads all the images
        loadImages();
        bossWin = false;
        //initializes the level and creates the first level.
        level = 1;
        makeLv1();

        // starts the game by creating a timer.
        running = true;
        gameTimer = new Timer();
        gameTimer.schedule(new TimerTask(){
            /**
             * The entire game updates every tick of the timer.
             * Every entity, player, tile, and level change is handled here.
             */
            @Override
            public void run() {
                /*
                  Checks for if the player has reached a new level via the newLv boolean.
                  Then deletes everything from the old level and then creates the new one.
                  Resets the players position and hitBox. As well as what portion of the level
                  the player is on.
                 */
                if(newLv){
                    level += 1;
                    newLv = false;
                    player.setX(65);
                    player.setY(512);
                    player.hitBox.x = 65;
                    player.hitBox.y = 512;
                    walls.clear();
                    slimes.clear();
                    spikes.clear();
                    bosses.clear();
                    bats.clear();
                    flagVisible = false;
                    screenX = 0;
                    screenY = 0;

                    //Checks what the new level is and then creates it.
                    if(level == 1){
                        makeLv1();
                    } else if(level == 2){
                        makeLv2();
                    } else if (level == 3){
                        makeLv3();
                    } else if (level == 4){
                        makeLv4();
                    } else if (level == 5){
                        makeLv5();
                    } else if (level == 6){
                        makeLv6();
                    } else if (level == 7){
                        makeLv7();
                    }
                }

                //Error removal. To keep from the player to glitch outside the map.
                if(screenX  < 0){
                    screenX = 0;
                }
                /*
                If the player goes off frame but is still on the same level. This updates the
                frame to delete everything that was in the old cell. And then add everything in teh new cell,
                Keeps the player y position the same but changes the x position.
                 */
                // If player goes to the right off the frame.
                if(player.x >= screenWidth - 32){
                    screenX ++;
                    player.x = 1;
                    player.hitBox.x = player.x;
                    walls.clear();
                    slimes.clear();
                    spikes.clear();
                    bosses.clear();
                    bats.clear();
                    if(level == 1){
                        makeLv1();
                    } else if(level == 2){
                        makeLv2();
                    } else if (level == 3){
                        makeLv3();
                    } else if( level == 4){
                        makeLv4();
                    } else if (level == 5){
                        makeLv5();
                    } else if (level == 6){
                        makeLv6();
                    } else if (level == 7){
                        makeLv7();
                    }
                    // Checks if the player walks off the left side of the frame
                } else if (player.x <= 0){
                    screenX --;
                    walls.clear();
                    slimes.clear();
                    spikes.clear();
                    bosses.clear();
                    bats.clear();
                    if(level == 1){
                        makeLv1();
                    } else if(level == 2){
                        makeLv2();
                    } else if (level == 3){
                        makeLv3();
                    } else if (level == 4){
                        makeLv4();
                    } else if (level == 5){
                        makeLv5();
                    } else if (level == 6){
                        makeLv6();
                    } else if (level == 7){
                        makeLv7();
                    }
                    player.x = screenWidth - 32;
                    player.hitBox.x = player.x;
                }
                if(player.y >= screenHeight -32){
                    screenY++;
                    walls.clear();
                    slimes.clear();
                    spikes.clear();
                    bosses.clear();
                    bats.clear();
                    if(level == 1){
                        makeLv1();
                    } else if(level == 2){
                        makeLv2();
                    } else if (level == 3){
                        makeLv3();
                    } else if (level == 4){
                        makeLv4();
                    } else if (level == 5){
                        makeLv5();
                    } else if (level == 6){
                        makeLv6();
                    } else if (level == 7){
                        makeLv7();
                    }
                    player.y = 1;
                    player.hitBox.y = player.y;
                }
                /*
                Updates all the slimes on teh current cell or level.
                Will skip if there are no slimes present
                Also in charge of removing the slimes from the ArrayList slimes if
                the slime is dead.
                 */
                if(!slimes.isEmpty()){
                    for(Slime slime : slimes){
                        slime.update();
                        if(!slime.alive && !slime.dying){
                            slimes.remove(slime);
                            break;
                        }
                    }
                }
                /*
                Updates all the Bats on the current cell or level
                Will skip if there are no bats present
                In charge of removing the bats that are dead from the ArrayList bats.
                 */
                if(!bats.isEmpty()){
                    for(Bat bat : bats){
                        bat.update();
                        if(!bat.alive && !bat.dying){
                            bats.remove(bat);
                            break;
                        }
                    }
                }
                /*
                In charge of updating the SlimeBoss on the current cell or level
                Will skip if there are no Slime bosses present
                In Charge of removing the boss when it is dead from teh game to avoid ghost collision.
                 */
                if(!bosses.isEmpty()) {
                    for (SlimeBoss boss : bosses) {
                        boss.update();
                        if (boss.hp <= 0 && !boss.dying && !boss.alive) {
                            bosses.remove(boss);
                            bossWin = true;
                            screenX++;
                            walls.clear();
                            slimes.clear();
                            spikes.clear();
                            bosses.clear();
                            bats.clear();
                            if(level == 1){
                                makeLv1();
                            } else if(level == 2){
                                makeLv2();
                            } else if (level == 3){
                                makeLv3();
                            } else if (level == 4){
                                makeLv4();
                            } else if (level == 5){
                                makeLv5();
                            } else if (level == 6){
                                makeLv6();
                            } else if (level == 7){
                                makeLv7();
                            }
                            break;
                        }
                    }
                }

                player.set();//The Player Update method
                if(player.healing){
                    if(heals.size() <= 30) {
                        if(count == 2 || count == 4) {
                            heals.add(new HealingParticle(rand.nextInt(player.x, player.x + player.width), player.y + player.height,
                                    rand.nextInt(2, 5)));
                        }
                    }
                }
                for(HealingParticle particle : heals){
                    particle.update();
                    if(particle.time >= 60){
                        heals.remove(particle);
                        break;
                    }
                }

                /*
                Checks for each slime if the slime is hit by the player
                Only when the player is attacking.
                 */
                if(player.attacking){
                    for(Slime slime : slimes){
                        slime.checkAlive(player);
                    }
                }

                /*
                This is where all the frames are updated.
                Because the frame length of certain animations is important to keep
                track of how long they are doing that thing. Ex.( the player is attacking during the attack animation
                but when it ends the player.attacking is set to false)
                the Frame update is controlled by the count variable.
                So while the rest of the game is running at 60 FPS
                the Animation part of the game is running at 12 FPS.
                 */
                if(count == 5){
                    player.nextFrame();

                    for(Slime slime: slimes){
                        slime.nextFrame();
                    }
                    for(Bat bat : bats){
                        bat.nextFrame();
                    }
                    for(SlimeBoss boss : bosses){
                        boss.nextFrame();
                        if(bosses.isEmpty()){
                            break;
                        }
                    }
                    keyCount++;
                    if(keyCount >= 10) {
                        keyPress = !keyPress;
                        keyCount = 0;
                    }
                    count = 0;
                }
                /*
                Checks if the is present on the level.
                If the flag is present of visible. It will check if teh player reaches the flag
                newLv becomes true if teh player touches the flag when it is visible
                else it remains at a default false;
                ONLY TIME that newLv is changed except for level creation.
                 */

                if(flagVisible){
                    newLv = flag.check(player.hitBox);
                }
                /*
                repaints the entire frame
                 */
                repaint();

                count++; // keeps track of when to animate

                /*
                Checks to see if the player is dead and has finished its dying animation.
                When the player has finished its dying animation it will paint the game over
                Then break the timer thus ending the game.
                or if the player beats the boss
                 */
                if(!player.alive && !player.dying){
                    running = false;
                    gameTimer.cancel();
                }
            }
        }, 0,1000/fps);

        repaint();
    }

    /**
     * This paints everything that is on the current frame
     * If it isn't on the current frame it doesn't exist and thus doesn't paint
     * Also called with the repaint() method.
     * Preconditions:
     *  - All the images must be loaded into the game with the loadImage() method
     *  - Requires said images to have the correct file path name and be present.
     * Post:
     *  - paints all the images of every tile, player, slime, bat, boss, spike, flag onto the background.
     * @param g  the <code>Graphics</code> context in which to paint
     */
    public void paint(Graphics g){
        super.paint(g);
        gtd = (Graphics2D) g;
        gtd.drawImage(background,0,0,screenWidth,screenHeight,null);
        if(running) {
            if(level == 1){
              if(screenX == 0){
                  gtd.setColor(Color.WHITE);
                  gtd.setFont(new Font("Apple Chancery",Font.BOLD, 31));
                  gtd.drawString("MOVE", 32*9 + 8,32*6);
                  gtd.drawString("MOVE", 32*9 + 7,32*6);
                  gtd.drawString("MOVE", 32*9 + 9,32*6);
                  gtd.drawString("MOVE", 32*9 + 6,32*6);
                  gtd.drawString("MOVE", 32*9 + 8,32*6 + 1);
                  gtd.drawString("MOVE", 32*9 + 8,32*6 - 1);
                  gtd.setColor(Color.BLACK);
                  gtd.setFont(new Font("Apple Chancery",Font.BOLD, 30));
                  gtd.drawString("MOVE", 32*9 + 8,32*6);

                  if(keyPress){
                      gtd.drawImage(aKey, 32 * 8 - 1, 32 * 7+7, 50, 50, null);
                      gtd.drawImage(dKey, 32 * 11 + 25, 32 * 7+7, 50, 50, null);
                  } else {
                      gtd.drawImage(aKey, 32 * 8 - 8, 32 * 7, 64, 64, null);
                      gtd.drawImage(dKey, 32 * 11 + 18, 32 * 7, 64, 64, null);
                  }
              } else if(screenX == 1){
                  gtd.setColor(Color.WHITE);
                  gtd.setFont(new Font("Apple Chancery",Font.BOLD, 31));
                  gtd.drawString("JUMP", 32*9 + 8,32*6);
                  gtd.drawString("JUMP", 32*9 + 7,32*6);
                  gtd.drawString("JUMP", 32*9 + 9,32*6);
                  gtd.drawString("JUMP", 32*9 + 6,32*6);
                  gtd.drawString("JUMP", 32*9 + 8,32*6 + 1);
                  gtd.drawString("JUMP", 32*9 + 8,32*6 - 1);
                  gtd.setColor(Color.BLACK);
                  gtd.setFont(new Font("Apple Chancery",Font.BOLD, 30));
                  gtd.drawString("JUMP", 32*9 + 8,32*6);

                  if(keyPress){
                      gtd.drawImage(wKey,32*9 + 25,32*7 + 7,50,50,null);
                  } else {
                      gtd.drawImage(wKey,32*9 + 18,32*7,64,64,null);
                  }
              } else if(screenX == 2){
                  gtd.setColor(Color.WHITE);
                  gtd.setFont(new Font("Apple Chancery",Font.BOLD, 30));
                  gtd.drawString("GO TO", 32*9 + 8,32*5);
                  gtd.drawString("GO TO", 32*9 + 7,32*5);
                  gtd.drawString("GO TO", 32*9 + 9,32*5);
                  gtd.drawString("GO TO", 32*9 + 6,32*5);
                  gtd.drawString("GO TO", 32*9 + 8,32*5 + 1);
                  gtd.drawString("GO TO", 32*9 + 8,32*5 - 1);
                  gtd.drawString("NEXT LEVEL", 32*8-12,32*7-12);
                  gtd.drawString("NEXT LEVEL", 32*8-11,32*7-12);
                  gtd.drawString("NEXT LEVEL", 32*8-13,32*7-12);
                  gtd.drawString("NEXT LEVEL", 32*8-12,32*7-11);
                  gtd.drawString("NEXT LEVEL", 32*8-12,32*7-13);
                  gtd.setColor(Color.BLACK);
                  gtd.setFont(new Font("Apple Chancery",Font.BOLD, 30));
                  gtd.drawString("GO TO", 32*9 + 8,32*5);
                  gtd.drawString("NEXT LEVEL", 32*8-12,32*7-12);
              }
            }
            else if (level == 2){
                if(screenX == 0){
                    gtd.setColor(Color.WHITE);
                    gtd.setFont(new Font("Apple Chancery",Font.BOLD, 30));
                    gtd.drawString("HOLD", 32*9 + 8,32*6);
                    gtd.drawString("HOLD", 32*9 + 7,32*6);
                    gtd.drawString("HOLD", 32*9 + 9,32*6);
                    gtd.drawString("HOLD", 32*9 + 6,32*6);
                    gtd.drawString("HOLD", 32*9 + 8,32*6 + 1);
                    gtd.drawString("HOLD", 32*9 + 8,32*6 - 1);
                    gtd.drawString("SPIKES!", 32*9, 32*5-12);
                    gtd.drawString("SPIKES!", 32*9 - 1, 32*5-12);
                    gtd.drawString("SPIKES!", 32*9 + 1, 32*5-12);
                    gtd.drawString("SPIKES!", 32*9, 32*5-11);
                    gtd.drawString("SPIKES!", 32*9, 32*5-13);
                    gtd.drawString("SPIKES!", 32*9 + 2, 32*5-12);
                    gtd.setColor(Color.BLACK);
                    gtd.setFont(new Font("Apple Chancery",Font.BOLD, 30));
                    gtd.drawString("HOLD", 32*9 + 8,32*6);
                    gtd.drawString("SPIKES!", 32*9, 32*5-12);

                    if(keyPress){
                        gtd.drawImage(wKey,32*9 + 27,32*7 + 7,50,50,null);
                    } else {
                        gtd.drawImage(wKey,32*9 + 20,32*7,64,64,null);
                    }
                }
                else if (screenX == 1){
                    gtd.setColor(Color.WHITE);
                    gtd.setFont(new Font("Apple Chancery",Font.BOLD, 30));
                    gtd.drawString("ATTACK", 32*8+20,32*6);
                    gtd.drawString("ATTACK", 32*8+19,32*6);
                    gtd.drawString("ATTACK", 32*8+21,32*6);
                    gtd.drawString("ATTACK", 32*8+22,32*6);
                    gtd.drawString("ATTACK", 32*8+20,32*6-1);
                    gtd.drawString("ATTACK", 32*8+20,32*6+1);

                    gtd.drawString("SLIME!", 32*9, 32*5-12);
                    gtd.drawString("SLIME!", 32*9 - 1, 32*5-12);
                    gtd.drawString("SLIME!", 32*9 + 1, 32*5-12);
                    gtd.drawString("SLIME!", 32*9, 32*5-11);
                    gtd.drawString("SLIME!", 32*9, 32*5-13);
                    gtd.drawString("SLIME!", 32*9 + 2, 32*5-12);
                    gtd.setColor(Color.BLACK);
                    gtd.setFont(new Font("Apple Chancery",Font.BOLD, 30));
                    gtd.drawString("ATTACK", 32*8+20 ,32*6);
                    gtd.drawString("SLIME!", 32*9, 32*5-12);

                    if(keyPress){
                        gtd.drawImage(spaceKey,32*9 +3,32*7 + 7,100,50,null);
                    } else {
                        gtd.drawImage(spaceKey,32*9 -9 ,32*7,128,64,null);
                    }

                }
                else if (screenX == 2){
                    gtd.setColor(Color.WHITE);
                    gtd.setFont(new Font("Apple Chancery",Font.BOLD, 30));
                    gtd.drawString("HOLD", 32*9 + 8,32*6);
                    gtd.drawString("HOLD", 32*9 + 7,32*6);
                    gtd.drawString("HOLD", 32*9 + 9,32*6);
                    gtd.drawString("HOLD", 32*9 + 6,32*6);
                    gtd.drawString("HOLD", 32*9 + 8,32*6 + 1);
                    gtd.drawString("HOLD", 32*9 + 8,32*6 - 1);
                    gtd.drawString("HEAL", 32*9+8, 32*5-12);
                    gtd.drawString("HEAL", 32*9 + 7, 32*5-12);
                    gtd.drawString("HEAL", 32*9 + 9, 32*5-12);
                    gtd.drawString("HEAL", 32*9+8, 32*5-11);
                    gtd.drawString("HEAL", 32*9+8, 32*5-13);
                    gtd.drawString("HEAL", 32*9 + 10, 32*5-12);
                    gtd.setColor(Color.BLACK);
                    gtd.setFont(new Font("Apple Chancery",Font.BOLD, 30));
                    gtd.drawString("HOLD", 32*9 + 8,32*6);
                    gtd.drawString("HEAL", 32*9+9, 32*5-12);

                    if(keyPress){
                        gtd.drawImage(sKey,32*9 + 27,32*7 + 7,50,50,null);
                    } else {
                        gtd.drawImage(sKey,32*9 + 20,32*7,64,64,null);
                    }
                }
            }
            else if (level == 4){
                if(screenX == 0){
                    gtd.setColor(Color.WHITE);
                    gtd.setFont(new Font("Apple Chancery",Font.BOLD, 31));
                    gtd.drawString("BAT!", 32*9 + 13,32*6);
                    gtd.drawString("BAT!", 32*9 + 12,32*6);
                    gtd.drawString("BAT!", 32*9 + 14,32*6);
                    gtd.drawString("BAT!", 32*9 + 11, 32*6);
                    gtd.drawString("BAT!", 32*9 + 13,32*6 + 1);
                    gtd.drawString("BAT!", 32*9 + 13,32*6 - 1);
                    gtd.setColor(Color.BLACK);
                    gtd.setFont(new Font("Apple Chancery",Font.BOLD, 30));
                    gtd.drawString("BAT!", 32*9 + 13,32*6);
                }
            }
            else if(level == 7){
                if(screenX == 2 && screenY == 1){
                    gtd.setColor(Color.BLACK);
                    gtd.setFont(new Font("Apple Chancery",Font.BOLD, 30));
                    gtd.drawString("YOU WIN!", 32*8+14,32*7-12);
                    gtd.drawString("YOU WIN!", 32*8+13,32*7-12);
                    gtd.drawString("YOU WIN!", 32*8+15,32*7-12);
                    gtd.drawString("YOU WIN!", 32*8+14,32*7-11);
                    gtd.drawString("YOU WIN!", 32*8+14,32*7-13);
                    gtd.setColor(Color.YELLOW);
                    gtd.setFont(new Font("Apple Chancery",Font.BOLD, 30));
                    gtd.drawString("YOU WIN!", 32*8+14,32*7-12);
                }
            }
            for (Wall wall : walls) {
                wall.draw(gtd);
            }
            player.draw(gtd);
            if (flagVisible) {
                flag.draw(gtd);
            }
            for(HealingParticle particle : heals){
                particle.draw(gtd);
            }
            for (Spike spike : spikes) {
                spike.draw(gtd);
            }
            for (Slime slime : slimes) {
                slime.draw(gtd);
            }
            for(Bat bat : bats){
                bat.draw(gtd);
            }
            for (SlimeBoss boss : bosses){
                boss.draw(gtd);
            }
        }else{
            gtd.setColor(Color.RED);
            gtd.setFont(new Font("TimesRoman",Font.BOLD, 30));
            gtd.drawString("GAME OVER", 230, 320);
        }
    }
    // Level Layouts

    /**
     * Makes Level  1 and has if conditions to check what part of level 1 the player is on via
     * screenX and screenY to keep track of what cell.
     * Also creates all the entities and tiles for that level and adds them to their corresponding lists.
     */
    public void makeLv1(){
        flagVisible = false;
        if(screenX == 0) {
            for (int i = 0; i < screenWidth; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < screenHeight -32; i += 32){
                walls.add(new Wall(0, i, blockWidth, blockHeight, brickImage));
            }
        } else if (screenX == 1){
            for (int i = 0; i < screenWidth; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 11; i ++){
                walls.add(new Wall(screenWidth-32, 32 * (i + 9), blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(screenWidth - 32, 0, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(screenWidth - 32, 32*1, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(screenWidth - 32, 32*2, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(screenWidth - 32, 32*3, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*4, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*5, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*7, 32*15, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*8, 32*15, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*10, 32*13, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*11, 32*13, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*14, 32*11, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*15, 32*11, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*9, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*9, blockWidth, blockHeight, brickImage));

        } else if(screenX == 2){
            flagVisible = true;
            for (int i = 0; i < screenWidth; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 19; i ++){
                walls.add(new Wall(screenWidth-32, 32 * i , blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 11; i ++){
                walls.add(new Wall(0, 32 * (i + 9), blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 6; i++){
                walls.add(new Wall(32*(i+3),32*8, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*3,32*(i+3), blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 4; i++){
                walls.add(new Wall(32 * i, 32*3, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(0, 0, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(0, 32*1, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(0, 32*2, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(0, 32*3, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*11, 32*10, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*12, 32*10, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*14, 32*12, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*15, 32*12, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*14, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*14, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*15, 32*16, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*16, 32*16, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*11, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*12, 32*18, blockWidth, blockHeight, brickImage));

            flag = new Flag(32*5, 32*6, blockWidth, 2*blockHeight, flagImage);
        }

    }

    /**
     * Makes Level 2 and has if conditions to check what part of level 2 the player is on via
     * screenX and screenY to keep track of what cell.
     * Also creates all the entities and tiles for that level and adds them to their corresponding lists.
     */
    public void makeLv2(){
        flagVisible = false;
        if(screenX == 0) {
            for (int i = 0; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < screenHeight -32; i += 32){
                walls.add(new Wall(0, i, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32*7, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*15, 32*18,blockWidth, blockHeight, brickImage));
            for(int i = 0; i < 7; i++){
                spikes.add(new Spike(32*(i+8), 32*18,  blockWidth, blockHeight, spikeImage));
            }
        }
        else if (screenX == 1) {
            for (int i = -32; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            Slime s = new Slime(32*14,32*18,this);
            s.setFaceRight(true);
            slimes.add(s);
        }
        else if  (screenX == 2){
            flagVisible = true;
            for (int i = 0; i < screenWidth; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 20; i++){
                walls.add(new Wall(screenWidth -32, 32*i,blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32*7, 32*18, blockWidth, blockHeight, brickImage));
            for(int i = 0; i < 10; i++){
                spikes.add(new Spike(32*(i+8), 32*18, blockWidth, blockHeight, spikeImage));
            }
            walls.add(new Wall(32*16, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*17, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*16, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*16, blockWidth, blockHeight, brickImage));
            flag = new Flag(32*19,32*14, blockWidth, 2*blockHeight, flagImage);
        }

    }
    /**
     * Makes Level 3 and has if conditions to check what part of level 3 the player is on via
     * screenX and screenY to keep track of what cell.
     * Also creates all the entities and tiles for that level and adds them to their corresponding lists.
     */
    public void makeLv3(){
        flagVisible = false;
        if(screenX == 0) {
            for (int i = 0; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < screenHeight -32; i += 32){
                walls.add(new Wall(0, i, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 15; i++){
                walls.add(new Wall(32*20, 32*(i+4), blockWidth, blockHeight, brickImage));
            }
            spikes.add(new Spike(32*7, 32*18, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*8,32*18, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*9, 32*18, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*10, 32*18, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*18, 32*18, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*17, 32*18, blockWidth, blockHeight, spikeImage));

            walls.add(new Wall(32*16, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*16, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*13, 32*15, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*14, 32*15, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*6, 32*13, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*7, 32*13, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*1, 32*11, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*2, 32*11, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*6, 32*9, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*11, 32*8, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*15, 32*6, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*4, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*4, blockWidth, blockHeight, brickImage));
        }
        if (screenX == 1) {
            for (int i = -32; i < screenWidth + 32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i< 16; i++){
                walls.add(new Wall(32*20,32*i, blockWidth, blockHeight, brickImage));
            }
            for (int i = 0; i < 15; i++) {
                walls.add(new Wall(0, 32 * (i + 4), blockWidth, blockHeight, brickImage));
            }
            for (int i = 0; i < 5; i++) {
                walls.add(new Wall(32 * (i), 32 * 4, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*i, 32*15, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*(i+4), 32*5, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*(i+10), 32*5, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 15; i++){
                walls.add(new Wall(32*(i+5), 32*9, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 6; i++){
                walls.add(new Wall(32*9, 32*(i+10), blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32*8, 32*4, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*9, 32*4, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*4, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*10, 32*4, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*14, 32*4, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*15, 32*4, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*16, 32*4, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*1, 32*9, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*1, 32*14, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*4, 32*14, blockWidth, blockHeight, brickImage));

            for(int i = 0; i < 3; i++){
                spikes.add(new Spike(32*(i+5), 32*4, blockWidth, blockHeight, spikeImage));
                spikes.add(new Spike(32*(i+1), 32*5, blockWidth, -blockHeight, spikeImage));
                spikes.add(new Spike(32*(i+11), 32*4, blockWidth, blockHeight, spikeImage));
            }
            spikes.add(new Spike(32*9, 32*5, blockWidth, -blockHeight, spikeImage));
            spikes.add(new Spike(32*15, 32*5, blockWidth, -blockHeight, spikeImage));
            spikes.add(new Spike(32*16, 32*5, blockWidth, -blockHeight, spikeImage));
            spikes.add(new Spike(32*2, 32*14, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*3, 32*14, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*9, 32*16, blockWidth, -blockHeight, spikeImage));

            slimes.add(new Slime(32*6, 32*8, this));
            slimes.add(new Slime(32*2, 32*18, this));
            slimes.add(new Slime(32*13, 32*18, this));
        }
        if (screenX == 2){
            flagVisible = true;
            for (int i = -32; i < screenWidth + 32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < screenHeight -32; i += 32){
                walls.add(new Wall(32*20, i, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i< 16; i++){
                walls.add(new Wall(0,32*i, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32*17, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*16, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*14, 32*14, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*15, 32*14, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*12, 32*12, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*10, 32*10, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*11, 32*10, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*17, 32*8, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*8, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*8, blockWidth, blockHeight, brickImage));

            walls.add(new Wall(32*10, 32*4, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*11, 32*4, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*12, 32*4, blockWidth, blockHeight, brickImage));

            spikes.add(new Spike(32*6, 32*18, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*7, 32*18, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*8, 32*18, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*12, 32*5, blockWidth, -blockHeight, spikeImage));

            slimes.add(new Slime(32*11, 32*3, this));
            slimes.add(new Slime(32*18, 32*7, this));
            for(Slime slime : slimes){
                slime.setFaceRight(false);
            }

            flag = new Flag(32*19, 32*6, blockWidth, 2*blockHeight, flagImage);
        }
    }
    /**
     * Makes Level 4 and has if conditions to check what part of level 4 the player is on via
     * screenX and screenY to keep track of what cell.
     * Also creates all the entities and tiles for that level and adds them to their corresponding lists.
     */
    public void makeLv4(){
        flagVisible = false;
        if(screenX == 0){
            for (int i = 0; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < screenHeight -32; i += 32){
                walls.add(new Wall(0, i, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 16; i++){
                walls.add(new Wall(32*20, 32*i, blockWidth, blockHeight, brickImage));
            }
            for(int i=0; i < 8; i++){
                walls.add(new Wall(32*(i+1), 32*10, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*(i+12), 32*10, blockWidth, blockHeight, brickImage));
            }
            bats.add(new Bat(32*11, 32*7, this));
        }
        else if(screenX == 1){
            for (int i = 0; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 16; i++){
                walls.add(new Wall(0, 32*i, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*20, 32*(i+4), blockWidth, blockHeight, brickImage));
            }
            for(int i =0; i < 8; i++){
                walls.add(new Wall(32*(i+1), 32*13, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 11; i++){
                walls.add(new Wall(32*(i+9), 32*7, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32*17, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*12, 32*15, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*13, 32*15, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*1, 32*12, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*2, 32*12, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*1, 32*11, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*2, 32*11, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*1, 32*10, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*1, 32*9, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*4, 32*7, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*5, 32*7, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*6, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*6, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*5, blockWidth, blockHeight, brickImage));

            slimes.add(new Slime(32*17, 32*18, this));
            bats.add(new Bat(32*4, 32*10, this));
            bats.add(new Bat(32*12, 32*3, this));
        }
        else if(screenX == 2){
            flagVisible = true;
            for (int i = 0; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 16; i++){
                walls.add(new Wall(0, 32*(i+4), blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < screenHeight-32; i+=32){
                walls.add(new Wall(32*20, i, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32*1, 32*4, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*2, 32*4, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*5, 0, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*5, 32, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*5, 32*2, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*5, 32*3, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*5, 32*4, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*17, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*18, blockWidth, blockHeight, brickImage));

            flag = new Flag(32*18, 32*16, blockWidth, 2*blockHeight, flagImage);

            bats.add(new Bat(32*10, 32*4, this));
            bats.add(new Bat(32*16, 32*8, this));
            bats.add(new Bat(32*3, 32*12, this));
            bats.add(new Bat(32*8, 32*16, this));
        }

    }
    /**
     * Makes Level 5 and has if conditions to check what part of level 5 the player is on via
     * screenX and screenY to keep track of what cell.
     * Also creates all the entities and tiles for that level and adds them to their corresponding lists.
     */
    public void makeLv5(){
        flagVisible = false;
        if(screenX == 0){
            for (int i = 0; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(i, -32, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < screenHeight -32; i += 32){
                walls.add(new Wall(0, i, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 14; i++){
                walls.add(new Wall(32*20, 32*(i+6), blockWidth,blockHeight,brickImage));
            }
            for(int i = 0; i < 3; i++) {
                walls.add(new Wall(32 * 20, 32*i, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 16; i++){
                spikes.add(new Spike(32*(i+4), 32*18, blockWidth, blockHeight, spikeImage));
            }
            walls.add(new Wall(32*6, 32*17, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*7, 32*17, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*8, 32*17, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*12, 32*15, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*13, 32*15, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*17, 32*13, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*18, 32*13, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*19, 32*13, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*19, 32*12, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*19, 32*11, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*12, 32*10, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*13, 32*10, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*14, 32*10, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*7, 32*9, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*8, 32*9, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32, 32*8, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*2, 32*8, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*3, 32*8, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32, 32*7, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*8, 32*5, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*13, 32*6, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*14, 32*6, blockWidth,blockHeight,brickImage));
            walls.add(new Wall(32*19, 32*6, blockWidth,blockHeight,brickImage));

            bats.add(new Bat(32*4, 32*3,this));
        }
        else if(screenX == 1){
            for (int i = 0; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 3; i++) {
                walls.add(new Wall(0, 32*i, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*20, 32*i, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 14; i++){
                walls.add(new Wall(0, 32*(i+6), blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 7; i++){
                walls.add(new Wall(32*20, 32*(i+6), blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 17; i++){
                spikes.add(new Spike(32*(i+4), 32*18, blockWidth, blockHeight, spikeImage));
            }
            walls.add(new Wall(32*19, 32*6, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*3, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32, 32*16, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*3, 32*14, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32, 32*12, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*3, 32*10, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32, 32*8, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*5, 32*6, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*10, 32*7, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*15, 32*6, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*20, 32*17+16, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*6, 32*17+16, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*11, 32*17+16, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*17, 32*17+16, blockWidth, blockHeight/2, brickImage));

            bats.add(new Bat(32*9, 32*3, this));
        }
        else if(screenX == 2){
            for (int i = 0; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 3; i++) {
                walls.add(new Wall(0, 32*i, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 7; i++){
                walls.add(new Wall(0, 32*(i+6), blockWidth, blockHeight, brickImage));
            }
            for(int i = 0;i < screenHeight - 32; i+=32){
                walls.add(new Wall(32*20, i, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 20; i++){
                spikes.add(new Spike(32*(i), 32*18, blockWidth, blockHeight, spikeImage));
            }
            walls.add(new Wall(0, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32, 32*6, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*4, 32*17+16, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*9, 32*17+16, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*14, 32*17+16, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*19, 32*17+16, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*7, 32*12, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*11, 32*10, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*15, 32*8, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*17, 32*6, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*6, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*6, blockWidth, blockHeight, brickImage));

            bats.add(new Bat(32*12, 32*3, this));
            bats.add(new Bat(32*6, 32*16, this));

            flagVisible = true;
            flag = new Flag(32*18, 32*4, blockWidth, 2*blockHeight, flagImage);
        }
    }
    /**
     * Makes Level 6 and has if conditions to check what part of level 6 the player is on via
     * screenX and screenY to keep track of what cell.
     * Also creates all the entities and tiles for that level and adds them to their corresponding lists.
     */
    public void makeLv6(){
        flagVisible =  false;
        if(screenX == 0){
            for (int i = 0; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(i, -32, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(i,32*11, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < screenHeight -32; i += 32){
                walls.add(new Wall(0, i, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32*1,32*15, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*3,32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*3,32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*5,32*15, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*6,32*15, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*6,32*16, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*7,32*16, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*8,32*16, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*9,32*16, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*9,32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*9,32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*10,32*18, blockWidth, blockHeight, brickImage));
            spikes.add(new Spike(32*7,32*15, blockWidth, blockHeight, spikeImage));
            for(int i = 0; i<9; i++){
                walls.add(new Wall(32*(i+12), 32*15, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 12; i++){
                walls.add(new Wall(32*(i+9), 32*7, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32*9,32*8, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*9,32*9, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*9,32*10, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*13,32*6, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*13,32*5, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*13,32*4, blockWidth, blockHeight, brickImage));
            spikes.add(new Spike(32*13,32*3, blockWidth, blockHeight, spikeImage));
            walls.add(new Wall(32*12,32*5, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*14,32*5, blockWidth, blockHeight/2, brickImage));
            for(int i = 0; i < 4; i++){
                walls.add(new Wall(32*(i+16), 32*3, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*(i+6),32*3, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*8,32*i, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*6,32*(i+4), blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*i, 32*3, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32*20,32*3, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*17,32*2, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*17,32, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*17,0, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32,32*9+16, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*3,32*7+16, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*5,32*5+16, blockWidth, blockHeight/2, brickImage));

            spikes.add(new Spike(32*6,32*8, blockWidth, -blockHeight, spikeImage));
            spikes.add(new Spike(32*13, 32*16, blockWidth, -blockHeight, spikeImage));
            spikes.add(new Spike(32*14, 32*16, blockWidth, -blockHeight, spikeImage));
            slimes.add(new Slime(32*12, 32*10, this));
            slimes.add(new Slime(32*4, 32*10, this));
            flagVisible = true;
            flag = new Flag(32*2, 32, blockWidth, 2*blockHeight, flagImage);
        }
        else if (screenX == 1){
            for (int i = 0; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight-32, blockWidth, blockHeight, brickImage));
            }
            for(int i =0; i < 8; i++){
                walls.add(new Wall(32*i, 32*19, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32*11, 32*19, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*12, 32*19, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*13, 32*19, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*17, 32*19, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 32*19, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*19, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*20, 32*19, blockWidth, blockHeight, brickImage));
            spikes.add(new Spike(32*8, 32*19, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*9, 32*19, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*10, 32*19, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*14, 32*19, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*15, 32*19, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*16, 32*19, blockWidth, blockHeight, spikeImage));
            for(int i = 0; i < 4; i++){
                walls.add(new Wall(32*i, 32*15, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*3, 32*(i+15), blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*20, 32*(i+11), blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*7, 32*(i+11), blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*6, 32*(i+3), blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32*5, 32*7, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*6, 32*7, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*20, 32*15, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(0, 32*3, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32, 32*3, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(0, 32*7, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32, 32*7, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(0, 32*11, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32, 32*11, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*6, 32*13, blockWidth, blockHeight, brickImage));
            for(int i =0; i < 16; i++){
                walls.add(new Wall(32*(i+6), 32*3, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 9; i++){
                walls.add(new Wall(32*(i+7), 32*15, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32*4, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*4, 32*17, blockWidth, blockHeight, brickImage));

            walls.add(new Wall(32*11, 32*11, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*12, 32*11, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*13, 32*11, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*12, 32*8, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*16, 32*11, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*16, 32*10, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*16, 32*6+16, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*17, 32*6+16, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*20, 32*7, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*2, 32*3, blockWidth/6, blockHeight, brickImage));
            walls.add(new Wall(32*6-(32/6), 32*3, blockWidth/6, blockHeight, brickImage));
            spikes.add(new Spike(32*8, 32*14, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*9, 32*14, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*10, 32*14, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*14, 32*14, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*15, 32*14, blockWidth, blockHeight, spikeImage));

            bats.add(new Bat(32*8, 32*6, this));
            slimes.add(new Slime(32*8, 32*2, this));

        }

        else if (screenX == 2){
            for (int i = 0; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 32, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < screenHeight -32; i += 32){
                walls.add(new Wall(32*20, i, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 16; i++){
                walls.add(new Wall(32*i, 32*15, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*i, 32*3, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 8; i++){
                walls.add(new Wall(32*(i+13), 32*19, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*15, 32*(i+4), blockWidth, blockHeight, brickImage));
                spikes.add(new Spike(32*(i+1), 32*14, blockWidth, blockHeight,spikeImage));
            }
            for(int i = 0; i < 5; i++){
                walls.add(new Wall(32*(i), 32*19, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 4; i++){
                walls.add(new Wall(32*(i+7), 32*19, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(0, 32*(i+11), blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*(i-1), 32*7, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32, 32*11, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*15, 32*18, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*15, 32*17, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*15, 32*16, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*2, 32*8, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*3, 32*8, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*4, 32*8, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*5, 32*8, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*5, 32*7, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*6, 32*7, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*7, 32*7, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*7, 32*8, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*8, 32*8, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*9, 32*8, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*10, 32*8, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*10, 32*7, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*11, 32*7, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*19, 32*17, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*16, 32*15, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*19, 32*13, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*16, 32*11, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*19, 32*9, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*16, 32*7, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*19, 32*5, blockWidth, blockHeight/2, brickImage));
            walls.add(new Wall(32*16, 32*3, blockWidth, blockHeight/2, brickImage));

            spikes.add(new Spike(32*3, 32*7, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*4, 32*7, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*8, 32*7, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*9, 32*7, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*4, 32*2, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*5, 32*2, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*8, 32*2, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*9, 32*2, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*5, 32*19, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*6, 32*19, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*11, 32*19, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*12, 32*19, blockWidth, blockHeight, spikeImage));
            spikes.add(new Spike(32*2, 32*16, blockWidth, -blockHeight, spikeImage));
            spikes.add(new Spike(32*3, 32*16, blockWidth, -blockHeight, spikeImage));
            spikes.add(new Spike(32*8, 32*16, blockWidth, -blockHeight, spikeImage));
            spikes.add(new Spike(32*9, 32*16, blockWidth, -blockHeight, spikeImage));

            bats.add(new Bat(32*8, 32*9, this));
        }
    }
    /**
     * Makes Level 7 and has if conditions to check what part of level 7 the player is on via
     * screenX and screenY to keep track of what cell.
     * Also creates all the entities and tiles for that level and adds them to their corresponding lists.
     * This level is the bossFight.
     */
    public void makeLv7(){
        if(screenX == 0 && screenY == 0){
            for(int i = 0; i < 8; i++){
                walls.add(new Wall(32*i, screenHeight-64, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*(i+13), screenHeight -64, blockWidth,blockHeight,brickImage));
            }
            for(int i = 0; i < 20; i++){
                walls.add(new Wall(0, 32*i, blockWidth, blockHeight,brickImage));
                walls.add(new Wall(32*20, 32*i, blockWidth, blockHeight, brickImage));
            }
        }
        else if(screenX == 0 && screenY == 1){
            for(int i = 0; i < 8; i++){
                walls.add(new Wall(32*i, 0, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*(i+13), 0, blockWidth,blockHeight,brickImage));
            }
            for(int i = 0; i < screenWidth; i+= 32){
                walls.add(new Wall(i, screenHeight-64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 20; i++){
                walls.add(new Wall(0, 32*i, blockWidth, blockHeight,brickImage));
                walls.add(new Wall(32*20, 32*i, blockWidth, blockHeight, brickImage));
            }
            bosses.add(new SlimeBoss(32*16, 32*16, this));

        } else if(screenX == 1 && screenY == 1){
            for(int i = 0; i < 8; i++){
                walls.add(new Wall(32*i, 0, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*(i+13), 0, blockWidth,blockHeight,brickImage));
            }
            for(int i = 0; i < screenWidth; i+= 32){
                walls.add(new Wall(i, screenHeight-64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 20; i++){
                walls.add(new Wall(0, 32*i, blockWidth, blockHeight,brickImage));
            }
            for(int i = 0; i < 15; i++){
                walls.add(new Wall(32*20, 32*i, blockWidth, blockHeight,brickImage));
            }
        }
        else if(screenX == 2 && screenY == 1){
            for(int i = 0; i < screenWidth; i+= 32){
                walls.add(new Wall(i, screenHeight-64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 20; i++){
                walls.add(new Wall(32*20, 32*i, blockWidth, blockHeight,brickImage));
            }
            for(int i = 0; i < 15; i++){
                walls.add(new Wall(0, 32*i, blockWidth, blockHeight,brickImage));
            }
        }
    }


    /**
     * Loads all the images for every single entity, tile, flag,background, spike.
     * Surrounded in a try catch to analyze exceptions and prevent total crash out if it fails.
     * If it does fail the game will stop.
     * Entities such as the Bat, Slime, and the Slime Boss. Their load images are prebuilt in their
     * constructors also surrounded by a try catch.
     * Preconditions:
     *  - All images must be in the correct filepath and with the correct name
     *  - all images must be present
     *  - All images must be in either .png or .jpeg and in the Resources directory.
     *  Post conditions:
     *    - assigns images to their correct storage value for animation and drawing.
     */

    public void loadImages(){
        try{
            background = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/castleHallwayBackground.png"));
            player.loadPlayerImages();
            flagImage = ImageIO.read(getClass().getResourceAsStream("/door.png"));
            brickImage = ImageIO.read(getClass().getResourceAsStream("/brickTile.jpg"));
            spikeImage = ImageIO.read(getClass().getResourceAsStream("/spikes.png"));
            BufferedImage WASD = ImageIO.read(getClass().getResourceAsStream("/wasdkeys.png"));
            wKey = WASD.getSubimage(20,30,350,350);
            dKey = WASD.getSubimage(400,400,350,350);
            aKey = WASD.getSubimage(400,30,350,350);
            sKey = WASD.getSubimage(20,400,350,350);
            spaceKey = ImageIO.read(getClass().getResourceAsStream("/spacebarKey.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Action performed event that is overridden by the KeyChecker.
     * Not Extremely used but allows the event to be processed by the
     * keyPressed() and keyReleased()
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
    }

    /**
     * This checks the keyEvent to see what key was pressed.
     * Then if it is one of the player's keybindings it will update the player's key boolean for that key
     * Will also make sure that the space button is pressed but not held via the player.spacePressed boolean.
     * @param e the KeyEvent that the user pressed
     */
    public void keyPressed(KeyEvent e) {
        if(e.getKeyChar() == 'a') player.keyLeft = true;
        if(e.getKeyChar() == 'd') player.keyRight = true;
        if(e.getKeyChar() == 'w') player.keyUp = true;
        if(e.getKeyChar() == 's') player.keyDown = true;
        if(e.getKeyChar() == KeyEvent.VK_SPACE) {
            if(!player.spacePressed){
                player.keySpace = true;
                player.spacePressed = true;
            } else {
                player.keySpace = false;
            }
        }
    }
    /**
     * This checks the keyEvent to see what key was Released.
     * Then if it is one of the player's keybindings it will update the player's key boolean for that key
     * Will also make sure that the space button is released to allow variable jump height.
     * @param e the KeyEvent that the user released
     */
    public void keyReleased(KeyEvent e) {
        if(e.getKeyChar() == 'a') player.keyLeft = false;
        if(e.getKeyChar() == 'd') player.keyRight = false;
        if(e.getKeyChar() == 'w') player.keyUp = false;
        if(e.getKeyChar() == 's') player.keyDown = false;
        if(e.getKeyChar() == KeyEvent.VK_SPACE){
            player.keySpace = false;
            player.spacePressed = false;
        }
    }


}
