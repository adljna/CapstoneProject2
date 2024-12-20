package TicTacToe;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MainMenu extends JPanel {
    private ImageIcon backgroundImageIcon;  // Use ImageIcon to handle GIF
    private Image singlePlayerButtonImage;
    private Image multiplayerButtonImage;
    private JFrame parentFrame;

    public MainMenu(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(null); // Use absolute positioning for buttons

        // Load the background and button images
        backgroundImageIcon = loadImageback("src/mainmenu.gif");
        singlePlayerButtonImage = loadImage("src/aibutton.png");
        multiplayerButtonImage = loadImage("src/multibutton.png");

        // Create the "Single Player" button
        JButton singlePlayerButton = createCustomButton(singlePlayerButtonImage, 0, 0);
        singlePlayerButton.addActionListener(e -> {
            GameMain singlePlayerGame = new GameMain();
            singlePlayerGame.setSinglePlayerMode(true); // Set mode to single player
            parentFrame.setContentPane(singlePlayerGame);
            parentFrame.revalidate();
        });

        // Create the "Multiplayer" button
        JButton multiplayerButton = createCustomButton(multiplayerButtonImage, 0, 0);
        multiplayerButton.addActionListener(e -> {
            GameMain multiplayerGame = new GameMain();
            multiplayerGame.setSinglePlayerMode(false); // Set mode to multiplayer
            parentFrame.setContentPane(multiplayerGame);
            parentFrame.revalidate();
        });

        // Add buttons to the panel
        add(singlePlayerButton);
        add(multiplayerButton);

        // Adjust positions and sizes dynamically
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int panelWidth = getWidth();
                int panelHeight = getHeight();

                // Dynamically position buttons
                singlePlayerButton.setBounds((panelWidth - 250) / 2, panelHeight / 2 - 100, 250, 80);
                multiplayerButton.setBounds((panelWidth - 250) / 2, panelHeight / 2, 250, 80);

                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImageIcon != null) {
            // Scale and draw the background image
            g.drawImage(backgroundImageIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }

    // Helper method to load images with null handling
    private Image loadImage(String path) {
        try {
            File file = new File(path);
            Image img = ImageIO.read(file);
            if (img != null) {
                return img.getScaledInstance(250, 80, Image.SCALE_SMOOTH);
            } else {
                System.out.println("Image not found: " + path);
            }
        } catch (IOException e) {
            System.out.println("Failed to load image: " + path + ". Error: " + e.getMessage());
        }
        return null;
    }

    // Helper method to load and return an ImageIcon for the background (supports GIF)
    private ImageIcon loadImageback(String path) {
        try {
            File file = new File(path);
            ImageIcon imgIcon = new ImageIcon(file.getAbsolutePath());
            if (imgIcon != null) {
                return imgIcon;
            } else {
                System.out.println("Image not found: " + path);
            }
        } catch (Exception e) {
            System.out.println("Failed to load image: " + path + ". Error: " + e.getMessage());
        }
        return null;
    }

    // Helper method to create a custom button
    private JButton createCustomButton(Image buttonImage, int x, int y) {
        JButton button = new JButton();
        if (buttonImage != null) {
            button.setIcon(new ImageIcon(buttonImage));
        }
        button.setBounds(x, y, 250, 80);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 191, 255, 100));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(null);
            }
        });

        return button;
    }
}
