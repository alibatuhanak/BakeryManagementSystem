package views;

import services.Settings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.sql.*;
import java.util.Base64;
import java.util.Objects;
import java.util.Vector;

public class ManageUsersPageAdmin extends JFrame {


    private DefaultTableModel RecordTable;
    private JPanel usersPanel;
    private JTable usersTable;
    private JTextField emailTF;
    private JTextField searchTF;
    private JButton btnAdd;
    private JTextField usernameTF;
    private JTextField passwordTF;
    private JTextField genderTF;
    private JTextField phoneTF;
    private JTextField addressTF;
    private JButton btnDelete;
    private JButton btnUpdate;
    private JButton btnReset;
    private JButton btnSearch;
    private JScrollPane scrollPane;
    private JTextField idTF;
    private JTextField balanceTF;
    private Connection mConnection;
    private Statement mStatement;
    private PreparedStatement mPreparedStatement;
    private ResultSet mResultSet;
    int ColumnCount, i;
    DefaultTableModel userModel;
    String sql = "SELECT * FROM users";

    public ManageUsersPageAdmin(JList list) {
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/logo_178x204.png")));
        this.setIconImage(imageIcon.getImage());
        this.setTitle("Safran Fırın Users Management");
        mConnection = Settings.connectDB();
        updatingDatabase(sql);
        this.add(usersPanel);
        this.setResizable(false);
        this.setSize(1000, 600);
        Settings.centerWindow(ManageUsersPageAdmin.this, 2);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        Settings.setIndex(this, list);
        this.setVisible(true);

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailTF.getText();
                String username = usernameTF.getText();
                String password = String.valueOf(passwordTF.getText());
                String gender = genderTF.getText();
                String phone = phoneTF.getText();
                String address = addressTF.getText();
                String balance = balanceTF.getText();

                if (balance.isEmpty() || gender.isEmpty() || email.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty() || phone.trim().isEmpty() || address.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Enter all fields.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() <= 7) {
                    JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Password should be greater than 7 characters.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!email.matches("[A-Za-z]+@.+")) {
                    JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Invalid email.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!phone.matches("[0-9]+")) {
                    JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Invalid phone.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                addUser(email, username, password, gender, phone, address, Float.parseFloat(balance));

                userModel = null;
                updatingDatabase(sql);
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = usersTable.getSelectedRow();
                int id = Integer.parseInt(usersTable.getModel().getValueAt(selectedRow, 0).toString());

                deleteUser(id);
                resetTF();
                userModel = null;
                updatingDatabase(sql);

            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String email = emailTF.getText();
                String username = usernameTF.getText();
                String password = String.valueOf(passwordTF.getText());
                String gender = genderTF.getText();
                String phone = phoneTF.getText();
                String address = addressTF.getText();
                String balance = balanceTF.getText();

                if (balance.isEmpty() || gender.isEmpty() || email.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty() || phone.trim().isEmpty() || address.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Enter all fields.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() <= 7) {
                    JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Password should be greater than 7 characters.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!email.matches("[A-Za-z]+@.+")) {
                    JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Invalid email.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!phone.matches("[0-9]+")) {
                    JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Invalid phone.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (userModel != null) {

                    int selectedRow = usersTable.getSelectedRow();
                    int id = Integer.parseInt(usersTable.getModel().getValueAt(selectedRow, 0).toString());

                    updateUser(id, email, username, password, gender, phone, address, Float.parseFloat(balance));
                    updatingDatabase(sql);
                }
                resetTF();

            }
        });
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetTF();
                JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Reseting successful.");

                usersTable.clearSelection();
                userModel = null;
                sql = "SELECT * FROM users";
                updatingDatabase(sql);
            }
        });
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchValue = searchTF.getText();
                searchUser(searchValue);

            }
        });

        usersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                userModel = (DefaultTableModel) usersTable.getModel();

                int id = (int) userModel.getValueAt(usersTable.getSelectedRow(), 0);
                String user_email = (String) userModel.getValueAt(usersTable.getSelectedRow(), 1);
                String user_username = (String) userModel.getValueAt(usersTable.getSelectedRow(), 2);
                String user_password = (String) userModel.getValueAt(usersTable.getSelectedRow(), 3);
                String user_gender = (String) userModel.getValueAt(usersTable.getSelectedRow(), 4);
                String user_phone = (String) userModel.getValueAt(usersTable.getSelectedRow(), 5);
                String user_address = (String) userModel.getValueAt(usersTable.getSelectedRow(), 6);
                float user_balance = (float) userModel.getValueAt(usersTable.getSelectedRow(), 7);

                byte[] decryptedPassword = Base64.getDecoder().decode(user_password);

                idTF.setText(String.valueOf(id));
                emailTF.setText(user_email);
                usernameTF.setText(user_username);
                passwordTF.setText(new String(decryptedPassword));
                genderTF.setText(user_gender);
                phoneTF.setText(user_phone);
                addressTF.setText(user_address);
                balanceTF.setText(String.valueOf(user_balance));

            }
        });
    }


    private void searchUser(String usernameText) {

        try {
            sql = "SELECT * FROM users WHERE user_username LIKE '" + usernameText + "%'";
            updatingDatabase(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void resetTF() {
        idTF.setText("");
        emailTF.setText("");
        usernameTF.setText("");
        passwordTF.setText("");
        genderTF.setText("");
        phoneTF.setText("");
        addressTF.setText("");
        balanceTF.setText("");
        searchTF.setText("");
    }

    private void addUser(String email, String username, String password, String gender, String phone, String address, float balance) {
        try {
            byte[] encryptedPassword = Base64.getEncoder().encode(password.getBytes());
            mStatement = mConnection.createStatement();
            String sql = "SELECT user_email FROM users WHERE user_email=?";
            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, email);
            mResultSet = mPreparedStatement.executeQuery();

            if (mResultSet.next()) {
                JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Email has allready been taken. Change email", "Try again", JOptionPane.ERROR_MESSAGE);
                return;
            }
            mStatement = mConnection.createStatement();
            sql = "SELECT user_username FROM users WHERE user_username=?";
            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, username);
            mResultSet = mPreparedStatement.executeQuery();

            if (mResultSet.next()) {
                JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Username has allready been taken. Change username", "Try again", JOptionPane.ERROR_MESSAGE);
                return;
            }

            sql = "INSERT INTO users(user_email,user_username,user_password,user_gender,user_phone,user_address, user_balance) VALUES(?,?,?,?,?,?,?)";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, email);
            mPreparedStatement.setString(2, username);
            mPreparedStatement.setString(3, new String(encryptedPassword));
            mPreparedStatement.setString(4, gender);
            mPreparedStatement.setString(5, phone);
            mPreparedStatement.setString(6, address);
            mPreparedStatement.setFloat(7, balance);

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Adding successful.");
            resetTF();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteUser(int id) {
        try {
            mStatement = mConnection.createStatement();

            String sql = "delete from users where id=?";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setInt(1, id);

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Deleting successful.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUser(int id, String email, String username, String password, String gender, String phone, String address, float balance) {
        byte[] encryptedPassword = Base64.getEncoder().encode(password.getBytes());
        try {
            mStatement = mConnection.createStatement();
            String sql = "SELECT user_email FROM users WHERE user_email=?";
            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, email);
            mResultSet = mPreparedStatement.executeQuery();

            if (mResultSet.next()) {
                if (!email.equals(mResultSet.getString("user_email"))) {
                    JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Email has allready been taken. Change email", "Try again", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            mStatement = mConnection.createStatement();
            sql = "SELECT user_username FROM users WHERE user_username=?";
            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, username);
            mResultSet = mPreparedStatement.executeQuery();

            if (mResultSet.next()) {
                if (!username.equals(mResultSet.getString("user_username"))) {
                    JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Username has allready been taken. Change username", "Try again", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            sql = "update users set user_email = ?, user_username=?, user_password=?,user_gender=?, user_phone=?, user_address=?, user_balance=? WHERE id=?";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, email);
            mPreparedStatement.setString(2, username);
            mPreparedStatement.setString(3, new String(encryptedPassword));
            mPreparedStatement.setString(4, gender);
            mPreparedStatement.setString(5, phone);
            mPreparedStatement.setString(6, address);
            mPreparedStatement.setFloat(7, balance);
            mPreparedStatement.setInt(8, id);

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageUsersPageAdmin.this, "Updating successful.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DefaultTableModel usersDataModel(String sql) {
        Object[][] data = {};
        String[] columnNames = {"id", "user_email", "user_username", "user_password", "user_gender", "user_phone", "user_address", "user_balance"};
        DefaultTableModel userModel = new DefaultTableModel(data, columnNames);
        userModel.setColumnIdentifiers(columnNames);
        try {
            Statement statement = mConnection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                int id = result.getInt("id");
                String email = result.getString("user_email");
                String username = result.getString("user_username");
                String password = result.getString("user_password");
                String gender = result.getString("user_gender");
                String phone = result.getString("user_phone");
                String address = result.getString("user_address");
                float balance = Float.parseFloat(result.getString("user_balance"));
                Object[] rowData = {id, email, username, password, gender, phone, address, balance};
                userModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userModel;
    }

    public void updatingDatabase(String sql) {
        try {
            mStatement = mConnection.createStatement();
            mResultSet = mStatement.executeQuery("select * from users");
            ResultSetMetaData stData = mResultSet.getMetaData();

            ColumnCount = stData.getColumnCount();
            RecordTable = (DefaultTableModel) usersTable.getModel();
            RecordTable.setRowCount(0);

            usersTable.setModel(usersDataModel(sql));

            TableRowSorter<TableModel> tableSorter1 = new TableRowSorter<>(usersTable.getModel());
            tableSorter1.setModel(usersTable.getModel());
            usersTable.setRowSorter(tableSorter1);

            while (mResultSet.next()) {
                Vector<String> columnData = new Vector<>();

                for (i = 1; i <= ColumnCount; i++) {
                    columnData.add(String.valueOf(mResultSet.getInt("id")));
                    columnData.add(mResultSet.getString("user_email"));
                    columnData.add(mResultSet.getString("user_username"));
                    columnData.add(mResultSet.getString("user_password"));
                    columnData.add(mResultSet.getString("user_phone"));
                    columnData.add(mResultSet.getString("user_address"));
                    columnData.add(String.valueOf(mResultSet.getFloat("user_balance")));
                }
                RecordTable.addRow(columnData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
