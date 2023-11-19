package views;

import services.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Objects;

public class AddBalancePage extends JFrame {
    private JButton button;
    private JPanel panel;
    private JLabel label;
    private JComboBox comboBox1;
    private JLabel balanceLabel;
    private PreparedStatement mPreparedStatement = null;
    private Connection mConnection = null;
    private Statement mStatement = null;
    private ResultSet mResultSet = null;
    private float resultBalance;
    private String username;
    private float selectedBalance = 1000;

    public AddBalancePage(String username) {
        Settings.centerWindow(this, 3);
        ImageIcon imageIcon = new ImageIcon(Objects
                .requireNonNull(this.getClass()
                        .getResource("/images/logo_178x204.png")));
        this.setIconImage(imageIcon.getImage());
        this.setTitle("Add Balance Safran Fırın");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
        this.username = username;
        this.add(panel);
        setSize(350, 350);
        getBalance();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                updateBalance();
                new UserProductPage(username);
                dispose();
            }
        });

        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int addBalanceIndex = comboBox1.getSelectedIndex();
                switch (addBalanceIndex) {
                    case 0:
                        selectedBalance = 1000;
                        break;
                    case 1:
                        selectedBalance = 2000;
                        break;

                    case 2:
                        selectedBalance = 3000;
                        break;

                    case 3:
                        selectedBalance = 4000;
                        break;
                    case 4:
                        selectedBalance = 5000;
                        break;
                }
            }
        });
    }

    public void getBalance() {
        try {
            mConnection = DriverManager.getConnection(Settings.DB_URL, Settings.USERNAME, Settings.PASSWORD);
            mStatement = mConnection.createStatement();
            String sql = "SELECT * FROM users WHERE user_username=?";

            mPreparedStatement = mConnection.prepareStatement(sql);
            mPreparedStatement.setString(1, username);

            ResultSet mResultSet = mPreparedStatement.executeQuery();

            if (mResultSet.next()) {
                resultBalance = mResultSet.getFloat("user_balance");
            } else {

                JOptionPane
                        .showMessageDialog(this,
                                "Not found balance.", "Invalid balance",
                                JOptionPane.WARNING_MESSAGE);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        balanceLabel.setText("Balance: " + resultBalance + " TL");
        balanceLabel.setForeground(Color.BLACK);
    }

    public void updateBalance() {
        try {

            mConnection = DriverManager.getConnection(Settings.DB_URL, Settings.USERNAME, Settings.PASSWORD);
            mStatement = mConnection.createStatement();
            String sql = "SELECT * FROM users WHERE user_username=?";

            mPreparedStatement = mConnection.prepareStatement(sql);
            mPreparedStatement.setString(1, username);

            ResultSet mResultSet = mPreparedStatement.executeQuery();

            if (mResultSet.next()) {
                mStatement = mConnection.createStatement();
                sql = "UPDATE users SET user_balance = ? WHERE user_username = ?";
                mPreparedStatement = mConnection.prepareStatement(sql);

                mPreparedStatement.setFloat(1, (resultBalance + selectedBalance));
                mPreparedStatement.setString(2, username);

                mPreparedStatement.executeUpdate();

            } else {
                JOptionPane
                        .showMessageDialog(this,
                                "Not found balance.", "Invalid balance",
                                JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
