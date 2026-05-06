package gui.panels;

import auth.SessionManager;
import gui.util.AppColors;
import gui.util.AppFonts;
import gui.util.UIHelper;
import payment.dao.PaymentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.UUID;

public class PaymentPanel extends JPanel {

    private JTable   historyTable;
    private JLabel   totalLabel;
    private final String role;

    public PaymentPanel() {
        this.role = SessionManager.getRole();
        setBackground(AppColors.BG_PRIMARY);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(AppColors.BG_PRIMARY);
        header.setBorder(new EmptyBorder(24, 28, 8, 28));
        header.add(UIHelper.heading(role.equals("TRAINER") ? "Payments Received" : "Payment"));

        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(AppColors.BG_PRIMARY);
        body.setBorder(new EmptyBorder(8, 28, 28, 28));

        if (role.equals("TRAINEE")) body.add(buildPayForm(), BorderLayout.NORTH);
        body.add(buildHistoryPanel(), BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(body,   BorderLayout.CENTER);

        loadPayments();
    }

    private JPanel buildPayForm() {
        JPanel card = UIHelper.card();
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));

        List<String[]> trainers;
        try {
            trainers = PaymentDAO.getAllTrainers();
        } catch (Exception ex) { return card; }

        String[] trainerNames = trainers.stream()
                .map(t -> t[1] + " (" + t[2] + " SAR)")
                .toArray(String[]::new);

        JComboBox<String> trainerBox = UIHelper.comboBox(trainerNames);
        JComboBox<String> typeBox    = UIHelper.comboBox(
                new String[]{"MADA", "APPLE_PAY", "CREDIT_CARD", "STC_PAY"});
        JTextField amountF = UIHelper.inputField("Amount (SAR)");
        amountF.setPreferredSize(new Dimension(140, 36));

        JButton payBtn = UIHelper.primaryButton("Make Payment");

        card.add(UIHelper.muted("Trainer: "));
        card.add(trainerBox);
        card.add(UIHelper.muted("  Method: "));
        card.add(typeBox);
        card.add(UIHelper.muted("  Amount: "));
        card.add(amountF);
        card.add(payBtn);

        payBtn.addActionListener(e -> {
            if (trainers.isEmpty()) { UIHelper.showError(this, "No trainers available."); return; }
            int trainerId = Integer.parseInt(trainers.get(trainerBox.getSelectedIndex())[0]);
            String type   = (String) typeBox.getSelectedItem();
            double amount;
            try {
                amount = Double.parseDouble(amountF.getText().trim());
            } catch (NumberFormatException ex) {
                UIHelper.showError(this, "Invalid amount."); return;
            }
            processPayment(trainerId, type, amount);
        });

        return card;
    }

    private JPanel buildHistoryPanel() {
        JPanel card = UIHelper.card();
        card.setLayout(new BorderLayout(0, 10));

        JLabel title = UIHelper.label("Payment History", AppFonts.SUBHEADING, AppColors.TEXT_PRIMARY);

        String[] cols = role.equals("TRAINER")
                ? new String[]{"#", "Date", "Method", "Amount (SAR)", "Trainer Earns", "Status", "Trainee"}
                : new String[]{"#", "Date", "Method", "Amount (SAR)", "Fee (5%)", "Trainer Earns", "Status", "Ref", "Trainer"};

        historyTable = UIHelper.styledTable(cols);
        historyTable.getColumnModel().getColumn(0).setMaxWidth(40);

        totalLabel = UIHelper.label("Total Paid: — SAR", AppFonts.BODY, AppColors.ACCENT);
        totalLabel.setBorder(new EmptyBorder(6, 4, 0, 0));

        JButton refreshBtn = UIHelper.ghostButton("Refresh");
        refreshBtn.addActionListener(e -> loadPayments());

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(AppColors.BG_CARD);
        south.add(totalLabel,  BorderLayout.WEST);
        south.add(refreshBtn,  BorderLayout.EAST);

        card.add(title,                            BorderLayout.NORTH);
        card.add(UIHelper.scrollPane(historyTable),BorderLayout.CENTER);
        card.add(south,                            BorderLayout.SOUTH);
        return card;
    }

    private void processPayment(int trainerId, String type, double amount) {
        try {
            int paymentId = PaymentDAO.savePayment(
                    SessionManager.getUserId(), trainerId, type, amount);

            // Simulate gateway (90% success)
            boolean success = Math.random() < 0.90;
            String  ref     = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String  status  = success ? "COMPLETED" : "FAILED";

            PaymentDAO.updateStatus(paymentId, status, success ? ref : null);

            if (success) {
                UIHelper.showSuccess(this,
                        String.format("Payment approved!%nRef: %s%nPlatform fee (5%%): %.2f SAR%nTrainer receives: %.2f SAR",
                                ref, amount * 0.05, amount * 0.95));
            } else {
                UIHelper.showError(this, "Payment was declined by the gateway. Please try again.");
            }
            loadPayments();
        } catch (Exception ex) {
            UIHelper.showError(this, "Payment error: " + ex.getMessage());
        }
    }

    private void loadPayments() {
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0);
        double total = 0;
        try {
            List<String[]> rows = role.equals("TRAINER")
                    ? PaymentDAO.getPaymentsByTrainer(SessionManager.getUserId())
                    : PaymentDAO.getPaymentsByTrainee(SessionManager.getUserId());

            for (String[] r : rows) {
                model.addRow(r);
                if ("COMPLETED".equals(r[role.equals("TRAINER") ? 5 : 6])) {
                    total += Double.parseDouble(r[3]);
                }
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to load payments: " + ex.getMessage());
        }
        totalLabel.setText(String.format("Total: %.2f SAR", total));
    }
}
