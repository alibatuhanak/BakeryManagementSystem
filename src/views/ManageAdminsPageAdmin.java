package views;

import services.Settings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Base64;
import java.util.Objects;
import java.util.Vector;

public class ManageAdminsPageAdmin extends JFrame {
    private JScrollPane scrollPane;
    private JTable adminsTable;
    private JTextField emailTF;
    private JTextField usernameTF;
    private JButton btnDelete;
    private JButton btnUpdate;
    private JButton btnReset;
    private JButton btnAdd;
    private JPasswordField passwordTF;
    private JTextField idTF;
    private JButton btnSearch;
    private JTextField searchTF;
    private JPanel adminsPanel;
    private Connection mConnection;
    private Statement mStatement;
    private PreparedStatement mPreparedStatement;
    private ResultSet mResultSet;
    private DefaultTableModel RecordTable;
    int ColumnCount, i;
    DefaultTableModel adminModel;
    String sql = "SELECT * FROM admins";

    public ManageAdminsPageAdmin(JList list) {
        ImageIcon imageIcon = new ImageIcon(
                Objects
                        .requireNonNull(this
                                .getClass()
                                .getResource("/images/logo_178x204.png")));
        this.setIconImage(imageIcon.getImage());
        this.setTitle("Safran Fırın Admins Management");
        mConnection = Settings.connectDB();
        updatingDatabase(sql);
        this.add(adminsPanel);
        this.setResizable(false);
        this.setSize(1000, 600);
        Settings.centerWindow(ManageAdminsPageAdmin.this, 2);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        Settings.setIndex(this, list);
        this.setVisible(true);

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailTF.getText();
                String username = usernameTF.getText();
                String password = String.valueOf(passwordTF.getText());

                if (email.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this,
                            "Enter all fields.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() <= 7) {
                    JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this,
                            "Password should be greater than 7 characters.", "Try Again",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!email.matches("[A-Za-z]+@.+")) {
                    JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this,
                            "Invalid email.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                addAdmin(email, username, password);
                adminModel = null;
                updatingDatabase(sql);
            }
        });


        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = adminsTable.getSelectedRow();
                int id = Integer.parseInt(adminsTable
                        .getModel()
                        .getValueAt(selectedRow, 0).toString());

                deleteAdmin(id);
                resetTF();
                adminModel = null;
                updatingDatabase(sql);
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String email = emailTF.getText();
                String username = usernameTF.getText();
                String password = String.valueOf(passwordTF.getText());

                if (email.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this,
                            "Enter all fields.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() <= 7) {
                    JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this,
                            "Password should be greater than 7 characters.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!email.matches("[A-Za-z]+@.+")) {
                    JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this,
                            "Invalid email.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (adminModel != null) {
                    int selectedRow = adminsTable.getSelectedRow();
                    int id = Integer.parseInt(adminsTable.getModel().getValueAt(selectedRow, 0).toString());

                    updateAdmin(id, email, username, password);
                    updatingDatabase(sql);
                }
                resetTF();
            }
        });
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetTF();
                JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this,
                        "Reseting successful.");

                adminsTable.clearSelection();
                adminModel = null;
                sql = "SELECT * FROM admins";
                updatingDatabase(sql);
            }
        });
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchValue = searchTF.getText();
                searchAdmin(searchValue);

            }
        });

        adminsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                adminModel = (DefaultTableModel) adminsTable.getModel();

                int id = (int) adminModel.getValueAt(adminsTable.getSelectedRow(), 0);
                String admin_email = (String) adminModel.getValueAt(adminsTable.getSelectedRow(), 1);
                String admin_username = (String) adminModel.getValueAt(adminsTable.getSelectedRow(), 2);
                String admin_password = (String) adminModel.getValueAt(adminsTable.getSelectedRow(), 3);

                byte[] decryptedPassword = Base64.getDecoder().decode(admin_password);

                idTF.setText(String.valueOf(id));
                emailTF.setText(admin_email);
                usernameTF.setText(admin_username);
                passwordTF.setText(new String(decryptedPassword));

            }
        });
    }


    private void searchAdmin(String usernameText) {

        try {
            sql = "SELECT * FROM admins WHERE admin_username LIKE '" + usernameText + "%'";
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
        searchTF.setText("");
    }

    private void addAdmin(String email, String username, String password) {
        try {
            byte[] encryptedPassword = Base64.getEncoder().encode(password.getBytes());
            mStatement = mConnection.createStatement();
            String sql = "SELECT admin_email FROM admins WHERE admin_email=?";
            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, email);
            mResultSet = mPreparedStatement.executeQuery();

            if (mResultSet.next()) {
                JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this,
                        "Email has allready been taken. Change email", "Try again", JOptionPane.ERROR_MESSAGE);
                return;
            }
            mStatement = mConnection.createStatement();
            sql = "SELECT admin_username FROM admins WHERE admin_username=?";
            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, username);
            mResultSet = mPreparedStatement.executeQuery();

            if (mResultSet.next()) {
                JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this,
                        "Username has allready been taken. Change username", "Try again", JOptionPane.ERROR_MESSAGE);
                return;
            }

            sql = "INSERT INTO admins(admin_email,admin_username,admin_password) VALUES(?,?,?)";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, email);
            mPreparedStatement.setString(2, username);
            mPreparedStatement.setString(3, new String(encryptedPassword));

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this, "Adding successful.");
            resetTF();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteAdmin(int id) {
        try {
            mStatement = mConnection.createStatement();

            String sql = "delete from admins where id=?";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setInt(1, id);

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this, "Deleting successful.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateAdmin(int id, String email, String username, String password) {
        byte[] encryptedPassword = Base64.getEncoder().encode(password.getBytes());
        try {
            mStatement = mConnection.createStatement();
            String sql = "SELECT admin_email FROM admins WHERE admin_email=?";
            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, email);
            mResultSet = mPreparedStatement.executeQuery();

            if (mResultSet.next()) {
                if (!email.equals(mResultSet.getString("admin_email"))) {
                    JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this,
                            "Email has allready been taken. Change email", "Try again", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            mStatement = mConnection.createStatement();
            sql = "SELECT admin_username FROM admins WHERE admin_username=?";
            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, username);
            mResultSet = mPreparedStatement.executeQuery();

            if (mResultSet.next()) {
                if (!username.equals(mResultSet.getString("admin_username"))) {
                JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this,
                        "Username has allready been taken. Change username", "Try again", JOptionPane.ERROR_MESSAGE);
                return;
            }}


            sql = "update admins set admin_email = ?, admin_username=?, admin_password=? WHERE id=?";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, email);
            mPreparedStatement.setString(2, username);
            mPreparedStatement.setString(3, new String(encryptedPassword));
            mPreparedStatement.setInt(4, id);

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageAdminsPageAdmin.this, "Updating successful.");


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private DefaultTableModel adminsDataModel(String sql) {
        Object[][] data = {};
        String[] columnNames = {"id", "admin_email", "admin_username", "admin_password"};
        DefaultTableModel adminModel = new DefaultTableModel(data, columnNames);
        adminModel.setColumnIdentifiers(columnNames);
        try {
            Statement statement = mConnection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                int id = result.getInt("id");
                String email = result.getString("admin_email");
                String username = result.getString("admin_username");
                String password = result.getString("admin_password");

                Object[] rowData = {id, email, username, password};
                adminModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return adminModel;
    }

    public void updatingDatabase(String sql) {
        try {
            mStatement = mConnection.createStatement();

            mResultSet = mStatement.executeQuery("select * from admins");
            ResultSetMetaData stData = mResultSet.getMetaData();

            ColumnCount = stData.getColumnCount();
            RecordTable = (DefaultTableModel) adminsTable.getModel();
            RecordTable.setRowCount(0);

            adminsTable.setModel(adminsDataModel(sql));

            TableRowSorter<TableModel> tableSorter1 = new TableRowSorter<>(adminsTable.getModel());
            tableSorter1.setModel(adminsTable.getModel());
            adminsTable.setRowSorter(tableSorter1);

            while (mResultSet.next()) {
                Vector<String> columnData = new Vector<>();

                for (i = 1; i <= ColumnCount; i++) {
                    columnData.add(String.valueOf(mResultSet.getInt("id")));
                    columnData.add(mResultSet.getString("admin_email"));
                    columnData.add(mResultSet.getString("admin_username"));
                    columnData.add(mResultSet.getString("admin_password"));
                }
                RecordTable.addRow(columnData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
