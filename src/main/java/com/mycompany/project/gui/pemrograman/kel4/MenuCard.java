package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Kartu menu di halaman utama. Klik (atau Enter) untuk membuka halaman.
 *
 * @author Kelompok 4
 */
public class MenuCard extends JPanel {

    private static final int SHADOW = 6;
    private final Color accent;
    private boolean hover;

    public MenuCard(String title, String description, AppIcon.Type icon, Color accent, Runnable action) {
        super(new BorderLayout(26, 0));
        this.accent = accent;
        setOpaque(false);
        setFocusable(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(24 + SHADOW, 34 + SHADOW, 24 + SHADOW, 30 + SHADOW));

        add(new JLabel(Ui.badge(icon, 92, accent)), BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(Ui.font(Font.BOLD, 26));
        lblTitle.setForeground(Ui.TEXT);
        JLabel lblDesc = new JLabel("<html>" + description + "</html>");
        lblDesc.setFont(Ui.font(Font.PLAIN, 17));
        lblDesc.setForeground(Ui.MUTED);
        text.add(Box.createVerticalGlue());
        text.add(lblTitle);
        text.add(Box.createVerticalStrut(6));
        text.add(lblDesc);
        text.add(Box.createVerticalGlue());
        add(text, BorderLayout.CENTER);

        add(new JLabel(new AppIcon(AppIcon.Type.CHEVRON_RIGHT, 30, accent)), BorderLayout.EAST);

        getAccessibleContext().setAccessibleName(title);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    action.run();
                }
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        int s = SHADOW;
        int lift = hover ? 2 : 0;
        for (int i = 0; i < s; i++) {
            g2.setColor(new Color(25, 60, 120, hover ? 9 : 6));
            g2.fill(new RoundRectangle2D.Double(i, i + 2, w - 2.0 * i, h - 2.0 * i - 2, 22 + s - i, 22 + s - i));
        }
        RoundRectangle2D shape = new RoundRectangle2D.Double(s, s - lift, w - 2.0 * s - 1, h - 2.0 * s - 1, 22, 22);
        g2.setColor(Color.WHITE);
        g2.fill(shape);

        // Garis aksen di sisi kiri
        g2.setClip(shape);
        g2.setColor(accent);
        g2.fillRect(s, s - lift, 7, h);
        g2.setClip(null);

        if (hover || isFocusOwner()) {
            g2.setColor(accent);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(shape);
        }
        g2.dispose();
    }
}
