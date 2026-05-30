package may_30_2026;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SwingGUI {
	

	    public static void main(String[] args) {
	        JFrame frame = new JFrame("My GUI");
	        frame.setSize(500, 450);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setLayout(new FlowLayout());

	        JLabel label = new JLabel("Enter your message");
	        JTextArea inputArea = new JTextArea(6, 35);
	        inputArea.setLineWrap(true);
	        inputArea.setWrapStyleWord(true);

	        String placeholder = "Enter your placeholder";

	        inputArea.setText(placeholder);
	        inputArea.setForeground(Color.gray);

	        inputArea.addFocusListener(new FocusListener() {
	            @Override
	            public void focusGained(FocusEvent e) {
	                if (inputArea.getText().equals(placeholder)) {
	                    inputArea.setText("");
	                    inputArea.setForeground(Color.black);
	                }
	            }

	            @Override
	            public void focusLost(FocusEvent e) {
	                if (inputArea.getText().isEmpty()) {
	                    inputArea.setText(placeholder);
	                    inputArea.setForeground(Color.gray);
	                }
	            }
	        });

	        JScrollPane inputScrollPane = new JScrollPane(inputArea);
	        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	        JButton button = new JButton("Submit");
	        JLabel outputLabel = new JLabel("Output:");

	        JTextArea outputArea = new JTextArea(6, 35);
	        outputArea.setLineWrap(true);
	        outputArea.setWrapStyleWord(true);
	        outputArea.setEditable(false);

	        JScrollPane outputScrollPane = new JScrollPane(outputArea);
	        outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	        outputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	        button.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                String inputText = inputArea.getText();
	                if (inputText.equals(placeholder)) {
	                    outputArea.setText("No remarks entered");
	                } else {
	                    outputArea.setText("You entered:\n" + inputText);
	                }
	            }
	        });

	        frame.add(label);
	        frame.add(inputScrollPane);
	        frame.add(button);
	        frame.add(outputLabel);
	        frame.add(outputScrollPane);
	        frame.setVisible(true);
	    }
}
