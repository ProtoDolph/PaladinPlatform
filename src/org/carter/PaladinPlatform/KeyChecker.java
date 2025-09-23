package org.carter.PaladinPlatform;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyChecker extends KeyAdapter {
    GamePanel panel;

    /**
     * This is the KeyChecker class
     * In charge of all user input entered. It will send this input back to the gamePanel to be
     * evaluated to see if it affects anything or not.
     * @param panel the gamePanel that its sending its input back to.
     */
    public KeyChecker(GamePanel panel){
        this.panel = panel;
    }

    /**
     * If a key was pressed it will call the GamePanel's keypressed method
     * to evaluate the key and to see what movement values of the player need to be cahnge.
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e){
        panel.keyPressed(e);
    }
    /**
     * If a key was released it will call the GamePanel's keyReleased method
     * to evaluate the key and to see what movement values of the player need to be change.
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e){
        panel.keyReleased(e);
    }
}
