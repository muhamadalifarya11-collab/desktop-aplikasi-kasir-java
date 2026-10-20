package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Halaman kasir (POS) dengan metode bayar Tunai, QRIS, dan PayLater.
 *
 * @author Kelompok 4
 */
public class PosPage extends JPanel implements Page {

    private final JComboBox<String> cmbProduk = new JComboBox<>();
    private final JTextField txtHarga = Ui.textField("0", false, 0);
    private final JTextField txtJumlah = Ui.textField("0", true, 5);
    private final JTextField txtTotal = Ui.textField("0", false, 0);
    private final JTextField txtBayar = Ui.textField("0", true, 0);
    private final JTextField txtKembalian = Ui.textField("0", false, 0);
    private final PillToggle tglTunai = new PillToggle(AppData.TUNAI, AppIcon.Type.MONEY, Ui.GREEN);
    private final PillToggle tglQris = new PillToggle(AppData.QRIS, AppIcon.Type.QR, Ui.TEAL);
    private final PillToggle tglPayLater = new PillToggle(AppData.PAYLATER, AppIcon.Type.CARD, Ui.PURPLE);
    private final JLabel lblStatus = new JLabel();
    private final DefaultTableModel model;
    private final JTable tblProduk;
    private RoundedPanel boxBayar;
    private ModernButton btnHitung;
    private ModernButton btnBayar;
    private ModernButton btnReset;

    private long totalHarga;
    private boolean lunas;
    private boolean syncing;

    public PosPage() {
        super(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 18, 14));

        model = new DefaultTableModel(new Object[]{"No", "Nama Produk", "Harga"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProduk = new JTable(model);
        Ui.moneyOnly(txtBayar, 16);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        JComponent form = createFormCard();
        JComponent list = createProductCard();
        form.setPreferredSize(new Dimension(600, 540));
        list.setPreferredSize(new Dimension(400, 540));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 0.6;
        c.insets = new Insets(0, 0, 0, 8);
        content.add(form, c);
        c.gridx = 1;
        c.weightx = 0.4;
        c.insets = new Insets(0, 8, 0, 0);
        content.add(list, c);

        add(content, BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);
        initEvents();
        setMetode(AppData.TUNAI);
        updateStatus();
    }

    // =====================================================================
    // Tampilan
    // =====================================================================
    private JComponent createFormCard() {
        RoundedPanel card = Ui.card(new GridBagLayout(), 22);
        Ui.styleCombo(cmbProduk);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.add(Ui.cardTitle("Data Pembelian", AppIcon.Type.CART), BorderLayout.WEST);
        lblStatus.setFont(Ui.font(Font.BOLD, 16));
        titleRow.add(lblStatus, BorderLayout.EAST);
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 4, 10, 4);
        card.add(titleRow, c);

        JPanel metode = new JPanel(new GridLayout(1, 3, 10, 0));
        metode.setOpaque(false);
        metode.setPreferredSize(new Dimension(280, 50));
        ButtonGroup group = new ButtonGroup();
        for (PillToggle t : new PillToggle[]{tglTunai, tglQris, tglPayLater}) {
            group.add(t);
            metode.add(t);
        }
        boxBayar = Ui.wrap(txtBayar, false, false);

        int w = 235;
        Ui.addRow(card, 1, Ui.formLabel("Produk", AppIcon.Type.TAG), Ui.wrap(cmbProduk, false, false), w);
        Ui.addRow(card, 2, Ui.formLabel("Harga Produk", AppIcon.Type.MONEY), Ui.wrap(txtHarga, true, true), w);
        Ui.addRow(card, 3, Ui.formLabel("Jumlah Pembelian", AppIcon.Type.COUNTER), Ui.wrap(txtJumlah, false, false), w);
        Ui.addRow(card, 4, Ui.formLabel("Total Harga", AppIcon.Type.RECEIPT), Ui.wrap(txtTotal, true, true), w);
        Ui.addRow(card, 5, Ui.formLabel("Metode Bayar", AppIcon.Type.CARD), metode, w);
        Ui.addRow(card, 6, Ui.formLabel("Uang Pembayaran", AppIcon.Type.WALLET), boxBayar, w);
        Ui.addRow(card, 7, Ui.formLabel("Uang Kembalian", AppIcon.Type.UNDO), Ui.wrap(txtKembalian, true, true), w);
        return card;
    }

    private JComponent createProductCard() {
        RoundedPanel card = Ui.card(new BorderLayout(0, 16), 20);
        card.add(Ui.cardTitle("Daftar Produk", AppIcon.Type.BOX), BorderLayout.NORTH);
        card.add(Ui.styleTable(tblProduk,
                new int[]{SwingConstants.CENTER, SwingConstants.LEFT, SwingConstants.RIGHT},
                new int[]{60, 200, 140}), BorderLayout.CENTER);
        tblProduk.getColumnModel().getColumn(0).setMaxWidth(70);
        tblProduk.getColumnModel().getColumn(2).setMinWidth(120);
        return card;
    }

    private JComponent createButtons() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 16, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 96));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        btnHitung = new ModernButton("Hitung Total", AppIcon.Type.CALCULATOR, Ui.BLUE);
        btnBayar = new ModernButton("Bayar Tunai", AppIcon.Type.MONEY, Ui.GREEN);
        btnReset = new ModernButton("Bersihkan Form", AppIcon.Type.REFRESH, Ui.RED);
        panel.add(btnHitung);
        panel.add(btnBayar);
        panel.add(btnReset);
        return panel;
    }

    // =====================================================================
    // Event & logika kasir
    // =====================================================================
    private void initEvents() {
        cmbProduk.addItemListener(e -> {
            if (!syncing && e.getStateChange() == ItemEvent.SELECTED) {
                produkDipilih();
            }
        });
        tblProduk.getSelectionModel().addListSelectionListener(e -> {
            int row = tblProduk.getSelectedRow();
            if (!e.getValueIsAdjusting() && !syncing && row >= 0) {
                cmbProduk.setSelectedIndex(row + 1);
            }
        });
        Ui.onChange(txtJumlah, () -> {
            if (!syncing) {
                resetTotal();
            }
        });
        Ui.onChange(txtBayar, () -> {
            if (!syncing && metode().equals(AppData.TUNAI)) {
                txtKembalian.setText("0");
            }
        });

        tglTunai.addActionListener(e -> setMetode(AppData.TUNAI));
        tglQris.addActionListener(e -> setMetode(AppData.QRIS));
        tglPayLater.addActionListener(e -> setMetode(AppData.PAYLATER));

        btnHitung.addActionListener(e -> hitungTotal());
        btnBayar.addActionListener(e -> bayar());
        btnReset.addActionListener(e -> bersihkanForm());
        txtJumlah.addActionListener(e -> hitungTotal());
        txtBayar.addActionListener(e -> bayar());
    }

    /** Memuat ulang daftar produk (bisa berubah dari halaman Kelola Produk). */
    @Override
    public void onShow() {
        if (lunas) {
            return;
        }
        String sebelumnya = cmbProduk.getSelectedIndex() > 0 ? (String) cmbProduk.getSelectedItem() : null;
        syncing = true;
        cmbProduk.removeAllItems();
        cmbProduk.addItem("Pilih Produk");
        model.setRowCount(0);
        int i = 1;
        for (AppData.Produk p : AppData.produk()) {
            cmbProduk.addItem(p.nama);
            model.addRow(new Object[]{i++, p.nama, Ui.rp(p.harga)});
        }
        AppData.Produk p = sebelumnya == null ? null : AppData.cariProduk(sebelumnya);
        cmbProduk.setSelectedIndex(p == null ? 0 : AppData.produk().indexOf(p) + 1);
        syncing = false;
        produkDipilih();
    }

    private AppData.Produk produkTerpilih() {
        int idx = cmbProduk.getSelectedIndex();
        return idx > 0 ? AppData.produk().get(idx - 1) : null;
    }

    private String metode() {
        if (tglQris.isSelected()) {
            return AppData.QRIS;
        }
        return tglPayLater.isSelected() ? AppData.PAYLATER : AppData.TUNAI;
    }

    private void setMetode(String metode) {
        boolean tunai = metode.equals(AppData.TUNAI);
        tglTunai.setSelected(tunai);
        tglQris.setSelected(metode.equals(AppData.QRIS));
        tglPayLater.setSelected(metode.equals(AppData.PAYLATER));
        tglTunai.repaint();
        tglQris.repaint();
        tglPayLater.repaint();

        syncing = true;
        txtBayar.setEditable(tunai);
        txtBayar.setFocusable(tunai);
        boxBayar.setFill(tunai ? java.awt.Color.WHITE : Ui.FIELD_READONLY);
        txtBayar.setText(tunai ? "0" : Ui.num(totalHarga));
        txtKembalian.setText("0");
        syncing = false;

        switch (metode) {
            case AppData.QRIS -> btnBayar.setContent("Bayar QRIS", AppIcon.Type.QR, Ui.TEAL);
            case AppData.PAYLATER -> btnBayar.setContent("Bayar PayLater", AppIcon.Type.CARD, Ui.PURPLE);
            default -> btnBayar.setContent("Bayar Tunai", AppIcon.Type.MONEY, Ui.GREEN);
        }
    }

    private void produkDipilih() {
        AppData.Produk p = produkTerpilih();
        syncing = true;
        if (p != null) {
            txtHarga.setText(Ui.num(p.harga));
            int row = AppData.produk().indexOf(p);
            tblProduk.setRowSelectionInterval(row, row);
            if (Ui.parse(txtJumlah.getText()) == 0) {
                txtJumlah.setText("1");
            }
        } else {
            txtHarga.setText("0");
            tblProduk.clearSelection();
        }
        syncing = false;
        resetTotal();
    }

    private boolean hitungTotal() {
        AppData.Produk p = produkTerpilih();
        if (p == null) {
            Ui.warn(this, "Silakan pilih produk terlebih dahulu.");
            cmbProduk.requestFocusInWindow();
            return false;
        }
        long jumlah = Ui.parse(txtJumlah.getText());
        if (jumlah <= 0) {
            Ui.warn(this, "Jumlah pembelian harus lebih dari 0.");
            txtJumlah.requestFocusInWindow();
            return false;
        }
        totalHarga = p.harga * jumlah;
        txtTotal.setText(Ui.num(totalHarga));
        syncing = true;
        txtKembalian.setText("0");
        if (!metode().equals(AppData.TUNAI)) {
            txtBayar.setText(Ui.num(totalHarga));
        }
        syncing = false;
        if (metode().equals(AppData.TUNAI)) {
            txtBayar.requestFocusInWindow();
        }
        return true;
    }

    private void bayar() {
        if (lunas) {
            Ui.info(this, "Transaksi ini sudah dibayar.\nKlik \"Bersihkan Form\" untuk transaksi baru.");
            return;
        }
        if (totalHarga <= 0 && !hitungTotal()) {
            return;
        }
        AppData.Produk p = produkTerpilih();
        long jumlah = Ui.parse(txtJumlah.getText());
        String item = p.nama + " x" + jumlah;

        switch (metode()) {
            case AppData.QRIS -> {
                if (QrisDialog.bayar(this, totalHarga, item)) {
                    AppData.catatTransaksi(p.nama, jumlah, totalHarga, AppData.QRIS, "Lunas via QRIS");
                    selesai("LUNAS - QRIS");
                }
            }
            case AppData.PAYLATER -> {
                AppData.Tagihan t = PayLaterDialog.proses(this, totalHarga, item);
                if (t != null) {
                    AppData.catatTransaksi(p.nama, jumlah, totalHarga, AppData.PAYLATER,
                            t.kode + " - " + t.pelanggan);
                    selesai("PAYLATER - " + t.pelanggan);
                    Ui.info(this, "PayLater disetujui untuk " + t.pelanggan + ".\nKode tagihan: " + t.kode
                            + "\nJatuh tempo: " + t.jatuhTempo.format(Ui.DATE));
                }
            }
            default -> {
                long uang = Ui.parse(txtBayar.getText());
                if (uang < totalHarga) {
                    Ui.warn(this, "Uang pembayaran kurang " + Ui.rp(totalHarga - uang) + ".");
                    txtBayar.requestFocusInWindow();
                    return;
                }
                long kembali = uang - totalHarga;
                txtKembalian.setText(Ui.num(kembali));
                AppData.catatTransaksi(p.nama, jumlah, totalHarga, AppData.TUNAI,
                        "Bayar " + Ui.rp(uang) + ", kembali " + Ui.rp(kembali));
                selesai("LUNAS - TUNAI");
                Ui.info(this, "Pembayaran tunai berhasil.\nKembalian: " + Ui.rp(kembali));
            }
        }
    }

    /** Mengunci form setelah transaksi dibayar sampai form dibersihkan. */
    private void selesai(String status) {
        lunas = true;
        setFormEnabled(false);
        lblStatus.setText(status);
        lblStatus.setForeground(Ui.GREEN);
    }

    private void bersihkanForm() {
        lunas = false;
        setFormEnabled(true);
        syncing = true;
        txtJumlah.setText("0");
        cmbProduk.setSelectedIndex(0);
        syncing = false;
        onShow();
        setMetode(AppData.TUNAI);
        updateStatus();
        cmbProduk.requestFocusInWindow();
    }

    private void setFormEnabled(boolean enabled) {
        cmbProduk.setEnabled(enabled);
        txtJumlah.setEditable(enabled);
        txtJumlah.setFocusable(enabled);
        tblProduk.setEnabled(enabled);
        tglTunai.setEnabled(enabled);
        tglQris.setEnabled(enabled);
        tglPayLater.setEnabled(enabled);
        btnHitung.setEnabled(enabled);
        btnBayar.setEnabled(enabled);
        if (!enabled) {
            txtBayar.setEditable(false);
            txtBayar.setFocusable(false);
        }
    }

    private void resetTotal() {
        totalHarga = 0;
        txtTotal.setText("0");
        txtKembalian.setText("0");
        if (!metode().equals(AppData.TUNAI)) {
            boolean s = syncing;
            syncing = true;
            txtBayar.setText("0");
            syncing = s;
        }
    }

    private void updateStatus() {
        lblStatus.setText("Belum dibayar");
        lblStatus.setForeground(Ui.MUTED);
    }
}
