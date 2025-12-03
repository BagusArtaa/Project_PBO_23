package model;

public class Score {

    private int userId;
    private int score;
    private double wpm;
    private double accuracy;
    private double timeSurvive;

    // tambahan untuk leaderboard display
    private String username;

    // dipakai saat endGame()
    public Score(int userId, int score, double wpm,
                 double accuracy, double timeSurvive) {

        this.userId = userId;
        this.score = score;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.timeSurvive = timeSurvive;
    }

    // dipakai untuk leaderboard (username ikut diambil dari JOIN)
    public Score(String username, int score, double wpm,
                 double accuracy, double timeSurvive) {

        this.username = username;
        this.score = score;
        this.wpm = wpm;
        this.accuracy = accuracy;
        this.timeSurvive = timeSurvive;
    }

    public int getUserId() { return userId; }
    public int getScore() { return score; }
    public double getWpm() { return wpm; }
    public double getAccuracy() { return accuracy; }
    public double getTimeSurvive() { return timeSurvive; }
    public String getUsername() { return username; }
}
