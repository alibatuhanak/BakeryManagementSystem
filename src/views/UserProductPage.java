package views;

import com.google.gson.Gson;
import models.Product;
import services.Settings;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class UserProductPage extends JFrame {

    TableColumn col;
    DefaultTableModel productModel;
    int numberOfProducts = 1;
    private String username;
    private JPanel Panel;
    private JTable table;
    private JButton BtnConfirm;
    private JPanel userPanel;
    private JTree tree1;
    private JLabel userLabel;
    private JLabel BalanceLabel;
    private Float resultBalance;
    private PreparedStatement mPreparedStatement = null;
    private Connection mConnection = null;
    private Statement mStatement = null;
    private ResultSet mResultSet = null;
    private JButton BtnCancel;
    private JScrollPane scrolltable;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JMenuItem aItem;
    private JMenuItem oItem;

    private JMenuItem exitItem;
    private JMenuItem aboutItem;
    public float amount = 0;
    float getAmount = 0;

    DefaultTreeModel model;
    boolean isSelected;
    DefaultTableModel ProductModel;

    DefaultMutableTreeNode product = new DefaultMutableTreeNode("Products");

    public UserProductPage(String username) {
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/logo_178x204.png")));
        this.setIconImage(imageIcon.getImage());

        JComboBox<Integer> comboBox = new JComboBox<Integer>(new Integer[]{1, 2, 3, 4, 5});
        Settings.centerWindow(this, 13);
        this.setTitle("Safran Fırın User Dashboard");
        this.username = username;
        this.setVisible(true);
        this.add(Panel);
        setSize(1400, 750);
        this.setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        mConnection = Settings.connectDB();

        Load();
        loadData("SELECT * FROM bakery_products");
        userLabel.setText("Welcome " + username);
        userLabel.setForeground(Color.WHITE);
        BtnCancel.setForeground(Color.WHITE);
        this.setIconImage(imageIcon.getImage());

        table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));

        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(255, 255, 255));
        menuBar.setOpaque(true);

        fileMenu = new JMenu("Account");
        helpMenu = new JMenu("Help");

        oItem = new JMenuItem("Orders");
        aItem = new JMenuItem("Add Balance");
        exitItem = new JMenuItem("Exit");

        aboutItem = new JMenuItem("About");

        fileMenu.setMnemonic(KeyEvent.VK_A);
        helpMenu.setMnemonic(KeyEvent.VK_H);

        fileMenu.add(oItem);
        fileMenu.add(aItem);
        fileMenu.add(exitItem);

        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        this.setJMenuBar(menuBar);

        final TableColumn[] col = {table.getColumnModel().getColumn(1)};
        col[0].setCellEditor(new DefaultCellEditor(comboBox));

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage("User");
            }
        });

        aItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AddBalancePage(username);
            }
        });
        oItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OrderPage(username);
            }
        });
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new About();
            }
        });

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
                JOptionPane.showMessageDialog(this, "Not found balance.", "Invalid balance", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        BalanceLabel.setText("Balance: " + resultBalance + " TL");
        BalanceLabel.setForeground(Color.WHITE);

        tree1.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                String selectedValue = e.getPath().getLastPathComponent().toString();
                TableColumn col;

                switch (selectedValue) {
                    case "Products":

                        loadData("SELECT * FROM bakery_products");

                        table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());
                        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));

                        col = table.getColumnModel().getColumn(1);
                        col.setCellEditor(new DefaultCellEditor(comboBox));
                        break;
                    case "Breads":
                        loadData("SELECT * FROM bakery_products WHERE product_type='breads'");
                        table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());
                        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
                        col = table.getColumnModel().getColumn(1);
                        col.setCellEditor(new DefaultCellEditor(comboBox));
                        break;
                    case "Pastries":
                        loadData("SELECT * FROM bakery_products WHERE product_type='pastries'");
                        table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());
                        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
                        col = table.getColumnModel().getColumn(1);
                        col.setCellEditor(new DefaultCellEditor(comboBox));
                        break;
                    case "Cookies":
                        loadData("SELECT * FROM bakery_products WHERE product_type='cookies'");
                        table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());
                        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
                        col = table.getColumnModel().getColumn(1);
                        col.setCellEditor(new DefaultCellEditor(comboBox));
                        break;
                    case "Cakes":
                        loadData("SELECT * FROM bakery_products WHERE product_type='cakes'");
                        table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());
                        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
                        col = table.getColumnModel().getColumn(1);
                        col.setCellEditor(new DefaultCellEditor(comboBox));
                        break;
                    case "Milky Desserts":
                        loadData("SELECT * FROM bakery_products WHERE product_type='milky desserts'");
                        table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());
                        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
                        col = table.getColumnModel().getColumn(1);
                        col.setCellEditor(new DefaultCellEditor(comboBox));
                        break;
                    case "Siruped Desserts":
                        loadData("SELECT * FROM bakery_products WHERE product_type='siruped desserts'");
                        table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());
                        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
                        col = table.getColumnModel().getColumn(1);
                        col.setCellEditor(new DefaultCellEditor(comboBox));
                        break;
                    case "Drinks":
                        loadData("SELECT * FROM bakery_products WHERE product_type='drinks'");
                        table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());
                        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
                        col = table.getColumnModel().getColumn(1);
                        col.setCellEditor(new DefaultCellEditor(comboBox));
                        break;
                    default:
                        break;
                }

            }
        });


        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                productModel = (DefaultTableModel) table.getModel();

                isSelected = (boolean) productModel.getValueAt(table.getSelectedRow(), 0);
                numberOfProducts = (int) productModel.getValueAt(table.getSelectedRow(), 1);
                int id = (int) productModel.getValueAt(table.getSelectedRow(), 2);
                String productName = (String) productModel.getValueAt(table.getSelectedRow(), 3);
                String productType = (String) productModel.getValueAt(table.getSelectedRow(), 4);
                float productPrice = (float) productModel.getValueAt(table.getSelectedRow(), 5);
                byte[] image1 = (byte[]) productModel.getValueAt(table.getSelectedRow(), 6);
                int productStock = (int) productModel.getValueAt(table.getSelectedRow(), 7);

                try {
                    mStatement = mConnection.createStatement();
                    String sql = "UPDATE bakery_products SET product_isSelected=?, product_quantity=? WHERE id = ?";

                    mPreparedStatement = mConnection.prepareStatement(sql);
                    mPreparedStatement.setBoolean(1, isSelected);
                    mPreparedStatement.setInt(2, numberOfProducts);
                    mPreparedStatement.setInt(3, id);

                    mPreparedStatement.executeUpdate();

                } catch (SQLException es) {
                    es.printStackTrace();
                }
                try {
                    mStatement = mConnection.createStatement();
                    String sql = "SELECT * FROM bakery_products WHERE product_isSelected = '1'";
                    mPreparedStatement = mConnection.prepareStatement(sql);
                    ResultSet rs = mPreparedStatement.executeQuery();

                    while (rs.next()) {

                        float price = rs.getFloat("product_price");
                        int qty = rs.getInt("product_quantity");
                        amount += qty * price;

                    }

                } catch (SQLException es) {
                    es.printStackTrace();
                }
            }
        });


        BtnConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    ArrayList<String> productList = new ArrayList<>();
                    ArrayList<Float> productPrice = new ArrayList<>();
                    ArrayList<Integer> productQuantity = new ArrayList<>();
                    ArrayList<Integer> productStock = new ArrayList<>();

                    mStatement = mConnection.createStatement();
                    String sql = "SELECT * FROM bakery_products WHERE product_isSelected = '1'";

                    ResultSet resultSet = mPreparedStatement.executeQuery(sql);
                    int stock = 0;
                    while (resultSet.next()) {

                        String name = resultSet.getString("product_name");
                        float price = resultSet.getFloat("product_price");
                        int qty = resultSet.getInt("product_quantity");
                        stock = resultSet.getInt("product_stock");
                        getAmount = qty * price;

                        productPrice.add(getAmount);
                        productList.add(name);
                        productQuantity.add(qty);
                        productStock.add(stock - qty);

                    }
                    float totalAmount = 0;

                    for (int i = 0; i < productPrice.size(); i++) {

                        totalAmount += productPrice.get(i);

                    }

                    int option = JOptionPane.showConfirmDialog(UserProductPage.this, productList.toString() + "   Total: " + totalAmount+" TL", "Are you sure ?!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (option == JOptionPane.YES_OPTION) {

                        mStatement = mConnection.createStatement();

                        resultBalance = resultBalance - totalAmount;

                        for (int i = 0; i < productPrice.size(); i++) {

                            sql = "UPDATE bakery_products SET product_stock= ? WHERE product_name=?";

                            mPreparedStatement = mConnection.prepareStatement(sql);

                            mPreparedStatement.setInt(1, productStock.get(i));
                            mPreparedStatement.setString(2, productList.get(i));

                            mPreparedStatement.executeUpdate();

                        }
                        sql = "UPDATE users SET user_balance=? WHERE user_username=?";
                        mPreparedStatement = mConnection.prepareStatement(sql);
                        mPreparedStatement.setFloat(1, resultBalance);
                        mPreparedStatement.setString(2, username);
                        mPreparedStatement.executeUpdate();

                        BalanceLabel.setText("Balance: " + resultBalance + " TL");

                        Gson gson = new Gson();

                        String json = gson.toJson(productList);

                        sql = "INSERT INTO orders(user_username,product_name,amount)  values(?,?,?)";

                        mPreparedStatement = mConnection.prepareStatement(sql);
                        mPreparedStatement.setString(1, username);
                        mPreparedStatement.setObject(2, json);
                        mPreparedStatement.setFloat(3, totalAmount);
                        mPreparedStatement.executeUpdate();

                        loadData("SELECT * FROM bakery_products");

                        table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());
                        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
                        col[0] = table.getColumnModel().getColumn(1);
                        col[0].setCellEditor(new DefaultCellEditor(comboBox));
                        resetProducts(comboBox);

                    } else {
                        System.out.println("No data");
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }


            }
        });
        BtnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetProducts(comboBox);


            }
        });
    }

    public void resetProducts(JComboBox comboBox){
        try {
            mStatement = mConnection.createStatement();
            String sql = "UPDATE bakery_products SET product_isSelected=?, product_quantity=? ";

            mPreparedStatement = mConnection.prepareStatement(sql);
            mPreparedStatement.setBoolean(1, false);
            mPreparedStatement.setInt(2, 1);

            mPreparedStatement.executeUpdate();
            loadData("SELECT * FROM bakery_products");
            TableColumn col;
            table.getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());
            table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()));
            col = table.getColumnModel().getColumn(1);
            col.setCellEditor(new DefaultCellEditor(comboBox));

        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    static class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        public CheckBoxRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setSelected((Boolean) value);
            return this;
        }
    }

    public void Load() {
        product.add(new DefaultMutableTreeNode("Breads"));
        product.add(new DefaultMutableTreeNode("Pastries"));
        product.add(new DefaultMutableTreeNode("Cookies"));
        product.add(new DefaultMutableTreeNode("Cakes"));
        product.add(new DefaultMutableTreeNode("Milky Desserts"));
        product.add(new DefaultMutableTreeNode("Siruped Desserts"));
        product.add(new DefaultMutableTreeNode("Drinks"));

        model = (DefaultTreeModel) tree1.getModel();
        model.setRoot(product);
        tree1.setModel(model);
    }

    private void loadData(String sql) {
        DefaultTableModel defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn("select");
        defaultTableModel.addColumn("quantity");
        defaultTableModel.addColumn("id");
        defaultTableModel.addColumn("product_name");
        defaultTableModel.addColumn("product_type");
        defaultTableModel.addColumn("product_price");
        defaultTableModel.addColumn("product_image");
        defaultTableModel.addColumn("product_stock");

        for (Product product : getAll(sql)) {
            defaultTableModel.addRow(new Object[]{
                    product.getSelected(),
                    product.getCount(),
                    product.getId(),
                    product.getName(),
                    product.getType(),
                    product.getPrice(),
                    product.getImage(),
                    product.getStock(),
            });

        }

        table.setModel(defaultTableModel);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(6).setCellRenderer(new ImageRender());
        table.setRowHeight(200);
        table.getColumnModel().getColumn(6).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);
        table.getColumnModel().getColumn(7).setPreferredWidth(200);

    }


    public java.util.List<Product> getAll(String sql) {
        List<Product> products = new ArrayList<Product>();

        try {
            PreparedStatement preparedStatement = mConnection
                    .prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                Product product = new Product();
                product.setSelected(resultSet.getBoolean("product_isSelected"));
                product.setCount(resultSet.getInt("product_quantity"));
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("product_name"));
                product.setType(resultSet.getString("product_type"));
                product.setPrice(resultSet.getFloat("product_price"));
                product.setImage(resultSet.getBytes("product_image"));
                product.setStock(resultSet.getInt("product_stock"));
                products.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    private static class ImageRender extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel jLabel = new JLabel();

            byte[] bytes = (byte[]) value;

            ImageIcon imageIcon = new ImageIcon(new ImageIcon(bytes).getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT));
            jLabel.setIcon(imageIcon);
            jLabel.setHorizontalAlignment(CENTER);
            return jLabel;

        }
    }

}
