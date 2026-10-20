# Kasir Warkop Kelompok 4

Aplikasi kasir desktop untuk warkop, dibuat dengan **Java Swing** dan **Maven**.

Fitur:

- **Kasir (POS)** — pilih produk, hitung total, bayar dengan **Tunai**, **QRIS**, atau **PayLater**
- **Kelola Produk** — tambah, ubah, hapus produk
- **Kelola PayLater** — daftar tagihan pelanggan, pelunasan via Tunai atau QRIS
- **Riwayat Transaksi** — semua transaksi dan rekap per metode bayar

> Data disimpan di memori. Saat aplikasi ditutup, data kembali ke data awal.

---

## Yang perlu disiapkan

1. **JDK 26**
   Project ini di-set untuk Java 26 (`maven.compiler.release` di `pom.xml`).
   Cek versi di terminal:

   ```bash
   java -version
   ```

   Kalau JDK kamu lebih lama, lihat bagian [Pakai JDK versi lain](#pakai-jdk-versi-lain).

2. **Visual Studio Code**

3. *(Opsional)* extension Java atau Code Runner, lihat cara 2 dan 3 di bawah.

---

## Cara menjalankan di VS Code

Buka VS Code, pilih **File → Open Folder…**, lalu pilih folder `project-gui-pemrograman-kel4`
(folder yang berisi `pom.xml`). Setelah itu pilih salah satu cara di bawah.

### Cara 1 — `Ctrl+Shift+B` (paling mudah, tanpa extension tambahan)

Tekan **`Ctrl+Shift+B`**. VS Code menjalankan task **Jalankan Kasir Warkop** yang
meng-compile semua file Java lalu membuka aplikasinya.

Cara ini hanya butuh JDK. Perintahnya ada di `.vscode/run-kasir.ps1`.

### Cara 2 — tombol ▷ Run Code (extension Code Runner)

Jika extension **Code Runner** terpasang, buka file `.java` mana saja di project ini lalu klik **▷**
(atau `Ctrl+Alt+N`). Project ini sudah diatur (`.vscode/settings.json`) agar Code Runner
meng-compile **semua** file, bukan hanya file yang sedang dibuka.

> Tanpa pengaturan itu, Code Runner hanya meng-compile satu file dan muncul error
> `cannot find symbol ... Main`, karena aplikasi ini terdiri dari banyak file.

### Cara 3 — extension Java (Run / F5)

Dengan extension Java (Microsoft *Extension Pack for Java* atau Oracle *Java*):

1. Tunggu sampai project selesai dimuat (status **Java: Ready** di pojok kiri bawah).
2. Buka `src/main/java/com/mycompany/project/gui/pemrograman/kel4/ProjectGuiPemrogramanKel4.java`.
3. Klik **Run** di atas method `main`, atau tekan **F5**.

Dengan cara mana pun, jendela **Kasir Warkop Kelompok 4** akan terbuka di Menu Utama.

### Alternatif: lewat terminal (butuh Maven terpasang)

Jika Maven (`mvn`) sudah ter-install, dari folder project jalankan:

```bash
mvn compile exec:java
```

---

## Cara menjalankan di NetBeans

Buka NetBeans → **File → Open Project…** → pilih folder project ini → klik **Run Project** (`F6`).

---

## Pakai JDK versi lain

Kode ini butuh minimal **Java 21**. Jika JDK kamu bukan 26, ubah baris berikut di `pom.xml`
sesuai versi JDK kamu, misalnya untuk Java 21:

```xml
<maven.compiler.release>21</maven.compiler.release>
```

Setelah itu di VS Code tekan `Ctrl+Shift+P` → **Java: Clean Java Language Server Workspace**
→ **Restart and delete**, lalu jalankan lagi.

---

## Masalah yang sering muncul

| Masalah | Solusi |
|---|---|
| Error `cannot find symbol ... Main` saat Run Code | Buka folder project (yang berisi `pom.xml`) di VS Code, bukan hanya file-nya, supaya `.vscode/settings.json` terbaca. Atau pakai `Ctrl+Shift+B`. |
| Tombol **Run** tidak muncul | Pastikan Extension Pack for Java sudah terpasang dan status **Java: Ready**. Buka folder yang berisi `pom.xml`, bukan folder induknya. |
| Error `release version 26 not supported` | JDK yang dipakai lebih lama dari 26. Install JDK 26 atau ubah versi di `pom.xml` (lihat di atas). |
| VS Code memakai JDK yang salah | `Ctrl+Shift+P` → **Java: Configure Java Runtime**, pilih JDK yang benar. |
| Banyak garis merah setelah ganti JDK | `Ctrl+Shift+P` → **Java: Clean Java Language Server Workspace**. |

---

## Struktur file

```
src/main/java/com/mycompany/project/gui/pemrograman/kel4/
├── ProjectGuiPemrogramanKel4.java   titik masuk aplikasi
├── Main.java                         jendela utama, header, navigasi antar halaman
├── HomePage.java                     menu utama (kartu menu + ringkasan)
├── PosPage.java                      halaman kasir
├── ProductPage.java                  halaman kelola produk
├── PayLaterPage.java                 halaman kelola tagihan PayLater
├── HistoryPage.java                  halaman riwayat transaksi
├── QrisDialog.java                   dialog pembayaran QRIS
├── PayLaterDialog.java               dialog pembayaran PayLater
├── AppData.java                      data produk, transaksi, dan tagihan
├── Ui.java                           warna, font, dan komponen yang dipakai bersama
├── AppIcon.java                      ikon yang digambar dengan kode
├── RoundedPanel.java                 panel bersudut membulat
├── ModernButton.java                 tombol berwarna dengan ikon
├── PillToggle.java                   tombol pilihan metode bayar
├── MenuCard.java                     kartu menu di halaman utama
└── Page.java                         interface untuk setiap halaman
```
