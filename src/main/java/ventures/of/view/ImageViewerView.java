package ventures.of.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ImageViewerView extends JPanel {
    static ArrayList<Frame> frames = new ArrayList<>();
    private final BufferedImage image;

    int xCords;  // Number of x-cords (horizontal divisions)
    int yCords;  // Number of y-cords (vertical divisions)

    int xIndex;  // X-coordinate index (0 to xCords - 1)
    int yIndex;  // Y-coordinate index (0 to yCords - 1)

    public ImageViewerView(int dividerX, int dividerY, int x, int y, BufferedImage image) {
        this.image = image;
        this.xCords = dividerX;
        this.yCords = dividerY;
        this.xIndex = x;
        this.yIndex = y;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int regionWidth = image.getWidth() / xCords;
        int regionHeight = image.getHeight() / yCords;
        int regionX = regionWidth * xIndex;
        int regionY = regionHeight * yIndex;

        g.drawImage(image, 0, 0, getWidth(), getHeight(), regionX, regionY, regionX + regionWidth, regionY + regionHeight, null);
    }

    public static void  createView(int dividerX, int dividerY, int x, int y, String imageName) {

        destroyFrames();
        // Load the original image
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(new File(imageName));
        } catch (IOException e) {
            System.out.println("Error loading the original image: " + e.getMessage());
            //System.exit(1);
        }

        BufferedImage finalOriginalImage = originalImage;
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ImageViewer");
            frames.add(frame);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);  // Full-screen mode

            // Create the panel and add it to the frame
            ImageViewerView panel = new ImageViewerView(dividerX, dividerY, x, y, finalOriginalImage);
            frame.getContentPane().add(panel);

            // Get the default screen device and set the frame to full-screen
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            gd.setFullScreenWindow(frame);

            frame.setVisible(true);
        });
    }

    public static Void destroyFrames() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(null);

        frames.forEach(Window::dispose) ;
        frames = new ArrayList<>();

        return null;
    }
}
