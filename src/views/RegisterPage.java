package views;

import models.User;
import services.Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Base64;
import java.util.Objects;

public class RegisterPage extends JFrame {


    private JPanel registerPanel;
    private JTextField emailTF;
    private JButton btnRegister;
    private JPasswordField passwordTF;
    private JButton btnCancel;
    private JTextField usernameTF;
    private JPasswordField rePasswordTF;
    private JTextField phoneTF;
    private JTextField addressTF;
    private JRadioButton btnFemale;
    private JRadioButton btnMale;
    private ButtonGroup group = new ButtonGroup();

    private Connection mConnection = null;
    private Statement mStatement = null;
    private PreparedStatement mPreparedStatement = null;
    private ResultSet mResultSet = null;

    private User user;

    public RegisterPage() {
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/logo_178x204.png")));
        this.setIconImage(imageIcon.getImage());
        this.setTitle("Safran Fırın Register System");

        this.add(registerPanel);
        this.setSize(500, 700);
        Settings.centerWindow(this, 2);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        group.add(this.btnMale);
        group.add(this.btnFemale);

        mConnection = Settings.connectDB();

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage("User");
            }
        });
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = emailTF.getText();
        String username = usernameTF.getText();
        String password = String.valueOf(passwordTF.getPassword());
        String rePassword = String.valueOf(rePasswordTF.getPassword());
        String gender = ((btnMale.isSelected()) ? btnMale.getText() : (btnFemale.isSelected() ? btnFemale.getText() : ""));
        String phone = phoneTF.getText();
        String address = addressTF.getText();
        float balance = 1000f;

        if (gender.isEmpty() || email.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty() || phone.trim().isEmpty() || address.trim().isEmpty()) {
            JOptionPane.showMessageDialog(RegisterPage.this, "Enter all fields.", "Try Again", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(rePassword)) {
            JOptionPane.showMessageDialog(RegisterPage.this, "Passwords does not match.", "Try Again", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.length() <= 7) {
            JOptionPane.showMessageDialog(RegisterPage.this, "Password should be greater than 7 characters.", "Try Again", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!email.matches("[A-Za-z-0-9]+@.+")) {
            JOptionPane.showMessageDialog(RegisterPage.this, "Invalid email.", "Try Again", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!phone.matches("[0-9]+")) {
            JOptionPane.showMessageDialog(RegisterPage.this, "Invalid phone.", "Try Again", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            mStatement = mConnection.createStatement();
            String sql = "SELECT * FROM users WHERE user_email=?";
            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, email);
            mResultSet = mPreparedStatement.executeQuery();

            if (mResultSet.next()) {
                JOptionPane.showMessageDialog(RegisterPage.this, "Email has allready been taken. Change email", "Try again", JOptionPane.ERROR_MESSAGE);
                return;
            }
            mStatement = mConnection.createStatement();
            sql = "SELECT * FROM users WHERE user_username=?";
            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, username);
            mResultSet = mPreparedStatement.executeQuery();

            if (mResultSet.next()) {
                JOptionPane.showMessageDialog(RegisterPage.this, "Username has allready been taken. Change username", "Try again", JOptionPane.ERROR_MESSAGE);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        byte[] encryptedPassword = Base64.getEncoder().encode(password.getBytes());
        user = createUser(email, username, new String(encryptedPassword), gender, phone, address, balance);

        if (user != null) {
            new LoginPage("User");
            dispose();
        } else {
            JOptionPane.showMessageDialog(RegisterPage.this,
                    "Failed to register new user",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private User createUser(String email, String username, String password, String gender, String phone, String address, float balance) {
        user = null;
        mConnection = null;
        try {
            mConnection = DriverManager.getConnection(Settings.DB_URL, Settings.USERNAME, Settings.PASSWORD);
            mStatement = mConnection.createStatement();
            String sql = "INSERT INTO users(user_email,user_username,user_password,user_gender,user_phone,user_address, user_balance) VALUES (?,?,?,?,?,?,?)";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, email);
            mPreparedStatement.setString(2, username);
            mPreparedStatement.setString(3, password);
            mPreparedStatement.setString(4, gender);
            mPreparedStatement.setString(5, phone);
            mPreparedStatement.setString(6, address);
            mPreparedStatement.setFloat(7, balance);

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Register successfully.");

            user = new User(email, username, password, gender, phone, address, balance);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }


}
