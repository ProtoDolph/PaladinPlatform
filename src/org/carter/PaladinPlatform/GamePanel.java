package org.carter.PaladinPlatform;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GamePanel extends javax.swing.JPanel implements ActionListener {

    boolean running;
    //player
    Player player;
    //dimensions
    int screenWidth = 640;
    int screenHeight = 640;

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
    //flag
    Flag flag;
    BufferedImage flagImage;
    boolean flagVisible;
    //background
    BufferedImage background;
    //Spike
    ArrayList<Spike> spikes = new ArrayList<>();
    BufferedImage spikeImage;
    ArrayList<SlimeBoss> bosses = new ArrayList<>();

    //performance
    int fps = 60;
    Timer gameTimer;
    int count;

    public GamePanel(){

        player = new Player(65,512,this);

        loadImages();
        level = 1;
        makeLv1();

        running = true;
        gameTimer = new Timer();
        gameTimer.schedule(new TimerTask(){
            @Override
            public void run() {
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
                    flagVisible = false;
                    screenX = 0;
                    screenY = 0;

                    if(level == 1){
                        makeLv1();
                    } else if(level == 2){
                        makeLv2();
                    } else if (level == 3){
                        makeLv3();
                    } else if (level == 4){
                        makeLv4();
                    }
                }

                if(screenX  < 0){
                    screenX = 0;
                }
                //level setter
                if(player.x >= screenWidth - 32){
                    screenX ++;
                    player.x = 1;
                    player.hitBox.x = player.x;
                    walls.clear();
                    slimes.clear();
                    spikes.clear();
                    bosses.clear();
                    if(level == 1){
                        makeLv1();
                    } else if(level == 2){
                        makeLv2();
                    } else if (level == 3){
                        makeLv3();
                    } else if( level == 4){
                        makeLv4();
                    }
                } else if (player.x <= 0){
                    screenX --;
                    walls.clear();
                    slimes.clear();
                    spikes.clear();
                    bosses.clear();
                    if(level == 1){
                        makeLv1();
                    } else if(level == 2){
                        makeLv2();
                    } else if (level == 3){
                        makeLv3();
                    } else if (level == 4){
                        makeLv4();
                    }
                    player.x = screenWidth - 32;
                    player.hitBox.x = player.x;
                }
                if(!slimes.isEmpty()){
                    for(Slime slime : slimes){
                        slime.update();
                        if(!slime.alive && !slime.dying){
                            slimes.remove(slime);
                            break;
                        }
                    }
                }
                for(SlimeBoss boss : bosses){
                    boss.update();
                    if(boss.hp <= 0 && !boss.dying && !boss.alive){
                        bosses.remove(boss);
                        break;
                    }
                }
                player.set();
                if(player.attacking){
                    for(Slime slime : slimes){
                        slime.checkAlive(player);
                    }
                }

                if(count == 5){
                    player.nextFrame();
                    for(Slime slime: slimes){
                        slime.nextFrame();
                    }
                    for(SlimeBoss boss : bosses){
                        boss.nextFrame();
                        if(bosses.isEmpty()){
                            break;
                        }
                    }
                    count = 0;
                }

                if(flagVisible){
                    newLv = flag.check(player.hitBox);
                }
                repaint();
                count++;
                if(!player.alive && !player.dying){
                    gameTimer.cancel();
                    running = false;
                }
            }
        }, 0,1000/fps);
        repaint();
    }


    public void paint(Graphics g){
        super.paint(g);
        gtd = (Graphics2D) g;
        gtd.drawImage(background,0,0,screenWidth,screenHeight,null);
        if(running) {

            for (Wall wall : walls) {
                wall.draw(gtd);
            }
            player.draw(gtd);
            if (flagVisible) {
                flag.draw(gtd);
            }
            for (Spike spike : spikes) {
                spike.draw(gtd);
            }
            for (Slime slime : slimes) {
                slime.draw(gtd);
            }
            for (SlimeBoss boss : bosses){
                boss.draw(gtd);
            }
        } else{
            gtd.setColor(Color.RED);
            gtd.setFont(new Font("TimesRoman",Font.BOLD, 30));
            gtd.drawString("GAME OVER", 230, 320);
        }
    }
    // Level Layouts
    public void makeLv1(){
        flagVisible = false;
        if(screenX == 0) {
            for (int i = 0; i < screenWidth; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32, screenHeight - 96, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32, screenHeight - (32 * 4), blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32, screenHeight - (32 * 5), blockWidth, blockHeight, brickImage));
        }
        if (screenX == 1){
            flagVisible = true;
            for (int i = 0; i < screenWidth; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(screenWidth -64, screenHeight - 96, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(screenWidth - 64, screenHeight - (32 * 4), blockWidth, blockHeight, brickImage));
            walls.add(new Wall(screenWidth -64, screenHeight - (32*5), blockWidth, blockHeight, brickImage));
            flag = new Flag(screenWidth - 96, screenHeight - 96, blockWidth, blockHeight, flagImage);
            spikes.add(new Spike(320,544,blockWidth,blockHeight,spikeImage));
            spikes.add(new Spike(352,544,blockWidth,blockHeight,spikeImage));

        }
    }
    public void makeLv2(){
        flagVisible = false;
        if(screenX == 0) {
            for (int i = -32; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(32, screenHeight - 96, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32, screenHeight - (32 * 4), blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32, screenHeight - (32 * 5), blockWidth, blockHeight, brickImage));
            slimes.add(new Slime(320,512,this));

        }
        if (screenX == 1) {
            flagVisible = true;
            for (int i = -32; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(screenWidth - 64, screenHeight - 96, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(screenWidth - 64, screenHeight - (32 * 4), blockWidth, blockHeight, brickImage));
            walls.add(new Wall(screenWidth - 64, screenHeight - (32 * 5), blockWidth, blockHeight, brickImage));
            slimes.add(new Slime(320,512,this));
            flag = new Flag(screenWidth - 96, screenHeight - 96, blockWidth, blockHeight, flagImage);
            spikes.add(new Spike(320,544,blockWidth,blockHeight,spikeImage));
            spikes.add(new Spike(288,544,blockWidth,blockHeight,spikeImage));
        }
    }
    public void makeLv3(){
        flagVisible = false;
        if(screenX == 0) {
            for (int i = 0; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 128; i < screenHeight-64; i +=32){
                walls.add(new Wall(0, i, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(screenWidth-32, i, blockWidth, blockHeight,brickImage));
            }
            for(int i = 0; i < 6; i += 1){
                walls.add(new Wall(32*i, screenHeight - 160,blockWidth,blockHeight, brickImage));
                walls.add(new Wall(32*(i+9), screenHeight-160, blockWidth, blockHeight, brickImage));
            }
            walls.add(new Wall(544,screenHeight - 96, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(576,screenHeight-96, blockWidth,blockHeight, brickImage));
            walls.add(new Wall(576,screenHeight-128, blockWidth,blockHeight, brickImage));
            walls.add(new Wall(576,screenHeight-160, blockWidth,blockHeight, brickImage));
            walls.add(new Wall(544,screenHeight-128, blockWidth,blockHeight, brickImage));
            walls.add(new Wall(512,screenHeight - 96, blockWidth, blockHeight, brickImage));

            walls.add(new Wall(32, 448, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32, 416, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(64, 448, blockWidth, blockHeight, brickImage));

            for(int i =0; i < 5; i++){
                walls.add(new Wall(32*i + 96,352,blockWidth, blockHeight, brickImage));
            }

            for(int i=0; i<4; i++){
                walls.add(new Wall(32*(i+15),352,blockWidth,blockHeight,brickImage));
            }
            walls.add(new Wall(32*18,320, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18, 288, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*17, 320, blockWidth, blockHeight, brickImage));
            walls.add(new Wall(32*18,288-32, blockWidth, blockHeight, brickImage));
            for(int i = 0; i<2; i++){
                walls.add(new Wall(32 *(i+14), 32*6, blockWidth, blockHeight, brickImage));
            }

            slimes.add(new Slime(96,448,this));


        }
        if (screenX == 1) {
            for (int i = 0; i < screenWidth+32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 128; i < screenHeight-64; i +=32){
                walls.add(new Wall(0, i, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 11; i++){
                walls.add(new Wall(screenWidth -32,32*(i+4), blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 19; i++) {
                walls.add(new Wall(32*i, 32 * 4, blockWidth, blockHeight, brickImage));
            }
            flagVisible = true;
            flag = new Flag(96, screenHeight - 96, blockWidth, blockHeight, flagImage);
        }
        if (screenX == 2){
            flagVisible = false;
            for (int i = 0; i < screenWidth; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < screenHeight-32; i +=32){
                walls.add(new Wall(screenWidth-32, i, blockWidth, blockHeight,brickImage));
            }
            for(int i = 0; i < 11; i++){
                walls.add(new Wall(0, 32 * (4+i), blockWidth, blockHeight, brickImage));
            }
            for(int i = 0; i < 16; i ++) {
                walls.add(new Wall(32*(i+1), 32 * 4, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*(i+3), 32*8, blockWidth, blockHeight, brickImage));
                walls.add(new Wall(32*(i+1), 32*12, blockWidth, blockHeight, brickImage));
                spikes.add(new Spike(32*(i+3), 32*17,blockWidth, blockHeight, spikeImage));
            }
            walls.add(new Wall(32*18,32*16,blockWidth, blockHeight,brickImage));
            walls.add(new Wall(32*17,32*16,blockWidth, blockHeight,brickImage));
            walls.add(new Wall(32*13,32*16,blockWidth, blockHeight,brickImage));
            walls.add(new Wall(32*12,32*16,blockWidth, blockHeight,brickImage));
            walls.add(new Wall(32*8,32*16,blockWidth, blockHeight,brickImage));
            walls.add(new Wall(32*7,32*16,blockWidth, blockHeight,brickImage));
            walls.add(new Wall(32*3,32*16,blockWidth, blockHeight,brickImage));
            walls.add(new Wall(32*2,32*17,blockWidth, blockHeight,brickImage));
        }
    }
    public void makeLv4(){
        flagVisible = false;
        if(screenX == 0) {
            for (int i = 0; i < screenWidth + 32; i += 32) {
                walls.add(new Wall(i, screenHeight - 64, blockWidth, blockHeight, brickImage));
            }
            for (int i = 0; i < screenHeight-32; i += 32) {
                walls.add(new Wall(0, i, blockWidth, blockHeight, brickImage));
            }
            for (int i = 0; i < 19; i++) {
                walls.add(new Wall(screenWidth - 32, 32*i, blockWidth, blockHeight, brickImage));
            }
            for (int i = 0; i < 19; i++) {
                walls.add(new Wall(32 * i, 0, blockWidth, blockHeight, brickImage));
            }
            bosses.add(new SlimeBoss(128, 640 - 128, this));

        }
    }


    public void loadImages(){
        try{
            background = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/castleHallwayBackground.png"));
            player.loadPlayerImages();
            flagImage = ImageIO.read(getClass().getResourceAsStream("/Flag8Bit.png"));
            brickImage = ImageIO.read(getClass().getResourceAsStream("/brickTile.jpg"));
            spikeImage = ImageIO.read(getClass().getResourceAsStream("/spikes.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

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
