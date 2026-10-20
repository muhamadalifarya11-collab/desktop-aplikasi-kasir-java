package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * Dialog pembayaran QRIS. Begitu QR "di-scan" (klik QR atau
 * tombol Scan), pembayaran langsung diproses otomatis dan dialog tertutup
 * sendiri.
 *
 * @author Kelompok 4
 */
public class QrisDialog extends JDialog {

    private static final int BERLAKU_DETIK = 5 * 60;
    private static final Color QRIS_RED = new Color(0xD7263D);

    private enum State {
        WAITING, PROCESSING, SUCCESS, EXPIRED
    }

    private final QrPanel qr;
    private final JLabel lblStatus;
    private final JLabel lblTimer;
    private final ModernButton btnScan;
    private final ModernButton btnBatal;
    private final Timer countdown;
    private int sisaDetik = BERLAKU_DETIK;
    private State state = State.WAITING;
    private boolean paid;

    /**
     * Menampilkan QRIS dan menunggu sampai dibayar atau dibatalkan.
     *
     * @return true jika pembayaran berhasil
     */
    public static boolean bayar(Component parent, long amount, String keterangan) {
        Window owner = parent instanceof Window w ? w : SwingUtilities.getWindowAncestor(parent);
        QrisDialog d = new QrisDialog(owner, amount, keterangan);
        d.setVisible(true);
        return d.paid;
    }

    private QrisDialog(Window owner, long amount, String keterangan) {
        super(owner, "Pembayaran QRIS", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(22, 32, 24, 32));

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("QRIS");
        logo.setFont(Ui.font(Font.BOLD | Font.ITALIC, 40));
        logo.setForeground(QRIS_RED);
        JLabel merchant = new JLabel("WARKOP KELOMPOK 4");
        merchant.setFont(Ui.font(Font.BOLD, 20));
        merchant.setForeground(Ui.TEXT);

        qr = new QrPanel(amount ^ System.nanoTime());
        qr.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                scan();
            }
        });

        JLabel lblTotalTitle = Ui.muted("Total Pembayaran", 15);
        JLabel lblTotal = new JLabel(Ui.rp(amount));
        lblTotal.setFont(Ui.font(Font.BOLD, 32));
        lblTotal.setForeground(Ui.TEXT);
        JLabel lblKet = Ui.muted(keterangan, 14);

        lblStatus = new JLabel("Scan QR untuk membayar");
        lblStatus.setFont(Ui.font(Font.BOLD, 16));
        lblStatus.setForeground(Ui.PRIMARY);
        lblTimer = Ui.muted("", 14);

        for (JComponent c : new JComponent[]{logo, merchant, qr, lblTotalTitle, lblTotal,
            lblKet, lblStatus, lblTimer}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        center.add(logo);
        center.add(merchant);
        center.add(Box.createVerticalStrut(14));
        center.add(qr);
        center.add(Box.createVerticalStrut(12));
        center.add(lblTotalTitle);
        center.add(lblTotal);
        center.add(lblKet);
        center.add(Box.createVerticalStrut(12));
        center.add(lblStatus);
        center.add(Box.createVerticalStrut(2));
        center.add(lblTimer);
        root.add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 14, 0));
        buttons.setOpaque(false);
        buttons.setPreferredSize(new Dimension(0, 56));
        btnBatal = new ModernButton("Batal", AppIcon.Type.REFRESH, Ui.GRAY);
        btnScan = new ModernButton("Scan QR", AppIcon.Type.QR, Ui.TEAL);
        btnBatal.setFont(Ui.font(Font.BOLD, 18));
        btnScan.setFont(Ui.font(Font.BOLD, 18));
        btnBatal.addActionListener(e -> batal());
        btnScan.addActionListener(e -> scan());
        buttons.add(btnBatal);
        buttons.add(btnScan);
        root.add(buttons, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(btnScan);

        countdown = new Timer(1000, e -> tick());
        tick();
        countdown.start();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                batal();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                countdown.stop();
            }
        });

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private void tick() {
        if (state != State.WAITING) {
            return;
        }
        if (sisaDetik <= 0) {
            countdown.stop();
            setState(State.EXPIRED);
            lblStatus.setText("QR sudah kedaluwarsa, silakan ulangi pembayaran");
            lblStatus.setForeground(Ui.RED);
            lblTimer.setText(" ");
            btnScan.setEnabled(false);
            return;
        }
        lblTimer.setText(String.format("Berlaku %02d:%02d", sisaDetik / 60, sisaDetik % 60));
        sisaDetik--;
    }

    /** QR di-scan: langsung diproses dan otomatis dibayar. */
    private void scan() {
        if (state != State.WAITING) {
            return;
        }
        countdown.stop();
        setState(State.PROCESSING);
        btnScan.setEnabled(false);
        btnBatal.setEnabled(false);
        lblStatus.setText("QR terdeteksi, memproses pembayaran...");
        lblStatus.setForeground(Ui.ORANGE);
        lblTimer.setText("Mohon tunggu");

        Timer proses = new Timer(1500, e -> {
            paid = true;
            setState(State.SUCCESS);
            lblStatus.setText("Pembayaran berhasil!");
            lblStatus.setForeground(Ui.GREEN);
            lblTimer.setText("Dibayar pukul " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            Timer tutup = new Timer(1300, e2 -> dispose());
            tutup.setRepeats(false);
            tutup.start();
        });
        proses.setRepeats(false);
        proses.start();
    }

    private void batal() {
        if (state == State.WAITING || state == State.EXPIRED) {
            dispose();
        }
    }

    private void setState(State s) {
        state = s;
        qr.setCursor(Cursor.getPredefinedCursor(s == State.WAITING ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        qr.repaint();
    }

    /** Gambar QR (pola acak + 3 penanda sudut seperti QR asli). */
    private class QrPanel extends JComponent {

        private static final int N = 29;
        private final boolean[][] modules = new boolean[N][N];

        QrPanel(long seed) {
            Random rnd = new Random(seed);
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    modules[r][c] = rnd.nextBoolean();
                }
            }
            setPreferredSize(new Dimension(290, 290));
            setMaximumSize(new Dimension(290, 290));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight());
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(0, 0, size - 1, size - 1, 16, 16));
            g2.setColor(Ui.FIELD_BORDER);
            g2.draw(new RoundRectangle2D.Double(0, 0, size - 1, size - 1, 16, 16));

            int margin = 16;
            int cell = (size - margin * 2) / N;
            int off = (size - cell * N) / 2;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setColor(new Color(0x111827));
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < N; c++) {
                    if (inFinder(r, c) || inLogo(r, c)) {
                        continue;
                    }
                    if (modules[r][c]) {
                        g2.fillRect(off + c * cell, off + r * cell, cell, cell);
                    }
                }
            }
            paintFinder(g2, off, off, cell);
            paintFinder(g2, off + (N - 7) * cell, off, cell);
            paintFinder(g2, off, off + (N - 7) * cell, cell);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Logo kecil di tengah
            int logoSize = cell * 7;
            int lx = off + 11 * cell;
            g2.setColor(Color.WHITE);
            g2.fillRect(lx, lx, logoSize, logoSize);
            g2.setColor(QRIS_RED);
            g2.setFont(Ui.font(Font.BOLD | Font.ITALIC, 15));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("QRIS", lx + (logoSize - fm.stringWidth("QRIS")) / 2,
                    lx + (logoSize - fm.getHeight()) / 2 + fm.getAscent());

            // Lapisan status
            if (state != State.WAITING) {
                g2.setColor(new Color(255, 255, 255, 215));
                g2.fill(new RoundRectangle2D.Double(1, 1, size - 2, size - 2, 16, 16));
                int d = 110;
                int cx = (size - d) / 2;
                Color c = switch (state) {
                    case SUCCESS -> Ui.GREEN;
                    case EXPIRED -> Ui.RED;
                    default -> Ui.ORANGE;
                };
                g2.setColor(c);
                g2.fill(new Ellipse2D.Double(cx, cx, d, d));
                AppIcon.Type t = switch (state) {
                    case SUCCESS -> AppIcon.Type.CHECK;
                    case EXPIRED -> AppIcon.Type.WARNING;
                    default -> AppIcon.Type.REFRESH;
                };
                new AppIcon(t, 60, Color.WHITE).paintIcon(this, g2, cx + 25, cx + 25);
            }
            g2.dispose();
        }

        private boolean inFinder(int r, int c) {
            return (r < 8 && c < 8) || (r < 8 && c >= N - 8) || (r >= N - 8 && c < 8);
        }

        private boolean inLogo(int r, int c) {
            return r >= 11 && r < 18 && c >= 11 && c < 18;
        }

        private void paintFinder(Graphics2D g2, int x, int y, int cell) {
            g2.setColor(new Color(0x111827));
            g2.fillRect(x, y, cell * 7, cell * 7);
            g2.setColor(Color.WHITE);
            g2.fillRect(x + cell, y + cell, cell * 5, cell * 5);
            g2.setColor(new Color(0x111827));
            g2.fillRect(x + cell * 2, y + cell * 2, cell * 3, cell * 3);
        }
    }
}
