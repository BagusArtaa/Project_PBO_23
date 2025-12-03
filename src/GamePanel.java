import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.swing.*;

import dao.WordDAO;
import model.Score;
import model.WordEntry;
import dao.ScoreDAO;

public class GamePanel extends JPanel implements ActionListener {
    private final Object pauseLock = new Object();
    private volatile boolean paused = false;
    private volatile boolean running = false; // single source of truth for thread loops

    private MainFrame frame;

    public static final int PANEL_WIDTH = 900;
    public static final int PANEL_HEIGHT = 600;

    private static final int SPAWN_MIN_Y = 150;  // Updated to be below header (80px)
    private static final int SPAWN_MAX_Y = 500;

    private int maxDifficulty;

   
    private Image ghost;
    private Image lich;
    private Image dragon;
  
    private Image bg_game;
    private Image fort;
    private Timer timer;
    private Player me;

    // gambar hati
    private Image heartFull;
    private Image heartEmpty;
    private Image headerimage;

    private Queue<MonsterUnit> queue = new LinkedList<>();
    private List<MonsterUnit> active = new ArrayList<>();

    // --- timing & spawn (game-time based) ---
    private long gameStartMillis = 0;      // real time when game started
    private long pausedAtMillis = 0;       // real time when pause started (0 if not paused)
    private long totalPausedMillis = 0;    // total accumulated paused duration
    private long lastSpawnGameTime = 0;    // waktu spawn terakhir berbasis "game time" (ms)
    private long spawnDelay = 2000;        // 2 detik antar spawn (ms)

    private boolean speedBoosted = false;
    private int boostedVelocity;

    private boolean gameOver = false;
    private Rectangle resumeButton = new Rectangle(370, 280, 160, 50);
    private Rectangle menuButton = new Rectangle(370, 360, 160, 50);

    private Thread countdownThread;
    private Thread timerThread;
    private int countdownValue = -1;
    private boolean showingCountdown = false;

    private int timeLeftSeconds = 60; // main countdown
    private int score = 0;
    private int comboStreak = 0; // jumlah benar berturut-turut
    private int scoreMultiplier = 1; // default x1
    private int correctChars = 0;     // jumlah karakter benar
    private int totalTyped = 0; // total key presses relevant untuk accuracy

    // Header constants
    private static final int HEADER_HEIGHT = 80;
    private static final int HEADER_PADDING = 10;

    public GamePanel(MainFrame frame) {
        this.frame = frame;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);

      
        ghost = new ImageIcon("assets/ghost.png").getImage();
        lich = new ImageIcon("assets/lich.png").getImage();
        dragon = new ImageIcon("assets/dragon.png").getImage();
       
        heartFull = new ImageIcon("assets/hati-penuh.png").getImage();
        heartEmpty = new ImageIcon("assets/hati-kosong.png").getImage();
     
        headerimage = new ImageIcon("assets/header.png").getImage();
        bg_game = new ImageIcon("assets/bg_game.png").getImage();
        fort = new ImageIcon("assets/fort.png").getImage();


        timer = new Timer(16, this); // Swing timer untuk animasi (panggil actionPerformed)

        setFocusable(true);

        // mouse click pada overlay saat paused
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!paused) return;
                int mx = e.getX();
                int my = e.getY();
                if (resumeButton.contains(mx, my)) {
                    resumeGame();
                    return;
                }
                if (menuButton.contains(mx, my)) {
                    returnToMenu();
                    return;
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // ESC toggle pause/resume
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (paused) resumeGame();
                    else pauseGame();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (paused) return; // jangan proses input saat pause
                totalTyped++;

                char typed = e.getKeyChar();

                for (MonsterUnit p : active) {
                    if (!p.isFinished()) {
                        if (p.consumeIfMatch(typed)) {
                            correctChars++;
                            comboStreak++;
                            updateMultiplier();
                            score += 1 * scoreMultiplier;
                        } else {
                            // salah => reset combo & multiplier
                            comboStreak = 0;
                            scoreMultiplier = 1;
                        }
                        break;
                    }
                }
            }
        });
    }

    private void returnToMenu() {
        // stop everything cleanly, wake waiting threads
        stopAllThreads();
        frame.showPanel("menu");
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(bg_game, 0, 0, null);

        // Draw header background
        drawHeader(g2D);

        // Monster & teks
        g2D.setFont(new Font("Arial", Font.BOLD, 20));
        for (MonsterUnit p : active) {
            if (!p.isFinished()) {

                Image img = p.getImage();
                int px = p.getX();
                int py = p.getY();
                String txt = p.getText();

                g2D.drawImage(img, px, py, null);
                g2D.setColor(Color.WHITE);

                FontMetrics fmMonster = g2D.getFontMetrics();
                int monsterTextWidth = fmMonster.stringWidth(txt);

                int textX = px + (img.getWidth(null) - monsterTextWidth) / 2;
                int textY = py - 5;

                g2D.drawString(txt, textX, textY);
            }
        }

        // Player & hearts
        g2D.drawImage(me.getImage(), me.getX(), me.getY(), null);
        // Countdown besar (321 Go!)
        if (showingCountdown) {
            g2D.setColor(Color.WHITE);
            g2D.setFont(new Font("Monospaced", Font.BOLD, 80));
            String msg = (countdownValue > 0) ? String.valueOf(countdownValue) : "Go!";
            FontMetrics fmCountdown = g2D.getFontMetrics();
            int w = fmCountdown.stringWidth(msg);
            int cx = (PANEL_WIDTH - w) / 2;
            int cy = PANEL_HEIGHT / 2;
            g2D.drawString(msg, cx, cy);
        }

        // Pause overlay terakhir agar tampil di atas semua elemen
        if (paused) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
            
            // Paused text - centered
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            String pausedText = "Paused";
            FontMetrics fmPaused = g2.getFontMetrics();
            int pausedWidth = fmPaused.stringWidth(pausedText);
            int pausedX = (PANEL_WIDTH - pausedWidth) / 2;
            int pausedY = 150;
            g2.drawString(pausedText, pausedX, pausedY);

            // Resume button - centered
            g2.setColor(Color.GRAY);
            g2.fill(resumeButton);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            String resumeText = "Resume";
            FontMetrics fmResume = g2.getFontMetrics();
            int resumeTextWidth = fmResume.stringWidth(resumeText);
            int resumeTextX = resumeButton.x + (resumeButton.width - resumeTextWidth) / 2;
            int resumeTextY = resumeButton.y + ((resumeButton.height + fmResume.getAscent()) / 2);
            g2.drawString(resumeText, resumeTextX, resumeTextY);

            // Menu button - centered
            g2.setColor(Color.GRAY);
            g2.fill(menuButton);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            String menuText = "Menu";
            FontMetrics fmMenu = g2.getFontMetrics();
            int menuTextWidth = fmMenu.stringWidth(menuText);
            int menuTextX = menuButton.x + (menuButton.width - menuTextWidth) / 2;
            int menuTextY = menuButton.y + ((menuButton.height + fmMenu.getAscent()) / 2);
            g2.drawString(menuText, menuTextX, menuTextY);

        }
    }

    private void drawHeader(Graphics2D g2D) {
        // Header background
        g2D.drawImage(headerimage, 0, 0, PANEL_WIDTH, HEADER_HEIGHT, null);
        
        
        // Lives (Hearts) - Left side
        int heartW = heartFull.getWidth(null);
        int padding = 8;
        int heartsStartX = HEADER_PADDING;
        int heartsStartY = (HEADER_HEIGHT - heartW) / 2;
        for (int i = 0; i < 3; i++) {
            int hx = heartsStartX + i * (heartW + padding);
            if (i < me.getLives()) g2D.drawImage(heartFull, hx, heartsStartY, null);
            else g2D.drawImage(heartEmpty, hx, heartsStartY, null);
        }

        // Score (Center)
        g2D.setFont(new Font("Arial", Font.BOLD, 24));
        String scoreText = "Score: " + score + " (" + scoreMultiplier + "x)";
        FontMetrics fmScore = g2D.getFontMetrics();
        int scoreWidth = fmScore.stringWidth(scoreText);
        int scoreCenterX = PANEL_WIDTH / 2 - scoreWidth / 2;
        int scoreY = (HEADER_HEIGHT + fmScore.getAscent()) / 2;
        g2D.setColor(Color.WHITE);
        g2D.drawString(scoreText, scoreCenterX, scoreY);

        // Timer (Right side)
        g2D.setFont(new Font("Arial", Font.BOLD, 28));
        String formatted = String.format("%d:%02d", timeLeftSeconds / 60, timeLeftSeconds % 60);
        FontMetrics fmTimer = g2D.getFontMetrics();
        int timerWidth = fmTimer.stringWidth(formatted);
        int timerX = PANEL_WIDTH - timerWidth - HEADER_PADDING;
        int timerY = (HEADER_HEIGHT + fmTimer.getAscent()) / 2;
        g2D.setColor(Color.WHITE);
        g2D.drawString(formatted, timerX, timerY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Swing timer dipakai untuk frame updates / game step
        if (paused) {
            // tetap repaint agar overlay terlihat dan klik/tombol dapat merespon
            repaint();
            return;
        }

        long now = System.currentTimeMillis();

        // compute gameTimeNow (ms) yang mengecualikan periode pause
        long gameTimeNow = 0;
        if (running) {
            gameTimeNow = now - gameStartMillis - totalPausedMillis;
        if (gameTimeNow < 0) gameTimeNow = 0;
        }

        if (!speedBoosted && timeLeftSeconds <= 30) {
            speedBoosted = true;

            for (MonsterUnit p : active) p.setVelocity(boostedVelocity);
            for (MonsterUnit q : queue) q.setVelocity(boostedVelocity);
        }

        updateSpawnDelay();
        // spawn (berdasarkan gameTimeNow)
        if (!queue.isEmpty() && running) {
            if (gameTimeNow - lastSpawnGameTime >= spawnDelay) {
                MonsterUnit unit = queue.poll();
                active.add(unit);
                lastSpawnGameTime = gameTimeNow;
            }
        }

        // gerakkan semua monster
        for (MonsterUnit p : active) p.move();

        // collision & removal
        Iterator<MonsterUnit> it = active.iterator();
        while (it.hasNext()) {
            MonsterUnit p = it.next();
            if (p.isFinished()) {
                it.remove();
                continue;
            }
            Rectangle rPac = p.getHitbox();
            Rectangle rPlayer = me.getHitbox();
            if (rPac.intersects(rPlayer)) {
                it.remove();
                me.takeHit();
                if (me.isDead()) {
                    endGame();
                    break;
                }
            }
        }

        repaint();
    }

    public void setMaxDifficulty(int maxDifficulty) {
        this.maxDifficulty = maxDifficulty;
    }

    // ---- lifecycle: countdown -> game -> main timer ----

    public void startCountdown() {
        // prepare queue + state should be already setup by resetGame()
        stopAllThreads(); // ensure no stray threads
        running = true;
        showingCountdown = true;
        countdownValue = 3;
        repaint();

        countdownThread = new Thread(() -> {
            try {
                while (running && countdownValue >= 0) {
                    // pause-aware wait
                    synchronized (pauseLock) {
                        while (paused && running) {
                            pauseLock.wait();
                        }
                    }
                    if (!running) break;
                    SwingUtilities.invokeLater(this::repaint);
                    Thread.sleep(1000);
                    countdownValue--;
                }

                if (running) {
                    SwingUtilities.invokeLater(() -> {
                        showingCountdown = false;
                        countdownValue = -1;
                        startGame();      // set gameStartMillis, start Swing Timer
                        startMainTimer(); // start the 60s thread
                    });
                }
            } catch (InterruptedException ignored) { }
        }, "CountdownThread");

        countdownThread.setDaemon(true);
        countdownThread.start();
    }

    public void startGame() {
        // start "game time" accounting
        gameStartMillis = System.currentTimeMillis();
        pausedAtMillis = 0;
        totalPausedMillis = 0;
        lastSpawnGameTime = 0;
        speedBoosted = false;

        timer.start(); // start Swing animation timer
    }

    public void startMainTimer() {
        // ensure previous timer thread stopped
        stopTimerThreadIfRunning();

        timeLeftSeconds = 60;
        timerThread = new Thread(() -> {
            try {
                while (running && timeLeftSeconds > 0) {
                    synchronized (pauseLock) {
                        while (paused && running) {
                            pauseLock.wait();
                        }
                    }
                    if (!running) break;
                    SwingUtilities.invokeLater(this::repaint);
                    Thread.sleep(1000);
                    if (!running) break;
                    timeLeftSeconds--;
                }
                if (running) {
                    SwingUtilities.invokeLater(this::endGame);
                }
            } catch (InterruptedException ignored) { }
        }, "TimerThread");

        timerThread.setDaemon(true);
        timerThread.start();
    }

    private void stopTimerThreadIfRunning() {
        if (timerThread != null && timerThread.isAlive()) {
            // signal it to stop and wake if waiting
            running = false;
            synchronized (pauseLock) { pauseLock.notifyAll(); }
            try { timerThread.join(50); } catch (InterruptedException ignored) { }
            // restart running flag if appropriate (caller will set it)
        }
    }

    private void stopAllThreads() {
        // stop threads and wake any waiting ones
        running = false;
        paused = false;
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
        // stop Swing timer
        timer.stop();

        // try joining briefly
        try {
            if (countdownThread != null && countdownThread.isAlive()) countdownThread.join(50);
            if (timerThread != null && timerThread.isAlive()) timerThread.join(50);
        } catch (InterruptedException ignored) { }
    }

    private void updateMultiplier() {
        if (comboStreak >= 100) scoreMultiplier = 4;
        else if (comboStreak >= 50) scoreMultiplier = 3;
        else if (comboStreak >= 25) scoreMultiplier = 2;
        else scoreMultiplier = 1;
    }

    private void endGame() {
        // stop everything
        stopAllThreads();
        gameOver = true;
        repaint();

        // Hitung totalSeconds (guard)
        double totalSeconds = 60 - timeLeftSeconds;
        if (totalSeconds < 0) totalSeconds = 0;

        // WPM & accuracy
        double minutes = totalSeconds / 60.0;
        double wpm = (correctChars / 5.0) / Math.max(0.0001, minutes);
        double accuracy = (totalTyped > 0) ? ((double) correctChars / totalTyped) * 100.0 : 0.0;

        // Kirim ke ScorePanel
        frame.getScorePanel().setTimeSurvive(totalSeconds);
        frame.getScorePanel().setScore(score);
        frame.getScorePanel().setWpm(wpm);
        frame.getScorePanel().setAccuracy(accuracy);

        // Buat Score object
        int userId = frame.getLoggedUser().getId();

        Score scoreObj = new Score(
                userId,
                score,
                wpm,
                accuracy,
                totalSeconds
        );

        // Simpan ke DB
        ScoreDAO dao = new ScoreDAO();
        dao.saveScore(scoreObj);

        frame.showPanel("score");

    }

    public void resetGame() {
        // stop and wake
        stopAllThreads();

        // clear lists
        active.clear();
        queue.clear();

        // load words
        WordDAO dao = new WordDAO();
        List<WordEntry> words = dao.getWordsByMaxDifficulty(maxDifficulty);
        Collections.shuffle(words);

        Random rand = new Random();
        int initVel = velocityForDifficulty();
        boostedVelocity = boostedVelocityForDifficulty();
        for (WordEntry w : words) {
            int randomY = SPAWN_MIN_Y + rand.nextInt(SPAWN_MAX_Y - SPAWN_MIN_Y + 1);
            Image monsterImg;
            switch (w.difficulty) {
                case 2: monsterImg = lich; break;
                case 3: monsterImg = dragon; break;
                default: monsterImg = ghost; break;
            }

            queue.add(new MonsterUnit(monsterImg, 900, randomY, 90, 90, initVel, w.word));
        }

        me = new Player(fort, -30, HEADER_HEIGHT + 0);  // Position below header with padding

        // reset state
        score = 0;
        comboStreak = 0;
        scoreMultiplier = 1;
        totalTyped = 0;
        correctChars = 0;
        timeLeftSeconds = 60;
        speedBoosted = false;
        gameOver = false;
        showingCountdown = false;
        countdownValue = -1;
        paused = false;
        running = false;
        totalPausedMillis = 0;
        pausedAtMillis = 0;

        repaint();
    }

    // pause/resume (maintain pause accounting)
    public void pauseGame() {
        if (!paused) {
            paused = true;
            pausedAtMillis = System.currentTimeMillis();
            SwingUtilities.invokeLater(this::repaint); // langsung tampil overlay
        }
    }

    public void resumeGame() {
        synchronized (pauseLock) {
            if (!paused) return;
            long now = System.currentTimeMillis();
            totalPausedMillis += (now - pausedAtMillis);
            pausedAtMillis = 0;
            paused = false;
            pauseLock.notifyAll(); // wake waiting thread(s)
        }
    }

    private int velocityForDifficulty() {
        switch (maxDifficulty) {
            case 3: return -2; // expert
            case 2: return -1; // normal
            default: return -1; // easy
        }
    }

    private int boostedVelocityForDifficulty() {
        switch (maxDifficulty) {
            case 3: return -3; // expert boosted
            case 2: return -2; // normal boosted
            default: return -1; // easy -> no boost (sama dengan initial)
        }
    }

    private void updateSpawnDelay() {
        long base = 2000;

        // EASY – tidak berubah
        if (maxDifficulty == 1) {
            spawnDelay = base;
            return;
        }

        // NORMAL
        if (maxDifficulty == 2) {
            if (timeLeftSeconds <= 20) {
                spawnDelay = 1500;
            } else if (timeLeftSeconds <= 40) {
                spawnDelay = 1700;
            } else {
                spawnDelay = base;
            }
            return;
        }

        // EXPERT
        if (maxDifficulty == 3) {
            if (timeLeftSeconds <= 20) {
                spawnDelay = 1000;
            } else if (timeLeftSeconds <= 40) {
                spawnDelay = 1300;
            } else {
                spawnDelay = base;
            }
            return;
        }
    }

}
