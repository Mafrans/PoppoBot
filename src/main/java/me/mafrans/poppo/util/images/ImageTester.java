package me.mafrans.poppo.util.images;

import me.mafrans.javadins.Javadins;
import me.mafrans.javadins.SessionInvalidException;
import me.mafrans.poppo.Main;
import me.mafrans.poppo.commands.Command_paladins;
import me.mafrans.poppo.util.web.HTTPUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.ParseException;

public class ImageTester {
    private JLabel imageLabel;
    private JPanel mainPanel;

    public static void main(String[] args) throws IOException, ParseException, SessionInvalidException, FontFormatException {
        ImageTester imageTester = new ImageTester();
        JFrame frame = new JFrame("ImageTester");
        frame.setContentPane(imageTester.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


        Image image = Command_paladins.generateImage(new Javadins("2954", "A174F3ECB5854FEBBFD47F7FDF1AA7CC").getMatch(780306132));
        imageTester.imageLabel.setIcon(new ImageIcon(image));
    }
}
