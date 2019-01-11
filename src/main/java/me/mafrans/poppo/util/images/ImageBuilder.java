package me.mafrans.poppo.util.images;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class ImageBuilder {
    private int width;
    private int height;
    private BufferedImage bufferedImage;
    private Graphics2D graphics;
    private ImageObserver observer;
    private Font textFont = new Font("Roboto", Font.PLAIN, 16);
    private Color color = new Color(255, 255, 255);

    public ImageBuilder(int width, int height) {
        this.width = width;
        this.height = height;

        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        System.out.println(textFont);
        graphics = bufferedImage.createGraphics();
    }

    public ImageBuilder setTextFont(Font font) {
        this.textFont = font;
        graphics.setFont(font);
        return this;
    }

    public ImageBuilder setColor(Color color) {
        this.color = color;
        graphics.setColor(color);
        return this;
    }

    public ImageBuilder addText(String text, int x, int y, int decoration, int size) {
        graphics.setFont(new Font(textFont.getFamily(), decoration, size));
        graphics.drawString(text, x, y);
        return this;
    }

    public ImageBuilder addImage(Image image, int x, int y, int width, int height) {
        graphics.drawImage(image, x, y, width, height, getObserver());
        return this;
    }

    public ImageBuilder addBackground(Image background, FitType fitType, boolean stretchX, boolean stretchY) {
        int imgX = 0;
        int imgY = 0;
        int imgWidth = background.getWidth(getObserver());
        int imgHeight = background.getHeight(getObserver());

        switch(fitType) {
            case CENTER:
                imgX = width/2 - imgWidth/2;
                imgY = height/2 - imgHeight/2;
                break;

            case TOP_LEFT:
                imgX = 0;
                imgY = 0;
                break;

            case TOP_RIGHT:
                imgX = width - imgWidth;
                imgY = 0;
                break;

            case BOTTOM_LEFT:
                imgX = 0;
                imgY = height - imgHeight;
                break;

            case BOTTOM_RIGHT:
                imgX = width - imgWidth;
                imgY = height - imgHeight;
                break;
        }

        if(stretchX) {
            imgWidth = width;
        }
        if(stretchY) {
            imgHeight = height;
        }

        graphics.drawImage(background, imgX, imgY, imgWidth, imgHeight, getObserver());
        return this;
    }

    public enum FitType {
        CENTER,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT;
    }

    private ImageObserver getObserver() {
        if(observer == null) {
            return (img, infoflags, x, y, width, height) -> false;
        }
        else return observer;
    }

    public Image build() {
        graphics.dispose();
        return bufferedImage;
    }
}
