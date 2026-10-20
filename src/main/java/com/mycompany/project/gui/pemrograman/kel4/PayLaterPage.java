package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Halaman pengelolaan tagihan PayLater: lihat tagihan dan terima pelunasan.
 *
 * @author Kelompok 4
 */
public class PayLaterPage extends JPanel implements Page {

    private final JLabel stAktif;
    private final JLabel stPelanggan;
    private final JLabel stTerlambat;
    private final JLabel stLunas;
    private final JComboBox<String> cmbFilter = new JComboBox<>(
            new String[]{"Semua Tagihan", "Belum Lunas", "Terlambat", "Lunas"});
    private final DefaultTableModel model;
    private final JTable table;
    private final List<AppData.Tagihan> baris = new ArrayList<>();

    public PayLaterPage() {
        super(new BorderLayout(0, 14));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 18, 14));

        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false);
        stAktif = Ui.statTile(stats, "Total Tagihan Aktif", AppIcon.Type.CARD, Ui.PURPLE);
        stPelanggan = Ui.statTile(stats, "Pelanggan Berutang", AppIcon.Type.USER, Ui.BLUE);
        stTerlambat = Ui.statTile(stats, "Tagihan Terlambat", AppIcon.Type.WARNING, Ui.RED);
        stLunas = Ui.statTile(stats, "Tagihan Lunas", AppIcon.Type.CHECK, Ui.GREEN);
        add(stats, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Kode", "Pelanggan", "No. HP", "Tanggal",
            "Jatuh Tempo", "Keterangan", "Jumlah", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.getColumnModel().getColumn(7).setCellRenderer(new Ui.StatusRenderer());

        RoundedPanel card = Ui.card(new BorderLayout(0, 12), 20);
        JPanel title = new JPanel(new BorderLayout(16, 0));
        title.setOpaque(false);
        title.add(Ui.cardTitle("Tagihan PayLater", AppIcon.Type.CARD), BorderLayout.CENTER);
        Ui.styleCombo(cmbFilter);
        RoundedPanel filterBox = Ui.wrap(cmbFilter, false, false);
        filterBox.setPreferredSize(new Dimension(230, 48));
        JPanel filterWrap = new JPanel(new java.awt.GridBagLayout());
        filterWrap.setOpaque(false);
        filterWrap.add(filterBox);
        title.add(filterWrap, BorderLayout.EAST);
        card.add(title, BorderLayout.NORTH);
        card.add(Ui.styleTable(table,
                new int[]{SwingConstants.CENTER, SwingConstants.LEFT, SwingConstants.LEFT, SwingConstants.CENTER,
                    SwingConstants.CENTER, SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.CENTER},
                new int[]{90, 170, 150, 120, 120, 250, 110, 120}), BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 16, 0));
        buttons.setOpaque(false);
        buttons.setPreferredSize(new Dimension(0, 84));
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
        ModernButton btnTunai = new ModernButton("Lunasi Tunai", AppIcon.Type.MONEY, Ui.GREEN);
        ModernButton btnQris = new ModernButton("Lunasi via QRIS", AppIcon.Type.QR, Ui.TEAL);
        ModernButton btnHapus = new ModernButton("Hapus Tagihan", AppIcon.Type.TRASH, Ui.RED);
        buttons.add(btnTunai);
        buttons.add(btnQris);
        buttons.add(btnHapus);
        add(buttons, BorderLayout.SOUTH);

        cmbFilter.addActionListener(e -> muat());
        btnTunai.addActionListener(e -> lunasi(AppData.TUNAI));
        btnQris.addActionListener(e -> lunasi(AppData.QRIS));
        btnHapus.addActionListener(e -> hapus());
    }

    @Override
    public void onShow() {
        muat();
    }

    private void muat() {
        String filter = (String) cmbFilter.getSelectedItem();
        model.setRowCount(0);
        baris.clear();
        // Tagihan terbaru di atas
        List<AppData.Tagihan> semua = new ArrayList<>(AppData.tagihan());
        java.util.Collections.reverse(semua);
        for (AppData.Tagihan t : semua) {
            boolean tampil = switch (filter) {
                case "Belum Lunas" -> !t.lunas;
                case "Terlambat" -> t.terlambat();
                case "Lunas" -> t.lunas;
                default -> true;
            };
            if (!tampil) {
                continue;
            }
            baris.add(t);
            model.addRow(new Object[]{t.kode, t.pelanggan, t.hp, t.tanggal.format(Ui.DATE),
                t.jatuhTempo.format(Ui.DATE), t.keterangan, Ui.rp(t.jumlah),
                t.lunas ? "Lunas" : t.status()});
        }
        stAktif.setText(Ui.rp(AppData.tagihanAktif(null)));
        stPelanggan.setText(AppData.jumlahPelangganAktif() + " orang");
        stTerlambat.setText(AppData.jumlahTerlambat() + " tagihan");
        stLunas.setText(AppData.tagihan().stream().filter(t -> t.lunas).count() + " tagihan");
    }

    private AppData.Tagihan terpilih() {
        int row = table.getSelectedRow();
        if (row < 0) {
            Ui.warn(this, "Pilih tagihan pada tabel terlebih dahulu.");
            return null;
        }
        return baris.get(row);
    }

    private void lunasi(String metode) {
        AppData.Tagihan t = terpilih();
        if (t == null) {
            return;
        }
        if (t.lunas) {
            Ui.info(this, "Tagihan " + t.kode + " sudah lunas (" + t.metodeLunas + ").");
            return;
        }
        boolean berhasil;
        if (metode.equals(AppData.QRIS)) {
            berhasil = QrisDialog.bayar(this, t.jumlah, "Pelunasan " + t.kode + " - " + t.pelanggan);
        } else {
            berhasil = Ui.confirm(this, "Terima pembayaran tunai " + Ui.rp(t.jumlah) + " dari "
                    + t.pelanggan + " untuk " + t.kode + "?");
        }
        if (berhasil) {
            t.lunas = true;
            t.metodeLunas = metode;
            muat();
            Ui.info(this, "Tagihan " + t.kode + " atas nama " + t.pelanggan + " LUNAS via " + metode + ".");
        }
    }

    private void hapus() {
        AppData.Tagihan t = terpilih();
        if (t != null && Ui.confirm(this, "Hapus tagihan " + t.kode + " (" + t.pelanggan + ")?")) {
            AppData.tagihan().remove(t);
            muat();
        }
    }
}
