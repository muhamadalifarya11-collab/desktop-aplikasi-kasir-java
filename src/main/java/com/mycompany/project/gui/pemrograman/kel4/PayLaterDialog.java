package com.mycompany.project.gui.pemrograman.kel4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

/**
 * Dialog pembayaran PayLater. Kasir mengisi data pelanggan, lalu
 * transaksi langsung disetujui otomatis dan dicatat sebagai tagihan.
 *
 * @author Kelompok 4
 */
public class PayLaterDialog extends JDialog {

    private static final String LIMIT_INFO = "Limit " + Ui.rp(AppData.LIMIT_PAYLATER) + " per pelanggan";

    private final long amount;
    private final String keterangan;
    private final JTextField txtHp;
    private final JTextField txtNama;
    private final JComboBox<String> cmbTenor;
    private final JLabel lblSisaLimit;
    private final JLabel lblStatus;
    private final ModernButton btnProses;
    private final ModernButton btnBatal;
    private boolean busy;
    private AppData.Tagihan result;

    /**
     * Menampilkan form PayLater.
     *
     * @return tagihan yang dibuat, atau null jika dibatalkan
     */
    public static AppData.Tagihan proses(Component parent, long amount, String keterangan) {
        Window owner = parent instanceof Window w ? w : SwingUtilities.getWindowAncestor(parent);
        PayLaterDialog d = new PayLaterDialog(owner, amount, keterangan);
        d.setVisible(true);
        return d.result;
    }

    private PayLaterDialog(Window owner, long amount, String keterangan) {
        super(owner, "Pembayaran PayLater", ModalityType.APPLICATION_MODAL);
        this.amount = amount;
        this.keterangan = keterangan;
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(22, 28, 24, 28));

        // Judul
        JPanel top = new JPanel(new BorderLayout(0, 14));
        top.setOpaque(false);
        top.add(Ui.cardTitle("PayLater Warkop", AppIcon.Type.CARD), BorderLayout.NORTH);

        RoundedPanel info = new RoundedPanel(new GridLayout(2, 1, 0, 4), 14, new Color(0xF1ECFD),
                new Color(0xD9CCF7), false, 14);
        JLabel lblTotal = new JLabel("Total belanja : " + Ui.rp(amount));
        lblTotal.setFont(Ui.font(Font.BOLD, 20));
        lblTotal.setForeground(Ui.PURPLE);
        info.add(lblTotal);
        lblSisaLimit = Ui.muted(LIMIT_INFO, 15);
        info.add(lblSisaLimit);
        top.add(info, BorderLayout.CENTER);
        root.add(top, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        txtHp = Ui.textField("", true, 13);
        txtNama = Ui.textField("", true, 0);
        cmbTenor = new JComboBox<>(new String[]{"14 Hari", "30 Hari"});
        Ui.styleCombo(cmbTenor);

        Ui.addRow(form, 0, Ui.formLabel("No. HP", AppIcon.Type.PHONE), Ui.wrap(txtHp, false, false), 215);
        Ui.addRow(form, 1, Ui.formLabel("Nama Pelanggan", AppIcon.Type.USER), Ui.wrap(txtNama, false, false), 215);
        Ui.addRow(form, 2, Ui.formLabel("Tenor", AppIcon.Type.CLOCK), Ui.wrap(cmbTenor, false, false), 215);
        root.add(form, BorderLayout.CENTER);

        // Status + tombol
        JPanel bottom = new JPanel(new BorderLayout(0, 14));
        bottom.setOpaque(false);
        lblStatus = new JLabel(" ");
        lblStatus.setFont(Ui.font(Font.BOLD, 15));
        lblStatus.setForeground(Ui.MUTED);
        lblStatus.setHorizontalAlignment(JLabel.CENTER);
        bottom.add(lblStatus, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 14, 0));
        buttons.setOpaque(false);
        buttons.setPreferredSize(new Dimension(0, 56));
        btnBatal = new ModernButton("Batal", AppIcon.Type.REFRESH, Ui.GRAY);
        btnProses = new ModernButton("Proses PayLater", AppIcon.Type.CHECK, Ui.PURPLE);
        btnBatal.setFont(Ui.font(Font.BOLD, 18));
        btnProses.setFont(Ui.font(Font.BOLD, 18));
        buttons.add(btnBatal);
        buttons.add(btnProses);
        bottom.add(buttons, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(btnProses);

        Ui.onChange(txtHp, this::cekPelanggan);
        btnBatal.addActionListener(e -> tutup());
        btnProses.addActionListener(e -> prosesPayLater());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                tutup();
            }
        });

        setSize(640, 500);
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    /** Mengisi nama otomatis untuk pelanggan lama dan menampilkan sisa limit. */
    private void cekPelanggan() {
        String hp = txtHp.getText();
        if (hp.length() < 10) {
            lblSisaLimit.setText(LIMIT_INFO);
            lblSisaLimit.setForeground(Ui.MUTED);
            return;
        }
        long sisa = AppData.sisaLimit(hp);
        String nama = AppData.namaPelanggan(hp);
        if (nama != null && txtNama.getText().isBlank()) {
            SwingUtilities.invokeLater(() -> txtNama.setText(nama));
        }
        lblSisaLimit.setText((nama != null ? "Pelanggan terdaftar: " + nama + "  -  " : "Pelanggan baru  -  ")
                + "Sisa limit " + Ui.rp(sisa));
        lblSisaLimit.setForeground(sisa >= amount ? Ui.GREEN : Ui.RED);
    }

    private void prosesPayLater() {
        if (busy) {
            return;
        }
        String hp = txtHp.getText();
        String nama = txtNama.getText().trim();
        if (hp.length() < 10) {
            Ui.warn(this, "No. HP minimal 10 digit.");
            txtHp.requestFocusInWindow();
            return;
        }
        if (nama.isEmpty()) {
            Ui.warn(this, "Nama pelanggan wajib diisi.");
            txtNama.requestFocusInWindow();
            return;
        }
        long sisa = AppData.sisaLimit(hp);
        if (amount > sisa) {
            Ui.warn(this, "Limit PayLater tidak cukup.\nSisa limit " + nama + ": " + Ui.rp(sisa)
                    + "\nSilakan lunasi tagihan sebelumnya atau pilih metode lain.");
            return;
        }
        int tenor = cmbTenor.getSelectedIndex() == 0 ? 14 : 30;

        busy = true;
        btnProses.setEnabled(false);
        btnBatal.setEnabled(false);
        lblStatus.setText("Memverifikasi akun PayLater...");
        lblStatus.setForeground(Ui.ORANGE);

        Timer verifikasi = new Timer(1300, e -> {
            result = AppData.tambahTagihan(nama, hp, LocalDateTime.now(), tenor, amount, keterangan);
            lblStatus.setText("Disetujui! " + result.kode + "  -  jatuh tempo " + result.jatuhTempo.format(Ui.DATE));
            lblStatus.setForeground(Ui.GREEN);
            Timer tutup = new Timer(1400, e2 -> dispose());
            tutup.setRepeats(false);
            tutup.start();
        });
        verifikasi.setRepeats(false);
        verifikasi.start();
    }

    private void tutup() {
        if (!busy) {
            dispose();
        }
    }
}
