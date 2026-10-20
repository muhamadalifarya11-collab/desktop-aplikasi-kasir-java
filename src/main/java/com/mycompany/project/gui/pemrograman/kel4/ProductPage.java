package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 * Halaman untuk menambah, mengubah, dan menghapus produk.
 *
 * @author Kelompok 4
 */
public class ProductPage extends JPanel implements Page {

    private final JTextField txtNama = Ui.textField("", true, 0);
    private final JTextField txtHarga = Ui.textField("", true, 9);
    private final JLabel lblJumlah = Ui.muted("", 16);
    private final DefaultTableModel model;
    private final JTable table;
    private AppData.Produk dipilih;
    private boolean syncing;

    public ProductPage() {
        super(new GridBagLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(14, 14, 18, 14));

        model = new DefaultTableModel(new Object[]{"No", "Nama Produk", "Harga"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);

        JComponent form = createFormCard();
        JComponent list = createListCard();
        form.setPreferredSize(new Dimension(460, 600));
        list.setPreferredSize(new Dimension(600, 600));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.weightx = 0.43;
        c.insets = new Insets(0, 0, 0, 8);
        add(form, c);
        c.gridx = 1;
        c.weightx = 0.57;
        c.insets = new Insets(0, 8, 0, 0);
        add(list, c);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !syncing) {
                pilihBaris();
            }
        });
    }

    private JComponent createFormCard() {
        RoundedPanel card = Ui.card(new GridBagLayout(), 22);
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 4, 16, 0);
        card.add(Ui.cardTitle("Form Produk", AppIcon.Type.TAG), c);

        JPanel rows = new JPanel(new GridBagLayout());
        rows.setOpaque(false);
        Ui.addRow(rows, 0, Ui.formLabel("Nama Produk", AppIcon.Type.TAG), Ui.wrap(txtNama, false, false), 170);
        Ui.addRow(rows, 1, Ui.formLabel("Harga", AppIcon.Type.MONEY), Ui.wrap(txtHarga, false, true), 170);
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 18, 0);
        card.add(rows, c);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 12, 12));
        buttons.setOpaque(false);
        ModernButton btnTambah = new ModernButton("Tambah", AppIcon.Type.PLUS, Ui.BLUE);
        ModernButton btnUbah = new ModernButton("Simpan Ubah", AppIcon.Type.CHECK, Ui.GREEN);
        ModernButton btnHapus = new ModernButton("Hapus", AppIcon.Type.TRASH, Ui.RED);
        ModernButton btnBersih = new ModernButton("Bersihkan", AppIcon.Type.REFRESH, Ui.GRAY);
        for (ModernButton b : new ModernButton[]{btnTambah, btnUbah, btnHapus, btnBersih}) {
            b.setFont(Ui.font(Font.BOLD, 18));
            b.setPreferredSize(new Dimension(160, 60));
            buttons.add(b);
        }
        btnTambah.addActionListener(e -> tambah());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> bersihkan());
        txtHarga.addActionListener(e -> {
            if (dipilih == null) {
                tambah();
            } else {
                ubah();
            }
        });
        c.gridy = 3;
        c.insets = new Insets(0, 4, 0, 4);
        card.add(buttons, c);

        // Pengisi agar isi form tetap di atas
        c.gridy = 4;
        c.weighty = 1;
        card.add(new JLabel(), c);
        return card;
    }

    private JComponent createListCard() {
        RoundedPanel card = Ui.card(new BorderLayout(0, 16), 20);
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        title.add(Ui.cardTitle("Daftar Produk", AppIcon.Type.BOX), BorderLayout.WEST);
        title.add(lblJumlah, BorderLayout.EAST);
        card.add(title, BorderLayout.NORTH);
        card.add(Ui.styleTable(table,
                new int[]{SwingConstants.CENTER, SwingConstants.LEFT, SwingConstants.RIGHT},
                new int[]{60, 320, 160}), BorderLayout.CENTER);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        return card;
    }

    @Override
    public void onShow() {
        muatTabel();
        bersihkan();
    }

    private void muatTabel() {
        syncing = true;
        model.setRowCount(0);
        int i = 1;
        for (AppData.Produk p : AppData.produk()) {
            model.addRow(new Object[]{i++, p.nama, Ui.rp(p.harga)});
        }
        syncing = false;
        lblJumlah.setText(AppData.produk().size() + " produk");
    }

    private void pilihBaris() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        dipilih = AppData.produk().get(row);
        txtNama.setText(dipilih.nama);
        txtHarga.setText(String.valueOf(dipilih.harga));
    }

    /** Validasi input; mengembalikan null jika tidak valid. */
    private AppData.Produk bacaForm(AppData.Produk kecuali) {
        String nama = txtNama.getText().trim();
        long harga = Ui.parse(txtHarga.getText());
        if (nama.isEmpty()) {
            Ui.warn(this, "Nama produk wajib diisi.");
            txtNama.requestFocusInWindow();
            return null;
        }
        if (harga <= 0) {
            Ui.warn(this, "Harga harus lebih dari 0.");
            txtHarga.requestFocusInWindow();
            return null;
        }
        AppData.Produk sama = AppData.cariProduk(nama);
        if (sama != null && sama != kecuali) {
            Ui.warn(this, "Produk \"" + nama + "\" sudah ada.");
            txtNama.requestFocusInWindow();
            return null;
        }
        return new AppData.Produk(nama, harga);
    }

    private void tambah() {
        AppData.Produk baru = bacaForm(null);
        if (baru == null) {
            return;
        }
        AppData.produk().add(baru);
        muatTabel();
        bersihkan();
        Ui.info(this, "Produk \"" + baru.nama + "\" berhasil ditambahkan.");
    }

    private void ubah() {
        if (dipilih == null) {
            Ui.warn(this, "Pilih produk pada tabel terlebih dahulu.");
            return;
        }
        AppData.Produk data = bacaForm(dipilih);
        if (data == null) {
            return;
        }
        dipilih.nama = data.nama;
        dipilih.harga = data.harga;
        muatTabel();
        bersihkan();
        Ui.info(this, "Produk berhasil diperbarui.");
    }

    private void hapus() {
        if (dipilih == null) {
            Ui.warn(this, "Pilih produk pada tabel terlebih dahulu.");
            return;
        }
        if (Ui.confirm(this, "Hapus produk \"" + dipilih.nama + "\"?")) {
            AppData.produk().remove(dipilih);
            muatTabel();
            bersihkan();
        }
    }

    private void bersihkan() {
        dipilih = null;
        syncing = true;
        table.clearSelection();
        syncing = false;
        txtNama.setText("");
        txtHarga.setText("");
        txtNama.requestFocusInWindow();
    }
}
