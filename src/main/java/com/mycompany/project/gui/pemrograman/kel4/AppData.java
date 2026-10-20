package com.mycompany.project.gui.pemrograman.kel4;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Penyimpanan data aplikasi (di memori). Data akan kembali ke data awal saat
 * aplikasi ditutup dan dijalankan lagi.
 *
 * @author Kelompok 4
 */
public final class AppData {

    /** Limit PayLater untuk setiap pelanggan (berdasarkan No. HP). */
    public static final long LIMIT_PAYLATER = 500_000;

    public static final String TUNAI = "Tunai";
    public static final String QRIS = "QRIS";
    public static final String PAYLATER = "PayLater";

    public static class Produk {

        public String nama;
        public long harga;

        public Produk(String nama, long harga) {
            this.nama = nama;
            this.harga = harga;
        }
    }

    public static class Transaksi {

        public final LocalDateTime waktu;
        public final String produk;
        public final long jumlah;
        public final long total;
        public final String metode;
        public final String keterangan;

        Transaksi(LocalDateTime waktu, String produk, long jumlah, long total, String metode, String keterangan) {
            this.waktu = waktu;
            this.produk = produk;
            this.jumlah = jumlah;
            this.total = total;
            this.metode = metode;
            this.keterangan = keterangan;
        }
    }

    public static class Tagihan {

        public final String kode;
        public final String pelanggan;
        public final String hp;
        public final LocalDateTime tanggal;
        public final LocalDate jatuhTempo;
        public final long jumlah;
        public final String keterangan;
        public boolean lunas;
        public String metodeLunas;

        Tagihan(String kode, String pelanggan, String hp, LocalDateTime tanggal, LocalDate jatuhTempo,
                long jumlah, String keterangan) {
            this.kode = kode;
            this.pelanggan = pelanggan;
            this.hp = hp;
            this.tanggal = tanggal;
            this.jatuhTempo = jatuhTempo;
            this.jumlah = jumlah;
            this.keterangan = keterangan;
        }

        public boolean terlambat() {
            return !lunas && LocalDate.now().isAfter(jatuhTempo);
        }

        public String status() {
            if (lunas) {
                return "Lunas";
            }
            return terlambat() ? "Terlambat" : "Belum Lunas";
        }
    }

    private static final List<Produk> PRODUK = new ArrayList<>();
    private static final List<Transaksi> TRANSAKSI = new ArrayList<>();
    private static final List<Tagihan> TAGIHAN = new ArrayList<>();
    private static int nomorTagihan = 1;

    static {
        PRODUK.add(new Produk("Kopi Hitam", 5000));
        PRODUK.add(new Produk("Kopi Susu", 8000));
        PRODUK.add(new Produk("Es Teh Manis", 5000));
        PRODUK.add(new Produk("Indomie Goreng", 12000));
        PRODUK.add(new Produk("Indomie Rebus", 12000));
        PRODUK.add(new Produk("Roti Bakar", 10000));
        PRODUK.add(new Produk("Pisang Goreng", 8000));

        // Contoh data PayLater
        LocalDateTime now = LocalDateTime.now();
        tambahTagihan("Budi Santoso", "081234567890", now.minusDays(3), 30, 45_000, "Kopi Susu x3, Roti Bakar x2");
        tambahTagihan("Siti Aminah", "085711223344", now.minusDays(20), 14, 24_000, "Indomie Goreng x2");
        Tagihan lunas = tambahTagihan("Andi Pratama", "089977665544", now.minusDays(10), 14, 16_000, "Kopi Susu x2");
        lunas.lunas = true;
        lunas.metodeLunas = QRIS;
    }

    private AppData() {
    }

    // ===== Produk =====
    public static List<Produk> produk() {
        return PRODUK;
    }

    public static Produk cariProduk(String nama) {
        for (Produk p : PRODUK) {
            if (p.nama.equalsIgnoreCase(nama.trim())) {
                return p;
            }
        }
        return null;
    }

    // ===== Transaksi =====
    public static List<Transaksi> transaksi() {
        return TRANSAKSI;
    }

    public static void catatTransaksi(String produk, long jumlah, long total, String metode, String keterangan) {
        TRANSAKSI.add(new Transaksi(LocalDateTime.now(), produk, jumlah, total, metode, keterangan));
    }

    public static long totalPenjualan(String metode, boolean hariIniSaja) {
        long sum = 0;
        for (Transaksi t : TRANSAKSI) {
            boolean metodeCocok = metode == null || t.metode.equals(metode);
            boolean tanggalCocok = !hariIniSaja || t.waktu.toLocalDate().equals(LocalDate.now());
            if (metodeCocok && tanggalCocok) {
                sum += t.total;
            }
        }
        return sum;
    }

    public static int jumlahTransaksiHariIni() {
        int n = 0;
        for (Transaksi t : TRANSAKSI) {
            if (t.waktu.toLocalDate().equals(LocalDate.now())) {
                n++;
            }
        }
        return n;
    }

    // ===== PayLater =====
    public static List<Tagihan> tagihan() {
        return TAGIHAN;
    }

    public static Tagihan tambahTagihan(String pelanggan, String hp, LocalDateTime tanggal, int tenorHari,
            long jumlah, String keterangan) {
        String kode = String.format("PL-%04d", nomorTagihan++);
        Tagihan t = new Tagihan(kode, pelanggan, hp, tanggal, tanggal.toLocalDate().plusDays(tenorHari),
                jumlah, keterangan);
        TAGIHAN.add(t);
        return t;
    }

    /** Total tagihan yang belum lunas, untuk satu pelanggan (hp != null) atau semua. */
    public static long tagihanAktif(String hp) {
        long sum = 0;
        for (Tagihan t : TAGIHAN) {
            if (!t.lunas && (hp == null || t.hp.equals(hp))) {
                sum += t.jumlah;
            }
        }
        return sum;
    }

    public static long sisaLimit(String hp) {
        return Math.max(0, LIMIT_PAYLATER - tagihanAktif(hp));
    }

    /** Nama pelanggan yang pernah memakai PayLater dengan No. HP ini, atau null. */
    public static String namaPelanggan(String hp) {
        for (Tagihan t : TAGIHAN) {
            if (t.hp.equals(hp)) {
                return t.pelanggan;
            }
        }
        return null;
    }

    public static long jumlahPelangganAktif() {
        return TAGIHAN.stream().filter(t -> !t.lunas).map(t -> t.hp).distinct().count();
    }

    public static long jumlahTerlambat() {
        return TAGIHAN.stream().filter(Tagihan::terlambat).count();
    }
}
