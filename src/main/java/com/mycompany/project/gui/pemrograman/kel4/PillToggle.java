package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JToggleButton;

/**
 * Tombol pilihan (toggle) berbentuk kapsul, dipakai untuk memilih metode
 * pembayaran.
 *
 * @author Kelompok 4
 */
public class PillToggle extends JToggleButton {

    private final AppIcon.Type iconType;
    private final Color accent;

    public PillToggle(String text, AppIcon.Type iconType, Color accent) {
        super(text);
        this.iconType = iconType;
        this.accent = accent;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setFont(Ui.font(Font.BOLD, 17));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        RoundRectangle2D shape = new RoundRectangle2D.Double(1, 1, w - 3, h - 3, 12, 12);
        boolean on = isSelected();
        Color fg = on ? Color.WHITE : Ui.TEXT;

        g2.setColor(on ? accent : Color.WHITE);
        g2.fill(shape);
        g2.setStroke(new BasicStroke(on || getModel().isRollover() || isFocusOwner() ? 2f : 1.2f));
        g2.setColor(on || getModel().isRollover() || isFocusOwner() ? accent : Ui.FIELD_BORDER);
        g2.draw(shape);

        int iconSize = 22;
        AppIcon icon = new AppIcon(iconType, iconSize, on ? Color.WHITE : accent);
        FontMetrics fm = g2.getFontMetrics(getFont());
        int total = iconSize + 10 + fm.stringWidth(getText());
        int x0 = (w - total) / 2;
        icon.paintIcon(this, g2, x0, (h - iconSize) / 2);
        g2.setColor(fg);
        g2.setFont(getFont());
        g2.drawString(getText(), x0 + iconSize + 10, (h - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }
}
