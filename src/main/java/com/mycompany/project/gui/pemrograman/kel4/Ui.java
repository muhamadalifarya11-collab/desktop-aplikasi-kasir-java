package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Ellipse2D;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

/**
 * Kumpulan warna, font, format, dan pembuat komponen yang dipakai semua halaman
 * agar tampilannya seragam.
 *
 * @author Kelompok 4
 */
public final class Ui {

    // ===== Warna =====
    public static final Color PRIMARY = new Color(0x2563C9);
    public static final Color BLUE = new Color(0x2477E8);
    public static final Color GREEN = new Color(0x1FA35B);
    public static final Color RED = new Color(0xEF4B55);
    public static final Color ORANGE = new Color(0xF08C1A);
    public static final Color PURPLE = new Color(0x7B4DE0);
    public static final Color TEAL = new Color(0x0E9F9A);
    public static final Color GRAY = new Color(0x64748B);
    public static final Color BG = new Color(0xEEF3FA);
    public static final Color CARD = Color.WHITE;
    public static final Color TEXT = new Color(0x1F2A44);
    public static final Color MUTED = new Color(0x7A8AA3);
    public static final Color FIELD_BORDER = new Color(0xC6D1E0);
    public static final Color FIELD_READONLY = new Color(0xE8EDF4);
    public static final Color HEADER_BG = new Color(0xDCE9FB);
    public static final Color SELECTION = new Color(0xD6E6FF);

    public static final String FONT = "Segoe UI";

    // ===== Format =====
    public static final Locale ID = Locale.of("id", "ID");
    private static final NumberFormat NUMBER = NumberFormat.getIntegerInstance(ID);
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d MMM yyyy", ID);
    public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm", ID);

    private Ui() {
    }

    public static Font font(int style, int size) {
        return new Font(FONT, style, size);
    }

    /** 15000 -> "15.000" */
    public static String num(long value) {
        return NUMBER.format(value);
    }

    /** 15000 -> "Rp 15.000" */
    public static String rp(long value) {
        return "Rp " + NUMBER.format(value);
    }

    /** Mengambil angka saja dari teks, "15.000" -> 15000. */
    public static long parse(String text) {
        String digits = text == null ? "" : text.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return 0;
        }
        return digits.length() > 15 ? Long.MAX_VALUE : Long.parseLong(digits);
    }

    // =====================================================================
    // Komponen
    // =====================================================================
    public static RoundedPanel card(LayoutManager layout, int padding) {
        return new RoundedPanel(layout, 18, CARD, null, true, padding);
    }

    public static JLabel cardTitle(String text, AppIcon.Type icon) {
        JLabel label = new JLabel(text, new AppIcon(icon, 36, PRIMARY), SwingConstants.LEFT);
        label.setIconTextGap(18);
        label.setFont(font(Font.BOLD, 24));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel formLabel(String text, AppIcon.Type icon) {
        JLabel label = new JLabel(text, new AppIcon(icon, 28, PRIMARY), SwingConstants.LEFT);
        label.setIconTextGap(20);
        label.setFont(font(Font.BOLD, 18));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel muted(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(font(Font.PLAIN, size));
        label.setForeground(MUTED);
        return label;
    }

    /**
     * Membuat text field polos (tanpa border) yang nantinya dibungkus
     * {@link #wrap}.
     *
     * @param maxDigits batas jumlah digit, 0 berarti teks bebas
     */
    public static JTextField textField(String text, boolean editable, int maxDigits) {
        JTextField field = new JTextField(text);
        styleText(field, editable);
        if (maxDigits > 0) {
            digitsOnly(field, maxDigits);
        }
        return field;
    }

    public static void styleText(JTextField field, boolean editable) {
        field.setFont(font(Font.PLAIN, 19));
        field.setForeground(TEXT);
        field.setCaretColor(PRIMARY);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder());
        field.setEditable(editable);
        field.setFocusable(editable);
    }

    public static void digitsOnly(JTextComponent field, int maxDigits) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DigitFilter(maxDigits, false));
    }

    /** Seperti {@link #digitsOnly}, tetapi titik pemisah ribuan (15.000) juga diterima. */
    public static void moneyOnly(JTextComponent field, int maxLength) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DigitFilter(maxLength, true));
    }

    /** Membungkus komponen input dalam kotak bersudut membulat. */
    public static RoundedPanel wrap(JComponent comp, boolean readOnly, boolean showRp) {
        RoundedPanel box = new RoundedPanel(new BorderLayout(10, 0), 10,
                readOnly ? FIELD_READONLY : Color.WHITE, FIELD_BORDER, false, 0);
        box.setBorder(BorderFactory.createEmptyBorder(0, comp instanceof JComboBox ? 10 : 18, 0, 16));
        box.setPreferredSize(new Dimension(220, 50));
        box.setMinimumSize(new Dimension(120, 50));
        box.add(comp, BorderLayout.CENTER);
        if (showRp) {
            JLabel rp = new JLabel("Rp");
            rp.setFont(font(Font.PLAIN, 19));
            rp.setForeground(MUTED);
            box.add(rp, BorderLayout.EAST);
        }
        comp.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                box.setBorderColor(PRIMARY, 2f);
                if (comp instanceof JTextField tf && tf.isEditable()) {
                    SwingUtilities.invokeLater(tf::selectAll);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                box.setBorderColor(FIELD_BORDER, 1.2f);
            }
        });
        return box;
    }

    /** Menambahkan baris "label : input" ke panel ber-GridBagLayout. */
    public static void addRow(JPanel panel, int row, JComponent label, JComponent input, int labelWidth) {
        GridBagConstraints c = new GridBagConstraints();
        label.setPreferredSize(new Dimension(labelWidth, 40));
        c.gridy = row;
        c.gridx = 0;
        c.weighty = 1;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 6, 0, 10);
        panel.add(label, c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 5, 4);
        panel.add(input, c);
    }

    public static <T> void styleCombo(JComboBox<T> combo) {
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton(new AppIcon(AppIcon.Type.CHEVRON, 18, TEXT));
                b.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
                b.setContentAreaFilled(false);
                b.setFocusPainted(false);
                b.setOpaque(false);
                return b;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                // transparan: latar digambar oleh RoundedPanel
            }

            @Override
            public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
                Component c = comboBox.getRenderer().getListCellRendererComponent(
                        listBox, comboBox.getSelectedItem(), -1, false, false);
                c.setFont(comboBox.getFont());
                c.setForeground(comboBox.getForeground());
                currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height,
                        c instanceof JPanel);
            }
        });
        combo.setFont(font(Font.PLAIN, 19));
        combo.setForeground(TEXT);
        combo.setBackground(Color.WHITE);
        combo.setOpaque(false);
        combo.setBorder(BorderFactory.createEmptyBorder());
        combo.setMaximumRowCount(10);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                setOpaque(index != -1);
                if (index != -1) {
                    setBackground(isSelected ? HEADER_BG : Color.WHITE);
                    setForeground(TEXT);
                }
                return this;
            }
        });
    }

    /** Memberi gaya tabel yang seragam dan mengembalikan scroll pane-nya. */
    public static JScrollPane styleTable(JTable table, int[] align, int[] widths) {
        table.setFont(font(Font.PLAIN, 17));
        table.setRowHeight(46);
        table.setShowGrid(true);
        table.setGridColor(new Color(0xE3E9F2));
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 52));
        table.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        for (int i = 0; i < align.length; i++) {
            if (!(table.getColumnModel().getColumn(i).getCellRenderer() instanceof CellRenderer)) {
                table.getColumnModel().getColumn(i).setCellRenderer(new CellRenderer(align[i]));
            }
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xDCE3EE)));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    /** Kotak statistik kecil: ikon, judul, dan nilai. Nilai dapat diubah lewat label yang dikembalikan. */
    public static JLabel statTile(JPanel parent, String title, AppIcon.Type icon, Color accent) {
        RoundedPanel tile = new RoundedPanel(new BorderLayout(16, 0), 16, CARD, null, true, 14);
        tile.add(new JLabel(badge(icon, 52, accent)), BorderLayout.WEST);
        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        text.add(muted(title, 15));
        JLabel value = new JLabel("-");
        value.setFont(font(Font.BOLD, 22));
        value.setForeground(TEXT);
        text.add(value);
        tile.add(text, BorderLayout.CENTER);
        parent.add(tile);
        return value;
    }

    /** Ikon di dalam lingkaran berwarna muda. */
    public static Icon badge(AppIcon.Type type, int size, Color accent) {
        AppIcon inner = new AppIcon(type, (int) (size * 0.52), accent);
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 34));
                g2.fill(new Ellipse2D.Double(x, y, size, size));
                g2.dispose();
                int off = (size - inner.getIconWidth()) / 2;
                inner.paintIcon(c, g, x + off, y + off);
            }

            @Override
            public int getIconWidth() {
                return size;
            }

            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }

    // =====================================================================
    // Dialog & event
    // =====================================================================
    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Konfirmasi",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static void onChange(JTextComponent field, Runnable action) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
        });
    }

    // =====================================================================
    // Kelas bantu
    // =====================================================================

    /** Hanya mengizinkan input angka dengan panjang maksimum tertentu. */
    public static class DigitFilter extends DocumentFilter {

        private final int maxLength;
        private final boolean allowDot;

        public DigitFilter(int maxLength, boolean allowDot) {
            this.maxLength = maxLength;
            this.allowDot = allowDot;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                throws BadLocationException {
            replace(fb, offset, 0, text, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            String t = text == null ? "" : text;
            if (!t.chars().allMatch(ch -> Character.isDigit(ch) || (allowDot && ch == '.'))) {
                return;
            }
            if (fb.getDocument().getLength() - length + t.length() > maxLength) {
                return;
            }
            super.replace(fb, offset, length, t, attrs);
        }
    }

    public static class HeaderRenderer extends DefaultTableCellRenderer {

        public HeaderRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            setFont(font(Font.BOLD, 17));
            setForeground(TEXT);
            setBackground(HEADER_BG);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xC8D8F0)));
            return this;
        }
    }

    public static class CellRenderer extends DefaultTableCellRenderer {

        public CellRenderer(int alignment) {
            setHorizontalAlignment(alignment);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            setFont(table.getFont());
            setForeground(TEXT);
            if (isSelected) {
                setBackground(SELECTION);
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF7F9FC));
            }
            return this;
        }
    }

    /** Renderer status: Lunas (hijau), Belum Lunas (oranye), Terlambat (merah). */
    public static class StatusRenderer extends CellRenderer {

        public StatusRenderer() {
            super(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setFont(font(Font.BOLD, 16));
            String s = String.valueOf(value);
            setForeground(switch (s) {
                case "Lunas" -> GREEN;
                case "Terlambat" -> RED;
                default -> ORANGE;
            });
            return this;
        }
    }
}
