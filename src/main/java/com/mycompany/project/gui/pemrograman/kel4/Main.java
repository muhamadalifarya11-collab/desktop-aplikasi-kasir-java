package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

/**
 * Jendela utama Kasir Warkop Kelompok 4. Berisi header dan beberapa halaman
 * yang ditukar dengan CardLayout.
 *
 * @author Kelompok 4
 */
public class Main extends JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Main.class.getName());

    public static final String HOME = "home";
    public static final String POS = "pos";
    public static final String PRODUK = "produk";
    public static final String PAYLATER = "paylater";
    public static final String RIWAYAT = "riwayat";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Ui.ID);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final CardLayout cards = new CardLayout();
    private final JPanel pages = new JPanel(cards);
    private final Map<String, Page> pageMap = new LinkedHashMap<>();
    private final Map<String, String> pageTitle = new LinkedHashMap<>();
    private JLabel lblSubtitle;
    private JLabel lblTanggal;
    private JLabel lblJam;
    private ModernButton btnMenu;

    public Main() {
        initComponents();
        startClock();
        navigate(HOME);
    }

    private void initComponents() {
        setTitle("Kasir Warkop Kelompok 4");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(createWindowIcon());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Ui.BG);
        root.add(createHeader(), BorderLayout.NORTH);

        pages.setOpaque(false);
        addPage(HOME, "Menu Utama", new HomePage(this::navigate));
        addPage(POS, "Kasir (POS)", new PosPage());
        addPage(PRODUK, "Kelola Produk", new ProductPage());
        addPage(PAYLATER, "Kelola PayLater", new PayLaterPage());
        addPage(RIWAYAT, "Riwayat Transaksi", new HistoryPage());
        root.add(pages, BorderLayout.CENTER);

        setContentPane(root);
        setMinimumSize(new Dimension(1180, 820));
        setSize(1300, 880);
        setLocationRelativeTo(null);
    }

    private void addPage(String key, String title, JComponent page) {
        pages.add(page, key);
        pageMap.put(key, (Page) page);
        pageTitle.put(key, title);
    }

    /** Pindah ke halaman lain. */
    public void navigate(String key) {
        cards.show(pages, key);
        pageMap.get(key).onShow();
        btnMenu.setVisible(!HOME.equals(key));
        lblSubtitle.setText(HOME.equals(key) ? "GUI Swings - Unpam Loh Yaa - My Dosen AGUS HERI YUNIAL S.Kom., M.Kom." : pageTitle.get(key));
    }

    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setPaint(new GradientPaint(0, 0, new Color(0x1C55B0), w, h, new Color(0x2C74DB)));
                g2.fillRect(0, 0, w, h);
                // Gelombang dekoratif
                Path2D wave = new Path2D.Double();
                wave.moveTo(w * 0.52, h);
                wave.curveTo(w * 0.68, h * 0.55, w * 0.82, h * 0.15, w, h * 0.05);
                wave.lineTo(w, h);
                wave.closePath();
                g2.setColor(new Color(255, 255, 255, 22));
                g2.fill(wave);
                Path2D wave2 = new Path2D.Double();
                wave2.moveTo(w * 0.70, h);
                wave2.curveTo(w * 0.80, h * 0.70, w * 0.90, h * 0.45, w, h * 0.40);
                wave2.lineTo(w, h);
                wave2.closePath();
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fill(wave2);
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(0, 140));
        header.setBorder(BorderFactory.createEmptyBorder(16, 46, 16, 34));

        // Kiri: ikon + judul
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        JLabel logo = new JLabel(new AppIcon(AppIcon.Type.STORE, 86, Color.WHITE));
        logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 36));
        left.add(logo);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("KASIR WARKOP KELOMPOK 4");
        title.setFont(Ui.font(Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        lblSubtitle = new JLabel(" ");
        lblSubtitle.setFont(Ui.font(Font.PLAIN, 23));
        lblSubtitle.setForeground(new Color(0xD6E4FA));
        lblSubtitle.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        titles.add(title);
        titles.add(lblSubtitle);
        left.add(titles);

        JPanel leftWrap = new JPanel(new GridBagLayout());
        leftWrap.setOpaque(false);
        leftWrap.add(left);

        // Kanan: tombol menu utama + jam
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        btnMenu = new ModernButton("Menu Utama", AppIcon.Type.HOME, new Color(0x1746A0));
        btnMenu.setFont(Ui.font(Font.BOLD, 18));
        btnMenu.setPreferredSize(new Dimension(200, 54));
        btnMenu.addActionListener(e -> navigate(HOME));
        JPanel menuWrap = new JPanel(new BorderLayout());
        menuWrap.setOpaque(false);
        menuWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 34));
        menuWrap.add(btnMenu);

        JLabel clockIcon = new JLabel(new AppIcon(AppIcon.Type.CLOCK, 44, Color.WHITE));
        clockIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
        JPanel clockText = new JPanel(new GridLayout(2, 1, 0, 4));
        clockText.setOpaque(false);
        lblTanggal = new JLabel(" ");
        lblTanggal.setFont(Ui.font(Font.BOLD, 18));
        lblTanggal.setForeground(Color.WHITE);
        lblJam = new JLabel(" ");
        lblJam.setFont(Ui.font(Font.PLAIN, 19));
        lblJam.setForeground(new Color(0xE3ECFB));
        clockText.add(lblTanggal);
        clockText.add(lblJam);
        right.add(menuWrap);
        right.add(clockIcon);
        right.add(clockText);

        header.add(leftWrap, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private void startClock() {
        Runnable tick = () -> {
            LocalDateTime now = LocalDateTime.now();
            lblTanggal.setText(now.format(DATE_FMT));
            lblJam.setText(now.format(TIME_FMT));
        };
        tick.run();
        new Timer(1000, e -> tick.run()).start();
    }

    private static BufferedImage createWindowIcon() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        new AppIcon(AppIcon.Type.STORE, 64, Ui.PRIMARY).paintIcon(null, g, 0, 0);
        g.dispose();
        return img;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.WARNING, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new Main().setVisible(true));
    }
}
