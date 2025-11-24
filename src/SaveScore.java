import javax.swing.*;

public class SaveScore extends SwingWorker<Boolean, Void> {
    
    private KoneksiDatabase koneksi;
    private int userId;
    private double wpm;
    private double accuracy;

    public SaveScore(KoneksiDatabase koneksi, int userId, double wpm, double accuracy) {
        this.koneksi = koneksi;
        this.userId = userId;
        this.wpm = wpm;
        this.accuracy = accuracy;
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        return koneksi.saveScore(userId, wpm, accuracy);
    }
    
    @Override
    protected void done() {
        try {
            if (get()) {
                System.out.println("Skor berhasil disimpan ke DB.");
            } else {
                System.err.println("Gagal menyimpan skor.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error saat menyimpan skor: " + e.getMessage());
        }
    }
}