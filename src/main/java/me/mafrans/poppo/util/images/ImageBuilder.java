package me.mafrans.poppo.util.images;

//import com.jhlabs.image.GaussianFilter;
import lombok.Getter;
import me.mafrans.poppo.util.GUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.security.Key;

public class ImageBuilder {
    private int width;
    private int height;
    private BufferedImage bufferedImage;
    @Getter private Graphics2D graphics;
    private ImageObserver observer;
    private Font textFont = GUtil.getTrueTypeFont("fonts/Lato-Regular.ttf");
    private Color color = new Color(255, 255, 255);

    public ImageBuilder(int width, int height) throws IOException, FontFormatException {
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

    public ImageBuilder addImage(Image image, int x, int y, int width, int height, int hint) {
        graphics.drawImage(image.getScaledInstance(width, height, hint), x, y, width, height, getObserver());
        return this;
    }

    public ImageBuilder setRenderingHint(RenderingHints.Key key, Object value) {
        graphics.setRenderingHint(key, value);
        return this;
    }

    public ImageBuilder addShape(Shape shape, boolean fill) {
        if(fill) {
            graphics.fill(shape);
        }
        else {
            graphics.draw(shape);
        }
        return this;
    }

    /*public BufferedImage applyFilter() {
        GaussianFilter gaussianFilter = new GaussianFilter(blurAmount);
        BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        gaussianFilter.filter(toBufferedImage(background), bufferedImage);
        return bufferedImage;
    }*/

    public ImageBuilder addBackground(Image background, FitType fitType, boolean stretchX, boolean stretchY, float blurAmount) {
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

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
