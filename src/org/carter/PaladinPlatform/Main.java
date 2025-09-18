package org.carter.PaladinPlatform;

import javax.swing.*;
import java.awt.*;

import static javax.swing.WindowConstants.*;

public class Main {

    public static void main(String[] args){
        MainFrame frame = new MainFrame();

        frame.setSize(640,640);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int)(screenSize.getWidth()/2 - frame.getSize().getWidth()/2), (int)(screenSize.getHeight()/2 - frame.getSize().getHeight()/2));

        frame.setResizable(false);
        frame.setTitle("Paladin Platform");
        frame.setVisible(true);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

}
