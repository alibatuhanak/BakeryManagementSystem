package views;

import services.Settings;

import javax.swing.*;
import java.util.Objects;

public class About extends JFrame {
    private JLabel aboutLabel;
    private JPanel panel;
    public About() {
        this.add(panel);
        ImageIcon imageIcon = new ImageIcon(
                Objects.requireNonNull(this.getClass().getResource("/images/logo_178x204.png"))
        );
        this.setIconImage(imageIcon.getImage());
        this.setVisible(true);
        this.setSize(1010, 800);
        this.setResizable(false);
        Settings.centerWindow(this, 2);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}



