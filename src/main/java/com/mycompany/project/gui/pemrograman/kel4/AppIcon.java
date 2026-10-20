package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;

/**
 * Ikon vektor sederhana yang digambar dengan Java2D (tanpa file gambar),
 * sehingga tetap tajam di ukuran berapa pun.
 *
 * @author Kelompok 4
 */
public class AppIcon implements Icon {

    public enum Type {
        STORE, CLOCK, CART, BOX, TAG, MONEY, COUNTER, RECEIPT, WALLET, UNDO,
        CALCULATOR, REFRESH, CHEVRON, CHEVRON_RIGHT, HOME, QR, CARD, PLUS, TRASH, CHECK,
        USER, CHART, WARNING, PHONE, LOCK
    }

    private final Type type;
    private final int size;
    private final Color color;

    public AppIcon(Type type, int size, Color color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);
        // Semua ikon digambar pada kanvas 24 x 24 lalu diskalakan.
        g2.scale(size / 24.0, size / 24.0);
        g2.setColor(color);
        BasicStroke stroke = new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);

        switch (type) {
            case STORE -> paintStore(g2);
            case CLOCK -> {
                g2.draw(new Ellipse2D.Double(3, 3, 18, 18));
                Path2D hand = new Path2D.Double();
                hand.moveTo(12, 7.5);
                hand.lineTo(12, 12);
                hand.lineTo(15.5, 14.5);
                g2.draw(hand);
            }
            case CART -> {
                Path2D basket = new Path2D.Double();
                basket.moveTo(5.6, 6);
                basket.lineTo(21.5, 6);
                basket.lineTo(19.3, 14);
                basket.lineTo(7.4, 14);
                basket.closePath();
                g2.fill(basket);
                Path2D handle = new Path2D.Double();
                handle.moveTo(1.8, 3.2);
                handle.lineTo(4.6, 3.2);
                handle.lineTo(7.4, 17);
                handle.lineTo(19.5, 17);
                g2.draw(handle);
                g2.fill(new Ellipse2D.Double(6.6, 18.6, 3.4, 3.4));
                g2.fill(new Ellipse2D.Double(16.4, 18.6, 3.4, 3.4));
            }
            case BOX -> paintBox(g2);
            case TAG -> {
                Path2D tag = new Path2D.Double();
                tag.moveTo(2, 12.5);
                tag.lineTo(11.5, 3);
                tag.lineTo(21, 3);
                tag.lineTo(21, 12.5);
                tag.lineTo(11.5, 22);
                tag.closePath();
                Area a = new Area(tag);
                a.subtract(new Area(new Ellipse2D.Double(14.5, 6, 3.2, 3.2)));
                g2.fill(a);
            }
            case MONEY -> {
                Area a = new Area(new RoundRectangle2D.Double(1.5, 5, 21, 14, 3, 3));
                a.subtract(new Area(new Ellipse2D.Double(7.5, 7.5, 9, 9)));
                a.add(new Area(new Ellipse2D.Double(10, 10, 4, 4)));
                a.subtract(new Area(new Ellipse2D.Double(3.5, 10.5, 3, 3)));
                a.subtract(new Area(new Ellipse2D.Double(17.5, 10.5, 3, 3)));
                g2.fill(a);
            }
            case COUNTER -> {
                Area a = new Area(new RoundRectangle2D.Double(3.5, 2, 17, 20, 4, 4));
                a.subtract(new Area(new Rectangle2D.Double(7, 6, 3.2, 3.2)));
                a.subtract(new Area(new Rectangle2D.Double(13.8, 6, 3.2, 3.2)));
                a.subtract(new Area(new Rectangle2D.Double(7, 11.2, 3.2, 3.2)));
                a.subtract(new Area(new Rectangle2D.Double(7, 16.2, 3.2, 3.2)));
                a.subtract(new Area(new Rectangle2D.Double(13.8, 11.2, 3.2, 8.2)));
                g2.fill(a);
            }
            case RECEIPT -> {
                Path2D p = new Path2D.Double();
                p.moveTo(4, 2);
                p.lineTo(20, 2);
                p.lineTo(20, 22);
                double[] xs = {18, 16, 14, 12, 10, 8, 6, 4};
                for (int i = 0; i < xs.length; i++) {
                    p.lineTo(xs[i], i % 2 == 0 ? 20.4 : 22);
                }
                p.closePath();
                Area a = new Area(p);
                a.subtract(new Area(new Rectangle2D.Double(7.5, 6, 9, 2.2)));
                a.subtract(new Area(new Rectangle2D.Double(7.5, 10.5, 9, 2.2)));
                a.subtract(new Area(new Rectangle2D.Double(7.5, 15, 5.5, 2.2)));
                g2.fill(a);
            }
            case WALLET -> {
                Path2D flap = new Path2D.Double();
                flap.moveTo(3.5, 7);
                flap.lineTo(17, 2.8);
                flap.lineTo(18.6, 7);
                flap.closePath();
                g2.fill(flap);
                Area a = new Area(new RoundRectangle2D.Double(2, 8, 20, 14, 4, 4));
                a.subtract(new Area(new RoundRectangle2D.Double(14.5, 12.5, 9, 5, 2.5, 2.5)));
                a.add(new Area(new Ellipse2D.Double(16.3, 14, 2, 2)));
                g2.fill(a);
            }
            case UNDO -> {
                g2.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Arc2D.Double(4, 6, 16, 16, 90, -260, Arc2D.OPEN));
                Path2D head = new Path2D.Double();
                head.moveTo(5.5, 6);
                head.lineTo(11.5, 1.8);
                head.lineTo(11.5, 10.2);
                head.closePath();
                g2.fill(head);
            }
            case CALCULATOR -> {
                Area a = new Area(new RoundRectangle2D.Double(4, 1, 16, 22, 4, 4));
                a.subtract(new Area(new RoundRectangle2D.Double(7, 4, 10, 4.5, 1, 1)));
                for (int r = 0; r < 3; r++) {
                    for (int col = 0; col < 3; col++) {
                        a.subtract(new Area(new RoundRectangle2D.Double(
                                7 + col * 3.6, 11 + r * 3.6, 2.6, 2.6, 0.8, 0.8)));
                    }
                }
                g2.fill(a);
            }
            case REFRESH -> {
                g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Arc2D.Double(4, 4, 16, 16, 35, 135, Arc2D.OPEN));
                g2.draw(new Arc2D.Double(4, 4, 16, 16, 215, 135, Arc2D.OPEN));
                Path2D h1 = new Path2D.Double();
                h1.moveTo(0.8, 10.5);
                h1.lineTo(7.2, 10.5);
                h1.lineTo(4, 15);
                h1.closePath();
                g2.fill(h1);
                Path2D h2 = new Path2D.Double();
                h2.moveTo(16.8, 13.5);
                h2.lineTo(23.2, 13.5);
                h2.lineTo(20, 9);
                h2.closePath();
                g2.fill(h2);
            }
            case CHEVRON -> {
                g2.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D p = new Path2D.Double();
                p.moveTo(6, 9);
                p.lineTo(12, 15);
                p.lineTo(18, 9);
                g2.draw(p);
            }
            case CHEVRON_RIGHT -> {
                g2.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D p = new Path2D.Double();
                p.moveTo(9, 5.5);
                p.lineTo(15.5, 12);
                p.lineTo(9, 18.5);
                g2.draw(p);
            }
            case HOME -> {
                g2.setStroke(new BasicStroke(2.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D roof = new Path2D.Double();
                roof.moveTo(2, 11.5);
                roof.lineTo(12, 3);
                roof.lineTo(22, 11.5);
                g2.draw(roof);
                Area body = new Area(new Rectangle2D.Double(5, 10.5, 14, 11));
                body.subtract(new Area(new Rectangle2D.Double(10, 15, 4, 6.5)));
                g2.fill(body);
            }
            case QR -> {
                double[][] corners = {{2, 2}, {14, 2}, {2, 14}};
                for (double[] p : corners) {
                    Area a = new Area(new RoundRectangle2D.Double(p[0], p[1], 8, 8, 2, 2));
                    a.subtract(new Area(new Rectangle2D.Double(p[0] + 1.8, p[1] + 1.8, 4.4, 4.4)));
                    a.add(new Area(new Rectangle2D.Double(p[0] + 3, p[1] + 3, 2, 2)));
                    g2.fill(a);
                }
                g2.fill(new Rectangle2D.Double(14, 14, 3.2, 3.2));
                g2.fill(new Rectangle2D.Double(18.8, 14, 3.2, 3.2));
                g2.fill(new Rectangle2D.Double(16.4, 16.6, 3.2, 3.2));
                g2.fill(new Rectangle2D.Double(14, 18.8, 3.2, 3.2));
                g2.fill(new Rectangle2D.Double(18.8, 18.8, 3.2, 3.2));
            }
            case CARD -> {
                Area a = new Area(new RoundRectangle2D.Double(1.5, 4.5, 21, 15, 3.5, 3.5));
                a.subtract(new Area(new Rectangle2D.Double(1.5, 8, 21, 2.8)));
                a.subtract(new Area(new RoundRectangle2D.Double(4.5, 14, 6.5, 2.4, 1.2, 1.2)));
                g2.fill(a);
            }
            case PLUS -> {
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Line2D.Double(12, 4.5, 12, 19.5));
                g2.draw(new Line2D.Double(4.5, 12, 19.5, 12));
            }
            case TRASH -> {
                g2.fill(new RoundRectangle2D.Double(3, 4.5, 18, 2.6, 1.2, 1.2));
                g2.fill(new RoundRectangle2D.Double(9, 2, 6, 3.5, 1, 1));
                Path2D body = new Path2D.Double();
                body.moveTo(5, 8.5);
                body.lineTo(19, 8.5);
                body.lineTo(17.6, 22);
                body.lineTo(6.4, 22);
                body.closePath();
                Area a = new Area(body);
                a.subtract(new Area(new Rectangle2D.Double(9, 11, 1.8, 8)));
                a.subtract(new Area(new Rectangle2D.Double(13.2, 11, 1.8, 8)));
                g2.fill(a);
            }
            case CHECK -> {
                g2.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D p = new Path2D.Double();
                p.moveTo(4.5, 12.5);
                p.lineTo(10, 18);
                p.lineTo(19.5, 6.5);
                g2.draw(p);
            }
            case USER -> {
                g2.fill(new Ellipse2D.Double(7.5, 2, 9, 9));
                Area body = new Area(new Ellipse2D.Double(3, 13, 18, 18));
                body.intersect(new Area(new Rectangle2D.Double(0, 0, 24, 22)));
                g2.fill(body);
            }
            case CHART -> {
                g2.fill(new RoundRectangle2D.Double(3, 13, 4.4, 8.5, 1.5, 1.5));
                g2.fill(new RoundRectangle2D.Double(9.8, 8, 4.4, 13.5, 1.5, 1.5));
                g2.fill(new RoundRectangle2D.Double(16.6, 3, 4.4, 18.5, 1.5, 1.5));
            }
            case WARNING -> {
                Path2D tri = new Path2D.Double();
                tri.moveTo(12, 2.5);
                tri.lineTo(22.5, 21);
                tri.lineTo(1.5, 21);
                tri.closePath();
                Area a = new Area(tri);
                a.subtract(new Area(new RoundRectangle2D.Double(10.8, 8.5, 2.4, 7, 1.2, 1.2)));
                a.subtract(new Area(new Ellipse2D.Double(10.7, 16.6, 2.6, 2.6)));
                g2.fill(a);
            }
            case PHONE -> {
                Area a = new Area(new RoundRectangle2D.Double(5.5, 1.5, 13, 21, 4, 4));
                a.subtract(new Area(new RoundRectangle2D.Double(7.7, 4.3, 8.6, 12.5, 1, 1)));
                a.subtract(new Area(new Ellipse2D.Double(10.8, 18.2, 2.4, 2.4)));
                g2.fill(a);
            }
            case LOCK -> {
                g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Arc2D.Double(7, 3, 10, 11, 0, 180, Arc2D.OPEN));
                g2.draw(new Line2D.Double(7, 8.5, 7, 11));
                g2.draw(new Line2D.Double(17, 8.5, 17, 11));
                Area body = new Area(new RoundRectangle2D.Double(4, 10.5, 16, 11.5, 3, 3));
                body.subtract(new Area(new Ellipse2D.Double(10.4, 14, 3.2, 3.2)));
                body.subtract(new Area(new Rectangle2D.Double(11.3, 16, 1.4, 3)));
                g2.fill(body);
            }
        }
        g2.dispose();
    }

    private void paintStore(Graphics2D g2) {
        // Atap kanopi bergelombang
        for (int i = 0; i < 4; i++) {
            double sx = 1.5 + i * 5.3;
            Area seg = new Area(new RoundRectangle2D.Double(sx, 2.5, 4.9, 5.5, 1.6, 1.6));
            seg.add(new Area(new Ellipse2D.Double(sx, 5.2, 4.9, 4.9)));
            g2.fill(seg);
        }
        // Badan toko dengan pintu
        Area body = new Area(new Rectangle2D.Double(3.5, 11.5, 17, 10));
        body.subtract(new Area(new Rectangle2D.Double(6, 14, 7, 7.5)));
        g2.fill(body);
        g2.draw(new Line2D.Double(3.5, 21.5, 20.5, 21.5));
    }

    private void paintBox(Graphics2D g2) {
        Path2D top = new Path2D.Double();
        top.moveTo(12, 1.8);
        top.lineTo(21.2, 6.5);
        top.lineTo(12, 11.2);
        top.lineTo(2.8, 6.5);
        top.closePath();
        g2.setColor(brighter(color, 0.35f));
        g2.fill(top);

        Path2D left = new Path2D.Double();
        left.moveTo(2.8, 7.8);
        left.lineTo(11.3, 12.2);
        left.lineTo(11.3, 22.4);
        left.lineTo(2.8, 18);
        left.closePath();
        g2.setColor(color);
        g2.fill(left);

        Path2D right = new Path2D.Double();
        right.moveTo(12.7, 12.2);
        right.lineTo(21.2, 7.8);
        right.lineTo(21.2, 18);
        right.lineTo(12.7, 22.4);
        right.closePath();
        g2.setColor(color.darker());
        g2.fill(right);
    }

    private static Color brighter(Color c, float amount) {
        int r = (int) (c.getRed() + (255 - c.getRed()) * amount);
        int g = (int) (c.getGreen() + (255 - c.getGreen()) * amount);
        int b = (int) (c.getBlue() + (255 - c.getBlue()) * amount);
        return new Color(r, g, b, c.getAlpha());
    }
}
