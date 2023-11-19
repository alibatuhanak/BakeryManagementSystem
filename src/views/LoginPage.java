package views;

import models.Admin;
import models.User;
import services.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.Base64;
import java.util.Objects;

public class LoginPage extends JFrame {
    private JTextField usernameTextField;
    private JPasswordField passwordTextField;
    private JButton loginBtn;
    private JButton registerBtn;
    private JPanel loginPanel;
    private JLabel loginPageTextField;
    private JLabel registerTextField;
    private JPanel panelEmpty;
    private PreparedStatement mPreparedStatement = null;
    private Connection mConnection = null;
    private Statement mStatement = null;
    private ResultSet mResultSet = null;
    public User user;
    public Admin admin;
    public JMenuBar menuBar;
    public JMenu goMenu;
    public JMenuItem backMenuItem;
    String name = "";

    public LoginPage(String name) {
        ImageIcon imageIcon = new ImageIcon(
                Objects
                        .requireNonNull(this.getClass()
                                .getResource("/images/logo_178x204.png")));
        this.setIconImage(imageIcon.getImage());
        this.setTitle("Safran Fırın Login System");
        this.name = name;
        add(loginPanel);
        mConnection = Settings.connectDB();
        this.setSize(550, 350);
        Settings.centerWindow(this, 2);
        loginPageTextField.setText(name + " Login Page");
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Settings.menuCreater(menuBar, goMenu, backMenuItem, this);
        this.setVisible(true);

        if (name.equals("Admin")) {
            registerBtn.setVisible(false);
            registerTextField.setVisible(false);
            loginBtn.setMargin(new Insets(0, 150, 0, 150));
        }

        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clickEvent();
            }
        });


        passwordTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    clickEvent();
                }
            }
        });
        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegisterPage();
            }
        });
    }

    public void clickEvent() {
        String username = usernameTextField.getText();
        String password = String.valueOf(passwordTextField.getPassword());
        byte[] encryptedPassword = Base64.getEncoder().encode(password.getBytes());

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane
                    .showMessageDialog(LoginPage.this,
                            "Enter all fields.", "Invalid user.",
                            JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (name.equals("Admin")) {
            admin = authAdmin(username, new String(encryptedPassword));
        } else {
            user = authUser(username, new String(encryptedPassword));
        }
        if (admin != null && user == null) {
            JOptionPane
                    .showMessageDialog(this,
                            "Welcome the Admin Dashboard Admin: " + admin.username, "👤",
                            JOptionPane.PLAIN_MESSAGE);
            dispose();
            new BakeryPageAdmin();
        }
        if (user != null && admin == null) {
            JOptionPane
                    .showMessageDialog(this,
                            "Welcome the User Dashboard User: " + user.username, "👤",
                            JOptionPane.PLAIN_MESSAGE);
            dispose();
            new UserProductPage(username);
        }
    }
    public User authUser(String username, String password) {
        String resultUsername,resultPassword;
        user = null;
        try {
            mConnection = DriverManager.getConnection(Settings.DB_URL, Settings.USERNAME, Settings.PASSWORD);
            mStatement = mConnection.createStatement();
            String sql = "SELECT * FROM users WHERE user_username=?";
            mPreparedStatement = mConnection.prepareStatement(sql);
            mPreparedStatement.setString(1, username);
            ResultSet mResultSet = mPreparedStatement.executeQuery();
            if (mResultSet.next()) {
                resultUsername = mResultSet.getString("user_username");
            } else {
                JOptionPane.showMessageDialog(this, "Not found user.",
                        "Invalid User", JOptionPane.WARNING_MESSAGE);
                return user;
            }
            sql = "SELECT * FROM users WHERE user_username=? AND user_password=?";
            mPreparedStatement = mConnection.prepareStatement(sql);
            mPreparedStatement.setString(1, resultUsername);
            mPreparedStatement.setString(2, password);
            mResultSet = mPreparedStatement.executeQuery();
            if (mResultSet.next()) {
                resultPassword = mResultSet.getString("user_password");
            } else {
                JOptionPane.showMessageDialog(this, "Password is wrong.", "Invalid User",
                        JOptionPane.WARNING_MESSAGE);
                return user;
            }
            user = new User(resultUsername, resultPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }  return user;


    }

    public Admin authAdmin(String username, String password) {
        String resultUsername,resultPassword;
        admin = null;
        try {
            mConnection = DriverManager.getConnection(Settings.DB_URL, Settings.USERNAME, Settings.PASSWORD);
            mStatement = mConnection.createStatement();
            String sql = "SELECT * FROM admins WHERE admin_username=?";
            mPreparedStatement = mConnection.prepareStatement(sql);
            mPreparedStatement.setString(1, username);
            ResultSet mResultSet = mPreparedStatement.executeQuery();
            if (mResultSet.next()) {
                resultUsername = mResultSet.getString("admin_username");
            } else {
                JOptionPane.showMessageDialog(this, "Not found admin.",
                        "Invalid Admin", JOptionPane.WARNING_MESSAGE);
                return admin;
            }
            sql = "SELECT * FROM admins WHERE admin_username=?  AND admin_password=?";
            mPreparedStatement = mConnection.prepareStatement(sql);
            mPreparedStatement.setString(1, resultUsername);
            mPreparedStatement.setString(2, password);
            mResultSet = mPreparedStatement.executeQuery();
            if (mResultSet.next()) {
                resultPassword = mResultSet.getString("admin_password");
            } else {
                JOptionPane.showMessageDialog(this, "Password is wrong.",
                        "Invalid Admin", JOptionPane.WARNING_MESSAGE);
                return admin;
            }
            admin = new Admin(resultUsername, resultPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admin;
    }
}
