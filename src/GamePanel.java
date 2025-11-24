import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.text.*;

public class GamePanel extends JPanel {

    private Main mainApp;

    private JPanel contentPanel;
    private JPanel gamePlayPanel;
    private JPanel resultPanel;

    private JLabel timerLabel;
    private JLabel wpmLabel;
    private JLabel accuracyLabel;
    private JLabel userLabel;
    private JButton menuButton;

    private JTextPane soalTextPane;
    private JTextField inputField;

    private JLabel finalWpmLabel;
    private JLabel finalAccuracyLabel;

    private String goalSoal;
    private String[] goalWords;
    private int[] wordStartIndices;
    private int currWordIdx;
    private volatile int countdownVal;
    private volatile boolean isGameRunning;
    private volatile boolean isCountdownRunning;
    private int totalCharBenar;
    private int totalCharKetik;
    private int totalCorrectCharsInPastWords;
    private int totalCharsInPastWords;

    private Thread gameThread;
    private Thread countdownThread;

    private Clip backgroundMusic;

    private StyledDocument styledDoc;

    public GamePanel(Main mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        topPanel.setBackground(Theme.DARK_GRAY);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        menuButton = new JButton("« Menu");
        styleHeaderButton(menuButton);

        timerLabel = new JLabel("Waktu: 60s", SwingConstants.CENTER);
        styleHeaderLabel(timerLabel);

        wpmLabel = new JLabel("WPM: 0", SwingConstants.CENTER);
        styleHeaderLabel(wpmLabel);

        accuracyLabel = new JLabel("Akurasi: 100%", SwingConstants.CENTER);
        styleHeaderLabel(accuracyLabel);

        userLabel = new JLabel("User: ...", SwingConstants.RIGHT);
        styleHeaderLabel(userLabel);

        JPanel menuButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        menuButtonPanel.setBackground(Theme.DARK_GRAY);
        menuButtonPanel.add(menuButton);

        topPanel.add(menuButtonPanel);
        topPanel.add(timerLabel);
        topPanel.add(wpmLabel);
        topPanel.add(accuracyLabel);
        topPanel.add(userLabel);

        add(topPanel, BorderLayout.NORTH);

        contentPanel = new JPanel(new CardLayout());
        setupGamePlayPanel();
        setupResultPanel();
        contentPanel.add(gamePlayPanel, "GAME_PLAY");
        contentPanel.add(resultPanel, "GAME_RESULT");
        add(contentPanel, BorderLayout.CENTER);

        loadSound();
    }

    private void setupGamePlayPanel() {
        gamePlayPanel = new JPanel(new BorderLayout(10, 10));
        gamePlayPanel.setBackground(Theme.DARK_GRAY);

        soalTextPane = new JTextPane();
        soalTextPane.setEditable(false);
        soalTextPane.setBackground(Theme.DARKER_GRAY);
        soalTextPane.setFont(Theme.MAIN_FONT);
        soalTextPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        styledDoc = soalTextPane.getStyledDocument();

        styledDoc.setParagraphAttributes(0, styledDoc.getLength(), Theme.STYLE_DEFAULT, false);

        JScrollPane scrollPane = new JScrollPane(soalTextPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.YELLOW, 2));
        gamePlayPanel.add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField();
        inputField.setFont(Theme.MAIN_FONT);
        inputField.setBackground(Theme.DARKER_GRAY);
        inputField.setForeground(Theme.LIGHT_GRAY);
        inputField.setCaretColor(Theme.YELLOW);
        inputField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputField.setEditable(false);

        gamePlayPanel.add(inputField, BorderLayout.SOUTH);

        addGameListeners();
    }

    private void setupResultPanel() {
        resultPanel = new JPanel(new GridBagLayout());
        resultPanel.setBackground(Theme.DARK_GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel title = new JLabel("Game Selesai!");
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.YELLOW);
        gbc.gridy = 0;
        resultPanel.add(title, gbc);

        finalWpmLabel = new JLabel("WPM: 0");
        finalWpmLabel.setFont(Theme.SUBTITLE_FONT);
        finalWpmLabel.setForeground(Theme.WHITE);
        gbc.gridy = 1;
        resultPanel.add(finalWpmLabel, gbc);

        finalAccuracyLabel = new JLabel("Akurasi: 0.00%");
        finalAccuracyLabel.setFont(Theme.SUBTITLE_FONT);
        finalAccuracyLabel.setForeground(Theme.WHITE);
        gbc.gridy = 2;
        resultPanel.add(finalAccuracyLabel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(20, 10, 5, 10);
        resultPanel.add(Box.createVerticalStrut(10), gbc);

        JButton mainLagiButton = new JButton("Main Lagi");
        styleResultButton(mainLagiButton);
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        resultPanel.add(mainLagiButton, gbc);

        JButton kembaliMenuButton = new JButton("Kembali ke Menu");
        styleResultButton(kembaliMenuButton);
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 10, 10, 10);
        resultPanel.add(kembaliMenuButton, gbc);

        mainLagiButton.addActionListener(e -> {
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, "GAME_PLAY");
            startCountdown();
        });

        kembaliMenuButton.addActionListener(e -> {
            mainApp.showPanel("MENU");
        });
    }

    private void styleHeaderLabel(JLabel label) {
        label.setFont(Theme.SUBTITLE_FONT);
        label.setForeground(Theme.WHITE);
    }

    private void styleHeaderButton(JButton button) {
        button.setBackground(Theme.YELLOW);
        button.setForeground(Color.BLACK);
        button.setFont(Theme.BOLD_FONT);
        button.setFocusPainted(false);
    }

    private void styleResultButton(JButton button) {
        button.setBackground(Theme.DARKER_GRAY);
        button.setForeground(Theme.WHITE);
        button.setFont(Theme.BOLD_FONT);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
    }

    public void setCurrentUsername(String username) {
        userLabel.setText("User: " + username);
    }

    private void loadSound() {
        try {
            File audioFile = new File("assets/background_music.wav");
            if (audioFile.exists()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioStream);
            } else {
                System.err.println("File musik tidak ditemukan: assets/background_music.wav");
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void startCountdown() {
        if (isCountdownRunning || isGameRunning) return;

        isCountdownRunning = true;
        countdownVal = 3;

        soalTextPane.setText("");
        styledDoc.setParagraphAttributes(0, styledDoc.getLength(), Theme.STYLE_DEFAULT, false);

        Style centeredStyle = styledDoc.addStyle("Centered", null);
        centeredStyle.addAttributes(Theme.STYLE_DEFAULT);
        StyleConstants.setAlignment(centeredStyle, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontSize(centeredStyle, 100);
        StyleConstants.setBold(centeredStyle, true);
        StyleConstants.setForeground(centeredStyle, Theme.YELLOW);

        soalTextPane.setParagraphAttributes(centeredStyle, false);

        inputField.setEditable(false);
        inputField.setText("");

        countdownThread = new Thread(() -> {
            try {
                while (countdownVal > 0) {
                    final int displayVal = countdownVal;
                    SwingUtilities.invokeLater(() -> {
                        soalTextPane.setText(String.valueOf(displayVal));
                        styledDoc.setParagraphAttributes(0, styledDoc.getLength(), centeredStyle, false);
                    });
                    Thread.sleep(1000);
                    countdownVal--;
                }

                SwingUtilities.invokeLater(() -> {
                    soalTextPane.setText("Go!");
                    styledDoc.setParagraphAttributes(0, styledDoc.getLength(), centeredStyle, false);
                });
                Thread.sleep(800);

                SwingUtilities.invokeLater(this::startGame);

            } catch (InterruptedException e) {
                System.out.println("Countdown diinterupsi.");
            }
        });

        countdownThread.start();
    }

    private void startGame() {
        isCountdownRunning = false;
        isGameRunning = true;

        prepareNewSoal();

        countdownVal = 60;
        timerLabel.setText("Waktu: " + countdownVal + "s");

        inputField.setEditable(true);
        inputField.requestFocusInWindow();

        if (backgroundMusic != null) {
            backgroundMusic.setFramePosition(0);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }

        gameThread = new Thread(() -> {
            try {
                while (isGameRunning && countdownVal > 0) {
                    Thread.sleep(1000);
                    countdownVal--;
                    SwingUtilities.invokeLater(() -> {
                        timerLabel.setText("Waktu: " + countdownVal + "s");
                        updateStats();
                    });
                }
                if (isGameRunning) {
                    SwingUtilities.invokeLater(() -> stopGame(GameEndType.TIMER_UP, true));
                }
            } catch (InterruptedException e) {
                System.out.println("Game thread diinterupsi.");
            }
        });
        gameThread.start();
    }

    public void stopGame(GameEndType reason, boolean showResult) {
        if (!isGameRunning) {
            if (isCountdownRunning) {
                isCountdownRunning = false;
                if (countdownThread != null && countdownThread.isAlive()) {
                    countdownThread.interrupt();
                }
            }
            return;
        }

        isGameRunning = false;

        if (gameThread != null && gameThread.isAlive()) {
            gameThread.interrupt();
        }

        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }

        inputField.setEditable(false);

        if (reason == GameEndType.USER_QUIT) {
            return;
        }

        double finalWpm = Double.parseDouble(wpmLabel.getText().split(": ")[1]);
        double finalAccuracy = Double.parseDouble(
            accuracyLabel.getText().split(": ")[1].replace("%", "")
        );

        if (mainApp.getCurrentUserId() != -1) {
            new SaveScore(mainApp.getKoneksi(), mainApp.getCurrentUserId(), finalWpm, finalAccuracy).execute();
        }

        if (showResult) {
            finalWpmLabel.setText(String.format("WPM: %.0f", finalWpm));
            finalAccuracyLabel.setText(String.format("Akurasi: %.2f%%", finalAccuracy));
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, "GAME_RESULT");
        }
    }

    private void prepareNewSoal() {
        goalSoal = mainApp.getKoneksi().getRandomSoal();
        goalWords = goalSoal.split("\\s+");
        wordStartIndices = new int[goalWords.length];

        currWordIdx = 0;
        totalCharBenar = 0;
        totalCharKetik = 0;
        totalCorrectCharsInPastWords = 0;
        totalCharsInPastWords = 0;

        soalTextPane.setText("");
        styledDoc.setParagraphAttributes(0, styledDoc.getLength(), Theme.STYLE_DEFAULT, true);

        try {
            int pos = 0;
            for (int i = 0; i < goalWords.length; i++) {
                wordStartIndices[i] = pos;
                styledDoc.insertString(pos, goalWords[i] + " ", null);
                pos += goalWords[i].length() + 1;
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        highlightCurrentWord();
    }

    private void addGameListeners() {
        menuButton.addActionListener(e -> {
            stopGame(GameEndType.USER_QUIT, false);
            mainApp.showPanel("MENU");
        });

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!isGameRunning) return;

                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    onSpacePressed();
                } else {
                    onCharacterTyped();
                }
            }
        });
    }

    private void onSpacePressed() {
        String typedWord = inputField.getText().trim();
        if (typedWord.isEmpty()) return;

        if (currWordIdx >= goalWords.length) return;

        String targetWord = goalWords[currWordIdx];
        boolean isCorrect = typedWord.equals(targetWord);

        int pos = wordStartIndices[currWordIdx];
        int len = targetWord.length();
        styledDoc.setCharacterAttributes(pos, len,
            isCorrect ? Theme.STYLE_CORRECT : Theme.STYLE_INCORRECT,
            true);

        totalCharsInPastWords += typedWord.length();
        if (isCorrect) {
            totalCorrectCharsInPastWords += targetWord.length();
        } else {
            for (int i = 0; i < typedWord.length() && i < targetWord.length(); i++) {
                if (typedWord.charAt(i) == targetWord.charAt(i)) {
                    totalCorrectCharsInPastWords++;
                }
            }
        }

        currWordIdx++;
        inputField.setText("");

        if (currWordIdx == goalWords.length) {
            stopGame(GameEndType.PARAGRAPH_COMPLETE, true);
        } else {
            highlightCurrentWord();
        }

        updateStats();
    }

    private void onCharacterTyped() {
        updateStats();
    }

    private void highlightCurrentWord() {
        if (currWordIdx >= goalWords.length) return;
    
        try {
            if (currWordIdx > 0) {
                int prevPos = wordStartIndices[currWordIdx - 1];
                int prevLen = goalWords[currWordIdx - 1].length();
    
                AttributeSet attrs = styledDoc.getCharacterElement(prevPos).getAttributes();
                Color prevColor = StyleConstants.getForeground(attrs);
    
                if (prevColor.equals(Theme.LIGHT_GRAY)) {
                    styledDoc.setCharacterAttributes(prevPos, prevLen, Theme.STYLE_DEFAULT, true);
                }
            }
    
            int pos = wordStartIndices[currWordIdx];
            int len = goalWords[currWordIdx].length();
    
            SimpleAttributeSet highlightStyle = new SimpleAttributeSet();
            StyleConstants.setBackground(highlightStyle, new Color(180, 180, 180)); 
            StyleConstants.setForeground(highlightStyle, Theme.DARK_GRAY);        
            StyleConstants.setBold(highlightStyle, false);
    
            styledDoc.setCharacterAttributes(pos, len, highlightStyle, true);
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    

    private void updateStats() {
        if (countdownVal == 60) return;

        totalCharBenar = totalCorrectCharsInPastWords;
        totalCharKetik = totalCharsInPastWords;

        String currentTyped = inputField.getText();
        totalCharKetik += currentTyped.length();

        if (currWordIdx < goalWords.length) {
            String currentTarget = goalWords[currWordIdx];
            int lenToCompare = Math.min(currentTyped.length(), currentTarget.length());
            for (int i = 0; i < lenToCompare; i++) {
                if (currentTyped.charAt(i) == currentTarget.charAt(i)) {
                    totalCharBenar++;
                }
            }
        }

        double elapsedTimeSeconds = 60.0 - countdownVal;
        double time = elapsedTimeSeconds / 60.0;

        if (time == 0) {
            wpmLabel.setText("WPM: 0");
            accuracyLabel.setText("Akurasi: 100%");
            return;
        }

        double wpm = (totalCharBenar / 5.0) / time;

        double akurasi = 100.0;
        if (totalCharKetik > 0) {
            akurasi = ((double) totalCharBenar / totalCharKetik) * 100.0;
        }

        wpmLabel.setText("WPM: " + (int) wpm);
        accuracyLabel.setText(String.format("Akurasi: %.2f%%", akurasi));
    }
}
