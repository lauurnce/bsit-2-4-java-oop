package ui;

import dao.BuyerDao;
import dao.LoanDao;
import dao.PropertyDao;
import model.Loan;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Summary report shown inside the main-menu dialog (with a Back button). */
public class ReportPanel extends JPanel {
    public ReportPanel() {
        setLayout(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setText(buildReport());
        add(new JScrollPane(area), BorderLayout.CENTER);
    }

    private String buildReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("     PROFRIENDS INC. — SUMMARY REPORT\n");
        sb.append("=========================================\n\n");
        try {
            int buyers = new BuyerDao().getAll().size();
            int props = new PropertyDao().getAll().size();
            List<Loan> loans = new LoanDao().getAll();

            double totalLoan = 0, totalDp = 0, totalMonthly = 0;
            for (Loan l : loans) {
                totalLoan += l.getLoanAmount();
                totalDp += l.getDownpayment();
                totalMonthly += l.computeMonthlyAmortization(); // polymorphic
            }

            sb.append(String.format("Total Buyers      : %d%n", buyers));
            sb.append(String.format("Total Properties  : %d%n", props));
            sb.append(String.format("Total Loans       : %d%n%n", loans.size()));
            sb.append(String.format("Total Loan Value  : %,.2f%n", totalLoan));
            sb.append(String.format("Total Downpayments: %,.2f%n", totalDp));
            sb.append(String.format("Total Monthly Amort: %,.2f%n%n", totalMonthly));

            sb.append("--- Per-Loan Monthly Amortization ---\n");
            for (Loan l : loans) {
                sb.append(String.format("Loan #%d (%s): %,.2f%n",
                        l.getLoanId(), l.getFinanceType(), l.computeMonthlyAmortization()));
            }
        } catch (Exception ex) {
            sb.append("Error generating report: ").append(ex.getMessage());
        }
        return sb.toString();
    }
}
