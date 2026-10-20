package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Halaman riwayat transaksi dan rekap penjualan per metode bayar.
 *
 * @author Kelompok 4
 */
public class HistoryPage extends JPanel implements Page {

    private final JLabel stTotal;
    private final JLabel stTunai;
    private final JLabel stQris;
    private final JLabel stPayLater;
    private final JLabel lblJumlah = Ui.muted("", 16);
    private final DefaultTableModel model;

    public HistoryPage() {
        super(new BorderLayout(0, 14));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 18, 14));

        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false);
        stTotal = Ui.statTile(stats, "Total Penjualan", AppIcon.Type.CHART, Ui.BLUE);
        stTunai = Ui.statTile(stats, "Tunai", AppIcon.Type.MONEY, Ui.GREEN);
        stQris = Ui.statTile(stats, "QRIS", AppIcon.Type.QR, Ui.TEAL);
        stPayLater = Ui.statTile(stats, "PayLater", AppIcon.Type.CARD, Ui.PURPLE);
        add(stats, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"No", "Waktu", "Produk", "Jumlah", "Total", "Metode",
            "Keterangan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);

        RoundedPanel card = Ui.card(new BorderLayout(0, 16), 20);
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        title.add(Ui.cardTitle("Riwayat Transaksi", AppIcon.Type.RECEIPT), BorderLayout.WEST);
        title.add(lblJumlah, BorderLayout.EAST);
        card.add(title, BorderLayout.NORTH);
        card.add(Ui.styleTable(table,
                new int[]{SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.LEFT, SwingConstants.CENTER,
                    SwingConstants.RIGHT, SwingConstants.CENTER, SwingConstants.LEFT},
                new int[]{60, 170, 200, 90, 130, 110, 330}), BorderLayout.CENTER);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        add(card, BorderLayout.CENTER);
    }

    @Override
    public void onShow() {
        model.setRowCount(0);
        List<AppData.Transaksi> list = AppData.transaksi();
        // Transaksi terbaru di atas
        for (int i = list.size() - 1; i >= 0; i--) {
            AppData.Transaksi t = list.get(i);
            model.addRow(new Object[]{i + 1, t.waktu.format(Ui.DATE_TIME), t.produk, t.jumlah,
                Ui.rp(t.total), t.metode, t.keterangan});
        }
        lblJumlah.setText(list.isEmpty() ? "Belum ada transaksi" : list.size() + " transaksi");
        stTotal.setText(Ui.rp(AppData.totalPenjualan(null, false)));
        stTunai.setText(Ui.rp(AppData.totalPenjualan(AppData.TUNAI, false)));
        stQris.setText(Ui.rp(AppData.totalPenjualan(AppData.QRIS, false)));
        stPayLater.setText(Ui.rp(AppData.totalPenjualan(AppData.PAYLATER, false)));
    }
}
