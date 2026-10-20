package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Panel dengan sudut membulat, garis tepi, dan bayangan halus (opsional).
 *
 * @author Kelompok 4
 */
public class RoundedPanel extends JPanel {

    private static final int SHADOW = 6;

    private final int radius;
    private final boolean shadow;
    private Color fill;
    private Color borderColor;
    private float borderWidth = 1.2f;

    public RoundedPanel(LayoutManager layout, int radius, Color fill, Color borderColor,
            boolean shadow, int padding) {
        super(layout);
        this.radius = radius;
        this.fill = fill;
        this.borderColor = borderColor;
        this.shadow = shadow;
        setOpaque(false);
        int s = shadow ? SHADOW : 0;
        setBorder(BorderFactory.createEmptyBorder(padding + s, padding + s, padding + s, padding + s));
    }

    public void setFill(Color fill) {
        this.fill = fill;
        repaint();
    }

    public void setBorderColor(Color borderColor, float width) {
        this.borderColor = borderColor;
        this.borderWidth = width;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        int s = shadow ? SHADOW : 0;

        if (shadow) {
            for (int i = 0; i < s; i++) {
                g2.setColor(new Color(25, 60, 120, 6));
                g2.fill(new RoundRectangle2D.Double(i, i + 2, w - 2.0 * i, h - 2.0 * i - 2,
                        radius + s - i, radius + s - i));
            }
        }

        RoundRectangle2D shape = new RoundRectangle2D.Double(s, s, w - 2.0 * s - 1, h - 2.0 * s - 1,
                radius, radius);
        g2.setColor(fill);
        g2.fill(shape);
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            g2.draw(shape);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
