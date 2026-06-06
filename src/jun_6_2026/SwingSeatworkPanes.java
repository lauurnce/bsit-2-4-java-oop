import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SwingSeatworkPanes {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Swing Product Order Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 250);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel customerLabel = new JLabel("Customer Name:");
        JLabel productLabel  = new JLabel("Product Name:");
        JLabel quantityLabel = new JLabel("Quantity:");
        JLabel priceLabel    = new JLabel("Price:");

        JTextField customerField = new JTextField();
        JTextField productField  = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField    = new JTextField();

        JButton computeButton = new JButton("Compute Total");

        panel.add(customerLabel);
        panel.add(customerField);
        panel.add(productLabel);
        panel.add(productField);
        panel.add(quantityLabel);
        panel.add(quantityField);
        panel.add(priceLabel);
        panel.add(priceField);
        panel.add(new JLabel(""));
        panel.add(computeButton);

        frame.add(panel);

        computeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String customerName = customerField.getText();
                String productName  = productField.getText();
                int quantity        = Integer.parseInt(quantityField.getText());
                double price        = Double.parseDouble(priceField.getText());
                double totalAmount  = quantity * price;

                JOptionPane.showMessageDialog(
                    null,
                    "Customer Name: " + customerName + "\n" +
                    "Product Name: " + productName + "\n" +
                    "Total Amount: " + totalAmount,
                    "Order Summary",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        frame.setVisible(true);
    }
}
