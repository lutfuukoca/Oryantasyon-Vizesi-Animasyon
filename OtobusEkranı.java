import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class OtobusEkranı extends JPanel {

    static final String[] DURAKLAR = {
        "Teknoloji Fakültesi",
        "Tıp Fakültesi",
        "Mühendislik Fakültesi"
    };
    static final long DURAK_SURESI_MS = 5000;

    // Arka plan binaları için sabit yükseklikler
    static final int[] BINA = {90, 140, 110, 170, 100, 150, 120, 180};

    private final long baslangic = System.currentTimeMillis();
    private double yolOfset = 0;
    private double tekerAci = 0;

    public OtobusEkranı() {
        setPreferredSize(new Dimension(900, 450));
        new Timer(16, e -> {
            yolOfset += 6;
            tekerAci += 0.22;
            repaint();
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int W = getWidth(), H = getHeight();
        int roadY = (int) (H * 0.72);

        long gecen = System.currentTimeMillis() - baslangic;
        int durakNo = (int) (gecen / DURAK_SURESI_MS) % DURAKLAR.length;
        long durakIcindeki = gecen % DURAK_SURESI_MS;

        cizGokyuzu(g, W, roadY);
        cizBinalar(g, W, roadY);
        cizAgaclar(g, W, roadY);
        cizYol(g, W, H, roadY);
        cizOtobus(g, W, roadY, DURAKLAR[durakNo], durakIcindeki);
        cizIlerleme(g, W, durakNo, durakIcindeki);

        g.dispose();
    }

    private void cizGokyuzu(Graphics2D g, int W, int roadY) {
        g.setPaint(new GradientPaint(0, 0, new Color(95, 170, 240), 0, roadY, new Color(210, 235, 255)));
        g.fillRect(0, 0, W, roadY);
        g.setColor(new Color(255, 230, 120));
        g.fillOval(W - 140, 40, 70, 70);
        // Bulutlar (yavaş kayar)
        g.setColor(new Color(255, 255, 255, 220));
        for (int i = 0; i < 3; i++) {
            int x = (int) ((i * 400 - yolOfset * 0.15) % (W + 200));
            if (x < -150) x += W + 200;
            g.fillOval(x, 50 + i * 25, 90, 30);
            g.fillOval(x + 25, 38 + i * 25, 60, 36);
            g.fillOval(x + 55, 52 + i * 25, 80, 28);
        }
    }

    private void cizBinalar(Graphics2D g, int W, int roadY) {
        int genislik = 110;
        int toplam = BINA.length * genislik;
        double ofs = (yolOfset * 0.35) % toplam;
        g.setColor(new Color(120, 140, 170));
        for (int i = -1; i <= W / genislik + BINA.length; i++) {
            int idx = Math.floorMod(i, BINA.length);
            int x = (int) (i * genislik - ofs);
            int h = BINA[idx];
            g.setColor(new Color(120, 140, 170));
            g.fillRect(x, roadY - h, genislik - 12, h);
            g.setColor(new Color(235, 240, 150));
            for (int wy = roadY - h + 15; wy < roadY - 20; wy += 28)
                for (int wx = x + 12; wx < x + genislik - 30; wx += 26)
                    g.fillRect(wx, wy, 12, 14);
        }
    }

    private void cizAgaclar(Graphics2D g, int W, int roadY) {
        int aralik = 230;
        double ofs = (yolOfset * 0.75) % aralik;
        for (int i = -1; i <= W / aralik + 1; i++) {
            int x = (int) (i * aralik - ofs) + 60;
            g.setColor(new Color(110, 75, 40));
            g.fillRect(x - 5, roadY - 55, 10, 55);
            g.setColor(new Color(50, 150, 70));
            g.fillOval(x - 32, roadY - 105, 64, 64);
            g.setColor(new Color(65, 175, 85));
            g.fillOval(x - 22, roadY - 120, 44, 44);
        }
    }

    private void cizYol(Graphics2D g, int W, int H, int roadY) {
        g.setColor(new Color(150, 200, 120));
        g.fillRect(0, roadY - 6, W, 10);
        g.setColor(new Color(60, 60, 65));
        g.fillRect(0, roadY + 4, W, H - roadY);
        g.setColor(new Color(240, 240, 240));
        int cizgi = 70, bosluk = 50, periyot = cizgi + bosluk;
        double ofs = yolOfset % periyot;
        int cy = roadY + (H - roadY) / 2 + 30;
        for (int x = (int) -ofs - periyot; x < W; x += periyot)
            g.fillRoundRect(x, cy, cizgi, 8, 4, 4);
    }

    private void cizOtobus(Graphics2D g, int W, int roadY, String durak, long durakIcindeki) {
        int bw = 440, bh = 160;
        int bx = W / 2 - bw / 2;
        int bounce = (int) (Math.sin(yolOfset * 0.12) * 2);
        int by = roadY + 20 - bh + bounce;

        // Gölge
        g.setColor(new Color(0, 0, 0, 70));
        g.fillOval(bx + 10, roadY + 28, bw - 20, 18);

        // Gövde
        g.setColor(new Color(245, 170, 30));
        g.fillRoundRect(bx, by, bw, bh, 36, 36);
        g.setColor(new Color(200, 120, 10));
        g.fillRect(bx, by + bh - 28, bw, 10);

        // LED durak ekranı
        int ex = bx + 24, ey = by + 12, ew = bw - 48, eh = 44;
        g.setColor(new Color(15, 15, 18));
        g.fillRoundRect(ex, ey, ew, eh, 12, 12);
        boolean yanik = durakIcindeki > 600 || (durakIcindeki / 150) % 2 == 0; // geçişte yanıp söner
        if (yanik) {
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
            g.setColor(new Color(255, 150, 0, 170));
            g.drawString("SIRADAKİ DURAK", ex + 12, ey + 17);
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
            g.setColor(new Color(255, 190, 40));
            g.drawString(durak, ex + 12, ey + 38);
        }

        // Pencereler
        g.setColor(new Color(170, 215, 245));
        int pw = 62, px = bx + 24;
        for (int i = 0; i < 5; i++) {
            g.fillRoundRect(px + i * (pw + 10), by + 68, pw, 46, 10, 10);
        }
        // Kapı
        g.setColor(new Color(130, 190, 225));
        g.fillRoundRect(bx + bw - 90, by + 68, 60, 76, 10, 10);
        g.setColor(new Color(90, 130, 160));
        g.drawLine(bx + bw - 60, by + 68, bx + bw - 60, by + 144);

        // Far
        g.setColor(new Color(255, 250, 200));
        g.fillOval(bx + bw - 16, by + bh - 55, 14, 18);

        // Tekerlekler
        cizTekerlek(g, bx + 95, roadY + 20 + bounce);
        cizTekerlek(g, bx + bw - 120, roadY + 20 + bounce);
    }

    private void cizTekerlek(Graphics2D g, int cx, int cy) {
        int r = 27;
        g.setColor(new Color(25, 25, 25));
        g.fillOval(cx - r, cy - r, 2 * r, 2 * r);
        g.setColor(new Color(190, 190, 195));
        g.fillOval(cx - 15, cy - 15, 30, 30);
        g.setColor(new Color(80, 80, 85));
        g.setStroke(new BasicStroke(3f));
        for (int i = 0; i < 4; i++) {
            double a = tekerAci + i * Math.PI / 2;
            g.drawLine(cx, cy, (int) (cx + Math.cos(a) * 14), (int) (cy + Math.sin(a) * 14));
        }
        g.setStroke(new BasicStroke(1f));
    }

    // Üstte durak noktaları ve 5 saniyelik ilerleme çubuğu
    private void cizIlerleme(Graphics2D g, int W, int durakNo, long durakIcindeki) {
        int n = DURAKLAR.length;
        int aralik = 150;
        int x0 = 30;
        int y = 28;
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        for (int i = 0; i < n; i++) {
            int x = x0 + i * aralik;
            if (i < n - 1) {
                g.setColor(new Color(255, 255, 255, 140));
                g.fillRoundRect(x + 10, y - 2, aralik - 20, 4, 4, 4);
                if (i == durakNo) {
                    g.setColor(new Color(255, 120, 0));
                    int dolu = (int) ((aralik - 20) * durakIcindeki / (double) DURAK_SURESI_MS);
                    g.fillRoundRect(x + 10, y - 2, dolu, 4, 4, 4);
                }
            }
            g.setColor(i <= durakNo ? new Color(255, 120, 0) : Color.WHITE);
            g.fillOval(x - 7, y - 7, 14, 14);
            g.setColor(new Color(30, 40, 60));
            g.drawString(DURAKLAR[i], x - 7, y + 24);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Otobüs Ekranı");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new OtobusEkranı());
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}