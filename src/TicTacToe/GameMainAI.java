package TicTacToe;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Tic-Tac-Toe: Two-player Graphic version with better OO design.
 * The Board and Cell classes are separated in their own classes.
 */
public class GameMainAI extends JPanel {
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // Define named constants for the drawing graphics
    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG_START = new Color(240, 248, 255); // Alice Blue
    public static final Color COLOR_BG_END = new Color(135, 206, 250);   // Light Sky Blue
    public static final Color COLOR_STATUS_BAR = new Color(255, 255, 255); // Lavender
    public static final Color COLOR_CROSS = new Color(255, 69, 0);  // Orange Red
    public static final Color COLOR_NOUGHT = new Color(0, 191, 255); // Deep Sky Blue
    public static final Font FONT_STATUS = new Font("Arial", Font.BOLD, 16);
    private boolean isGameOver = false; // Flag untuk mencatat apakah game selesai


    // Define game objects
    private Board board;         // the game board
    private State currentState;  // the current state of the game
    private Seed currentPlayer;  // the current player
    private JLabel statusBar;    // for displaying status message

    private AIPlayer aiPlayer;   // AI player object for single-player mode
    private boolean isSinglePlayer = true; // Flag for single-player mode

    /** Constructor to setup the UI and game components */
    public GameMainAI() {
        Timer timer = new Timer(100, e -> repaint()); // Refresh display every 100ms
        timer.start();

        // This JPanel fires MouseEvent
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

                if (isGameOver) {
                    // Jika game selesai, klik mouse akan mereset game
                    newGame();
                    isGameOver = false; // Reset flag setelah memulai game baru
                    return;
                }

                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        currentState = board.stepGame(currentPlayer, row, col);
                        repaint();

                        // Periksa apakah pemain manusia menang/seri setelah langkah
                        if (currentState == State.CROSS_WON) {
                            stopBackgroundMusic();
                            SoundEffect.GAME_WON.play(); // Mainkan suara kemenangan
                            isGameOver = true;
                            return;
                        } else if (currentState == State.DRAW) {
                            stopBackgroundMusic();
                            SoundEffect.GAME_DRAW.play(); // Mainkan suara seri
                            isGameOver = true;
                            return;
                        }

                        // Ganti giliran ke AI jika dalam mode single-player
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                        if (isSinglePlayer && currentPlayer == Seed.NOUGHT) {
                            makeAIMove();

                            // Periksa apakah AI menang/seri setelah langkahnya
                            if (currentState == State.NOUGHT_WON) {
                                stopBackgroundMusic();
                                SoundEffect.GAME_LOST.play(); // Mainkan suara kekalahan
                                isGameOver = true;
                            } else if (currentState == State.DRAW) {
                                stopBackgroundMusic();
                                SoundEffect.GAME_DRAW.play(); // Mainkan suara seri
                                isGameOver = true;
                            }
                        }
                    }
                }
                repaint();
            }
        });

        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_STATUS_BAR);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 40));
        statusBar.setHorizontalAlignment(JLabel.CENTER);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        super.setLayout(new BorderLayout());
        super.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 40));

        // Set up Game
        initGame();
        newGame();
    }

    /** Initialize the game (run once) */
    public void initGame() {
        board = new Board();  // allocate the game-board
        aiPlayer = new AIPlayerMinimax(board); // Instantiate AI player
    }

    /** Reset the game-board contents and the current-state, ready for new game */
    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED; // all cells empty
            }
        }
        currentPlayer = Seed.CROSS;    // cross plays first
        currentState = State.PLAYING;  // ready to play
        repaint();
    }

    /** AI makes its move */
    private void makeAIMove() {
        System.out.println("Before AI move, current player: " + currentPlayer);
        int[] move = aiPlayer.move(); // Get the AI's move
        currentState = board.stepGame(currentPlayer, move[0], move[1]);

        // Only switch players if the game is still ongoing
        if (currentState == State.PLAYING) {
            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
        }

        System.out.println("After AI move, current player: " + currentPlayer);
        repaint();
    }
    private void stopBackgroundMusic() {
        if (SoundEffect.GAME_START.isPlaying()) {
            SoundEffect.GAME_START.stop();
        }
    }



    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) {  // Callback via repaint()
        super.paintComponent(g);
        board.paint(g);  // ask the game board to paint itself

        // Print status-bar message
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
        }
    }

    /** Draw a gradient background */

    /** The entry "main" method */
    public void game() {
        // Run GUI construction codes in Event-Dispatching thread for thread safety
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 70);
            frame.setLocationRelativeTo(null); // Pusatkan jendela
            frame.setContentPane(new MainMenu(frame)); // Set menu utama sebagai konten awal
            frame.setVisible(true); // Tampilkan jendela    // show it
        });
    }

    public static void main(String[] args) {
        GameMainAI a = new GameMainAI();
        a.game();
    }

}