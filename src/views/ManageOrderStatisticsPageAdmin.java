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
import java.util.Objects;
import java.util.Vector;

public class ManageOrderStatisticsPageAdmin extends JFrame {
    private JPanel orderPanel;
    private JScrollPane scrollPane;
    private JTable ordersTable;
    private JTextField usernameTF;
    private JTextField productNameTF;
    private JTextField amountTF;
    private JButton btnDelete;
    private JButton btnUpdate;
    private JButton btnReset;
    private JButton btnAdd;
    private JTextField idTF;
    private JTextField departmentTF;
    private JButton btnSearch;
    private JTextField searchTF;
    private Connection mConnection;
    private Statement mStatement;
    private PreparedStatement mPreparedStatement;
    private ResultSet mResultSet;
    int ColumnCount, i;
    DefaultTableModel orderModel;
    String sql = "SELECT * FROM orders";
    private DefaultTableModel RecordTable;

    public ManageOrderStatisticsPageAdmin(JList list) {
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/logo_178x204.png")));
        this.setIconImage(imageIcon.getImage());
        this.setTitle("Safran Fırın Order Statistics Management");
        mConnection = Settings.connectDB();
        updatingDatabase(sql);
        this.add(orderPanel);
        this.setResizable(false);
        this.setSize(1000, 600);
        Settings.centerWindow(ManageOrderStatisticsPageAdmin.this, 2);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        Settings.setIndex(this, list);
        this.setVisible(true);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = usernameTF.getText();
                String productName = productNameTF.getText();
                float amount = Float.parseFloat(amountTF.getText());

                if (userName.isEmpty() || productName.isEmpty() || String.valueOf(amount).isEmpty()) {
                    JOptionPane.showMessageDialog(ManageOrderStatisticsPageAdmin.this, "Enter all fields.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                addOrder(userName, productName, amount);

                orderModel = null;
                sql = "SELECT * FROM orders";
                updatingDatabase(sql);
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ordersTable.getSelectedRow();
                int id = Integer.parseInt(ordersTable.getModel().getValueAt(selectedRow, 0).toString());


                deleteEmployee(id);
                resetTF();
                orderModel = null;
                sql = "SELECT * FROM orders";
                updatingDatabase(sql);


            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String userName = usernameTF.getText();
                String productName = productNameTF.getText();
                float amount = Float.parseFloat(amountTF.getText());

                if (userName.isEmpty() || productName.isEmpty() || String.valueOf(amount).isEmpty()) {
                    JOptionPane.showMessageDialog(ManageOrderStatisticsPageAdmin.this, "Enter all fields.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (orderModel != null) {

                    int selectedRow = ordersTable.getSelectedRow();
                    int id = Integer.parseInt(ordersTable.getModel().getValueAt(selectedRow, 0).toString());

                    updateOrder(id, userName, productName, amount);
                    sql = "SELECT * FROM orders";
                    updatingDatabase(sql);
                }
                resetTF();

            }
        });
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                resetTF();
                JOptionPane.showMessageDialog(ManageOrderStatisticsPageAdmin.this, "Reseting successful.");

                ordersTable.clearSelection();
                orderModel = null;
                sql = "SELECT * FROM orders";
                updatingDatabase(sql);
            }
        });
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchValue = searchTF.getText();
                searchEmployee(searchValue);


            }
        });

        ordersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                orderModel = (DefaultTableModel) ordersTable.getModel();

                int id = (int) orderModel.getValueAt(ordersTable.getSelectedRow(), 0);
                String userName = (String) orderModel.getValueAt(ordersTable.getSelectedRow(), 1);
                String productName = (String) orderModel.getValueAt(ordersTable.getSelectedRow(), 2);
                float amount = (Float) orderModel.getValueAt(ordersTable.getSelectedRow(), 3);


                idTF.setText(String.valueOf(id));
                usernameTF.setText(userName);
                productNameTF.setText(productName);
                amountTF.setText(String.valueOf(amount));

            }
        });
    }


    private void searchEmployee(String userName) {
        try {
            sql = "SELECT * FROM orders WHERE user_username LIKE '" + userName + "%'";
            updatingDatabase(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void resetTF() {
        idTF.setText("");
        usernameTF.setText("");
        productNameTF.setText("");
        amountTF.setText("");
        searchTF.setText("");
    }

    private void addOrder(String user_username, String product_name, float amount) {
        try {

            mStatement = mConnection.createStatement();
            sql = "INSERT INTO orders(user_username,product_name,amount) VALUES(?,?,?)";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, user_username);
            mPreparedStatement.setString(2, product_name);
            mPreparedStatement.setFloat(3, amount);


            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageOrderStatisticsPageAdmin.this, "Adding successful.");
            resetTF();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteEmployee(int id) {
        try {
            mStatement = mConnection.createStatement();

            String sql = "delete from orders where id=?";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setInt(1, id);

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageOrderStatisticsPageAdmin.this, "Deleting successful.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateOrder(int id, String username, String productName, float amount) {
        try {
            mStatement = mConnection.createStatement();
            sql = "update orders set user_username = ?, product_name=?, amount=? WHERE id=?";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, username);
            mPreparedStatement.setString(2, productName);
            mPreparedStatement.setFloat(3, amount);
            mPreparedStatement.setInt(4, id);

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageOrderStatisticsPageAdmin.this, "Updating successful.");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    DefaultTableModel ordersDataModel(String sql) {
        Object[][] data = {};
        String[] columnNames = {"ID", "user_username", "product_name", "amount"};
        DefaultTableModel ProductModel= new DefaultTableModel(data, columnNames);
        ProductModel.setColumnIdentifiers(columnNames);
        try {
            mConnection = DriverManager.getConnection(Settings.DB_URL, Settings.USERNAME, Settings.PASSWORD);
            Statement statement = mConnection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                int id = result.getInt("id");
                String userName = result.getString("user_username");
                String productName = result.getString("product_name");
                float amount = result.getFloat("amount");
                Object[] rowData = {id, userName, productName, amount};
                ProductModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ProductModel;
    }

    public void updatingDatabase(String sql) {

        try {

            mStatement = mConnection.createStatement();
            mResultSet = mStatement.executeQuery(sql);

            ResultSetMetaData stData = mResultSet.getMetaData();

            ColumnCount = stData.getColumnCount();
            RecordTable = (DefaultTableModel) ordersTable.getModel();
            RecordTable.setRowCount(0);

            ordersTable.setModel(ordersDataModel(sql));

            TableRowSorter<TableModel> tableSorter1 = new TableRowSorter<>(ordersTable.getModel());
            tableSorter1.setModel(ordersTable.getModel());
            ordersTable.setRowSorter(tableSorter1);

            while (mResultSet.next()) {
                Vector columnData = new Vector<>();
                for (i = 1; i <= ColumnCount; i++) {
                    columnData.add(mResultSet.getInt("id"));
                    columnData.add(mResultSet.getString("user_username"));
                    columnData.add(mResultSet.getString("product_name"));
                    columnData.add(mResultSet.getFloat("amount"));
                }
                RecordTable.addRow(columnData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

