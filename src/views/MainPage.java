package views;

import services.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class MainPage extends JFrame {
    private JLabel iconLabel;
    private JPanel MainPagePanel;
    private JButton btnAdmin;
    private JButton btnUser;
    public MainPage() {
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/logo_178x204.png")));
        this.setIconImage(imageIcon.getImage());
        this.setTitle("Safran Fırın Management System");
        this.add(MainPagePanel);
        this.setSize(550, 614);
        Settings.centerWindow(this, 2);
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage("Admin");
            }
        });
        btnUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage("User");
            }
        });
    }
}
