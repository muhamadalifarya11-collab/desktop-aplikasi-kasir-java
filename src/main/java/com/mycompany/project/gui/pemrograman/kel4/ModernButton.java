package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;

/**
 * Tombol berwarna dengan ikon dan teks, sudut membulat.
 *
 * @author Kelompok 4
 */
public class ModernButton extends JButton {

    private String title;
    private AppIcon.Type iconType;
    private Color base;

    public ModernButton(String title, AppIcon.Type iconType, Color base) {
        this.title = title;
        this.iconType = iconType;
        this.base = base;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setForeground(Color.WHITE);
        setFont(Ui.font(Font.BOLD, 22));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(220, 100));
        setMinimumSize(new Dimension(100, 48));
        getAccessibleContext().setAccessibleName(title);
    }

    /** Mengganti teks, ikon, dan warna tombol sekaligus. */
    public void setContent(String title, AppIcon.Type iconType, Color base) {
        this.title = title;
        this.iconType = iconType;
        this.base = base;
        getAccessibleContext().setAccessibleName(title);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        if (!isEnabled()) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
        }

        Color c = base;
        if (getModel().isPressed()) {
            c = base.darker();
        } else if (getModel().isRollover()) {
            c = mix(base, Color.BLACK, 0.08f);
        }
        int arc = Math.min(16, h / 2);
        g2.setPaint(new GradientPaint(0, 0, mix(c, Color.WHITE, 0.10f), w, h, c));
        g2.fill(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, arc, arc));

        if (isFocusOwner()) {
            g2.setColor(new Color(255, 255, 255, 150));
            g2.draw(new RoundRectangle2D.Double(3, 3, w - 7, h - 7, arc - 4, arc - 4));
        }

        int iconSize = Math.max(18, Math.min(46, (int) (h * 0.42)));
        AppIcon icon = new AppIcon(iconType, iconSize, getForeground());
        FontMetrics fm = g2.getFontMetrics(getFont());
        int gap = iconSize / 2 + 6;
        int totalW = iconSize + gap + fm.stringWidth(title);
        int x0 = (w - totalW) / 2;
        icon.paintIcon(this, g2, x0, (h - iconSize) / 2);

        g2.setColor(getForeground());
        g2.setFont(getFont());
        g2.drawString(title, x0 + iconSize + gap, (h - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }

    private static Color mix(Color a, Color b, float t) {
        return new Color(
                Math.round(a.getRed() + (b.getRed() - a.getRed()) * t),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }
}
