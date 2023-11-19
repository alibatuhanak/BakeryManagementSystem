package views;

import services.Settings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import java.sql.*;
import java.util.Objects;

public class OrderPage extends JFrame {


    private JPanel panel1;
    private JTable table1;
    private PreparedStatement mPreparedStatement = null;
    private Connection mConnection = null;
    private Statement mStatement = null;
    private ResultSet mResultSet = null;
    DefaultTreeModel model;
    public String username;

    public OrderPage(String username){
        this.username = username;
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/logo_178x204.png")));
        this.setIconImage(imageIcon.getImage());
        this.setTitle("Orders Page Safran Fırın");
        Settings.centerWindow(this, 13);
        this.add(panel1);
        this.setResizable(false);
        table1.setModel(ProductDataModel("SELECT * FROM orders WHERE user_username=?"));
        setSize(1300, 750);
        this.setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setIconImage(imageIcon.getImage());
        table1.getColumnModel().getColumn(2).setPreferredWidth(200);
    }

    DefaultTableModel ProductDataModel(String sql) {
        Object[][] data = {};
        String[] columnNames = {"ID", "user_username", "product_name", "amount"};
        DefaultTableModel ProductModel= new DefaultTableModel(data, columnNames);
        ProductModel.setColumnIdentifiers(columnNames);
        try {
            mConnection = DriverManager.getConnection(Settings.DB_URL, Settings.USERNAME, Settings.PASSWORD);
            Statement statement = mConnection.createStatement();
            mPreparedStatement = mConnection.prepareStatement(sql);
            mPreparedStatement.setString(1,username);

            ResultSet result = mPreparedStatement.executeQuery();
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
}
