package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Halaman utama: ringkasan hari ini dan kartu menu.
 *
 * @author Kelompok 4
 */
public class HomePage extends JPanel implements Page {

    private final JLabel stProduk;
    private final JLabel stTransaksi;
    private final JLabel stPenjualan;
    private final JLabel stPayLater;

    public HomePage(Consumer<String> navigate) {
        super(new BorderLayout(0, 18));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 22, 20));

        JPanel top = new JPanel(new BorderLayout(0, 16));
        top.setOpaque(false);
        JPanel welcome = new JPanel(new GridLayout(2, 1, 0, 2));
        welcome.setOpaque(false);
        welcome.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        JLabel title = new JLabel("Selamat datang di Kasir Warkop Kelompok 4");
        title.setFont(Ui.font(Font.BOLD, 26));
        title.setForeground(Ui.TEXT);
        welcome.add(title);
        welcome.add(Ui.muted("Pilih menu di bawah untuk mulai bekerja.", 17));
        top.add(welcome, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(1, 4, 16, 0));
        stats.setOpaque(false);
        stProduk = Ui.statTile(stats, "Jumlah Produk", AppIcon.Type.BOX, Ui.ORANGE);
        stTransaksi = Ui.statTile(stats, "Transaksi Hari Ini", AppIcon.Type.RECEIPT, Ui.BLUE);
        stPenjualan = Ui.statTile(stats, "Penjualan Hari Ini", AppIcon.Type.CHART, Ui.GREEN);
        stPayLater = Ui.statTile(stats, "Tagihan PayLater Aktif", AppIcon.Type.CARD, Ui.PURPLE);
        top.add(stats, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(2, 2, 18, 18));
        menu.setOpaque(false);
        menu.add(new MenuCard("Kasir (POS)",
                "Catat penjualan dan terima pembayaran Tunai, QRIS, atau PayLater.",
                AppIcon.Type.CALCULATOR, Ui.BLUE, () -> navigate.accept(Main.POS)));
        menu.add(new MenuCard("Kelola Produk",
                "Tambah, ubah, dan hapus menu warkop beserta harganya.",
                AppIcon.Type.BOX, Ui.ORANGE, () -> navigate.accept(Main.PRODUK)));
        menu.add(new MenuCard("Kelola PayLater",
                "Pantau tagihan pelanggan dan terima pelunasan Tunai atau QRIS.",
                AppIcon.Type.CARD, Ui.PURPLE, () -> navigate.accept(Main.PAYLATER)));
        menu.add(new MenuCard("Riwayat Transaksi",
                "Lihat semua transaksi dan rekap penjualan per metode bayar.",
                AppIcon.Type.CHART, Ui.GREEN, () -> navigate.accept(Main.RIWAYAT)));
        add(menu, BorderLayout.CENTER);
    }

    @Override
    public void onShow() {
        stProduk.setText(AppData.produk().size() + " menu");
        stTransaksi.setText(AppData.jumlahTransaksiHariIni() + " transaksi");
        stPenjualan.setText(Ui.rp(AppData.totalPenjualan(null, true)));
        stPayLater.setText(Ui.rp(AppData.tagihanAktif(null)));
    }
}
