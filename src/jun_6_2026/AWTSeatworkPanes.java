import java.awt.*;

public class AWTSeatworkPanes {
    public static void main(String[] args) {

        Frame frame = new Frame("AWT Student Information Form");

        Panel panel = new Panel();
        panel.setLayout(new GridLayout(4, 2, 10, 10));

        Label nameLabel    = new Label("Student Name:");
        Label courseLabel  = new Label("Course:");
        Label sectionLabel = new Label("Section:");

        TextField nameField    = new TextField();
        TextField courseField  = new TextField();
        TextField sectionField = new TextField();

        Button submitButton = new Button("Submit");

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(courseLabel);
        panel.add(courseField);
        panel.add(sectionLabel);
        panel.add(sectionField);
        panel.add(new Label(""));
        panel.add(submitButton);

        frame.add(panel);
        frame.setSize(350, 200);
        frame.setVisible(true);
    }
}
