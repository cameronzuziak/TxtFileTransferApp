package frontEnd;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class BackgroundPanel extends JPanel{
    private ImageIcon bgImage;
    public BackgroundPanel() {
        bgImage = new ImageIcon("src/frontEnd/background.gif");
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }
    @Override
    protected void paintComponent(Graphics g) {
        int imageWidth = bgImage.getIconWidth();
        int imageHeight = bgImage.getIconHeight();
        if (imageWidth == 0 || imageHeight == 0) {
            return;
        }
        double widthScale = (double)getWidth() / (double)imageWidth;
        double heightScale = (double)getHeight() / (double)imageHeight;
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.drawImage(bgImage.getImage(), AffineTransform.getScaleInstance(widthScale, heightScale), this);
        g2d.dispose();
    }
}
