package org.carter.PaladinPlatform;

import java.awt.*;

public class MainFrame extends javax.swing.JFrame{
    /**
     * This is the class that handles all the graphics.
     * It adds the GamePanel that is in charge of the updates and generating the graphics.
     * This is also in charge of the input from the KeyChecker that the GamePanel and the KeyChecker
     * communicate to each other through this MainFrame class.
     */
    public MainFrame(){
        GamePanel panel = new GamePanel();
        panel.setLocation(0,0);
        panel.setBackground(Color.DARK_GRAY);
        panel.setVisible(true);
        this.add(panel);

        addKeyListener(new KeyChecker(panel));
    }
}
