package me.mafrans.poppo.util.images;

import me.mafrans.poppo.util.web.HTTPUtil;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ImageTester {
    private JLabel imageLabel;
    private JPanel mainPanel;

    public static void main(String[] args) throws IOException {
        ImageTester imageTester = new ImageTester();
        JFrame frame = new JFrame("ImageTester");
        frame.setContentPane(imageTester.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


        ImageBuilder imageBuilder = new ImageBuilder(500, 400);
        imageBuilder.addBackground(HTTPUtil.getImage("https://i.imgur.com/A2tJq59.jpg"), ImageBuilder.FitType.CENTER, true, true);
        imageBuilder.addText("Test", 200, 300, Font.PLAIN, 20);
        imageBuilder.setColor(new Color(255, 0, 0));
        imageBuilder.addText("Another Test", 400, 100, Font.ITALIC + Font.BOLD, 20);



        Image image = imageBuilder.build();
        imageTester.imageLabel.setIcon(new ImageIcon(image));
    }
}
