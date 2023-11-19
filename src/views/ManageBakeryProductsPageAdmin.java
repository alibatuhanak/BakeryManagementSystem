package views;

import models.Product;
import services.Settings;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.List;

public class ManageBakeryProductsPageAdmin extends JFrame {
    private JButton btnAdd;
    private JButton btnReset;
    private JButton btnDelete;
    private JButton btnUpdate;
    private JScrollPane scrollPane;
    private JTable productsTable;
    private JTextField productTypeTF;
    private JTextField productPriceTF;
    private JTextField idTF;
    private JTextField searchTF;
    private JButton btnSearch;
    private JPanel productsPanel;
    private JButton btnBrowse;
    private JTextField productNameTF;
    private JTextField productStockTF;
    private JLabel imageLabel;
    private Connection mConnection;
    private Statement mStatement;
    private PreparedStatement mPreparedStatement;
    private ResultSet mResultSet;
    private DefaultTableModel RecordTable;
    int ColumnCount, i;
    DefaultTableModel productModel;
    String sql = "SELECT * FROM bakery_products";
    String path = System.getProperty("user.dir") + "/src/images/img.png";

    public ManageBakeryProductsPageAdmin(JList list) {
        ImageIcon imageIcon = new ImageIcon(Objects
                .requireNonNull(this.getClass()
                        .getResource("/images/logo_178x204.png")));
        this.setIconImage(imageIcon.getImage());
        this.setTitle("Safran Fırın Bakery Products Management");
        mConnection = Settings.connectDB();
        loadData(sql);

        this.add(productsPanel);
        this.setResizable(false);
        this.setSize(1250, 850);
        Settings.centerWindow(ManageBakeryProductsPageAdmin.this, 2);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        Settings.setIndex(this, list);
        this.setVisible(true);

        Image firstImage = getScaledImage(new ImageIcon(path).getImage(), 150, 150);
        ImageIcon finalImage = new ImageIcon(firstImage);
        imageLabel.setIcon(finalImage);

        btnBrowse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(System.getProperty("user.home")));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.IMAGE",
                        "jpg", "png", "gif", "jpeg");
                fc.addChoosableFileFilter(filter);
                int result = fc.showSaveDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fc.getSelectedFile();
                    path = selectedFile.getAbsolutePath();

                    Image selectedImage = getScaledImage(new ImageIcon(path).getImage(), 128, 128);
                    ImageIcon finalImage = new ImageIcon(selectedImage);
                    imageLabel.setIcon(finalImage);
                } else {
                    System.out.println("No data");
                }

            }
        });

        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    mStatement = mConnection.createStatement();

                    sql = "INSERT INTO bakery_products(product_name,product_type,product_price,product_image,product_stock) VALUES(?,?,?,?,?)";

                    String productPath = path;
                    InputStream is = new FileInputStream(new File(productPath));

                    String productName = productNameTF.getText();
                    String productType = productTypeTF.getText();
                    float productPrice = Float.parseFloat(productPriceTF.getText());
                    int productStock = Integer.parseInt(productStockTF.getText());

                    if (productName.trim().isEmpty() || productType.trim().isEmpty() || productPrice == 0.0 || productStock == 0 || productPath.equals(System.getProperty("user.dir") + "/src/images/img.png")) {
                        JOptionPane.showMessageDialog(ManageBakeryProductsPageAdmin.this, "Enter all fields.", "Try Again", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    mPreparedStatement = mConnection.prepareStatement(sql);

                    mPreparedStatement.setString(1, productName);
                    mPreparedStatement.setString(2, productType);
                    mPreparedStatement.setFloat(3, productPrice);
                    mPreparedStatement.setBlob(4, is);
                    mPreparedStatement.setInt(5, productStock);

                    mPreparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(productsPanel, "Data added.", "Successfully.", JOptionPane.INFORMATION_MESSAGE);

                    resetTF();
                    productModel = null;
                    sql = "SELECT * FROM bakery_products";
                    loadData(sql);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = productsTable.getSelectedRow();
                int id = Integer.parseInt(productsTable.getModel().getValueAt(selectedRow, 0).toString());

                deleteProduct(id);
                resetTF();
                productModel = null;
                sql = "SELECT * FROM bakery_products";
                loadData(sql);

            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Icon productImage = imageLabel.getIcon();
                String productName = productNameTF.getText();
                String productType = productTypeTF.getText();
                float productPrice = Float.parseFloat(productPriceTF.getText());
                int productStock = Integer.parseInt(productStockTF.getText());

                if (productName.trim().isEmpty() || productType.trim().isEmpty() ||
                        String.valueOf(productPrice).trim().isEmpty() || String.valueOf(productStock).trim().isEmpty()) {
                    JOptionPane.showMessageDialog(ManageBakeryProductsPageAdmin.this,
                            "Enter all fields.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (productModel != null) {

                    int selectedRow = productsTable.getSelectedRow();
                    int id = Integer.parseInt(productsTable.getModel().getValueAt(selectedRow, 0).toString());

                    updateProduct(id, productName, productType, productPrice, path, productStock);
                    productModel = null;
                    sql = "SELECT * FROM bakery_products";
                    loadData(sql);
                }
                resetTF();
            }
        });
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetTF();
                JOptionPane.showMessageDialog(ManageBakeryProductsPageAdmin.this,
                        "Reseting successful.");

                productsTable.clearSelection();
                productModel = null;
                sql = "SELECT * FROM bakery_products";
                loadData(sql);
            }
        });
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchValue = searchTF.getText();
                searchProduct(searchValue);
            }
        });

        productsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                productModel = (DefaultTableModel) productsTable.getModel();

                if (productsTable.getValueAt(productsTable.getSelectedRow(), 4) != null) {

                    int id = (int) productModel.getValueAt(productsTable.getSelectedRow(), 0);
                    String productName = (String) productModel.getValueAt(productsTable.getSelectedRow(), 1);
                    String productType = (String) productModel.getValueAt(productsTable.getSelectedRow(), 2);
                    float productPrice = (float) productModel.getValueAt(productsTable.getSelectedRow(), 3);
                    byte[] image1 = (byte[]) productModel.getValueAt(productsTable.getSelectedRow(), 4);
                    int productStock = (int) productModel.getValueAt(productsTable.getSelectedRow(), 5);

                    idTF.setText(String.valueOf(id));
                    productNameTF.setText(productName);
                    productTypeTF.setText(productType);
                    productPriceTF.setText(String.valueOf(productPrice));
                    Image selectedImage = getScaledImage(new ImageIcon(image1).getImage(), 128, 128);
                    ImageIcon finalImage = new ImageIcon(selectedImage);
                    imageLabel.setIcon(finalImage);
                    productStockTF.setText(String.valueOf(productStock));
                } else {
                    System.out.println("No image");
                }
            }
        });
    }


    private void searchProduct(String nameText) {
        try {
            sql = "SELECT * FROM bakery_products WHERE product_name LIKE '" + nameText + "%'";
            loadData(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void resetTF() {
        idTF.setText("");
        productNameTF.setText("");
        productTypeTF.setText("");
        productPriceTF.setText("0.0");
        path = System.getProperty("user.dir") + "/src/images/img.png";
        ImageIcon imageIcon1 = new ImageIcon(path);
        Image image2 = getScaledImage(imageIcon1.getImage(), 128, 128);
        ImageIcon imageIcon3 = new ImageIcon(image2);
        imageLabel.setIcon(imageIcon3);
        productStockTF.setText("0");
        searchTF.setText("");
    }

    private void deleteProduct(int id) {
        try {
            mStatement = mConnection.createStatement();

            String sql = "delete from bakery_products where id=?";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setInt(1, id);

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageBakeryProductsPageAdmin.this,
                    "Deleting successful.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateProduct(int id, String product_name, String product_type,
                               float product_price, String product_image, int product_stock) {
        try {
            mStatement = mConnection.createStatement();
            sql = "update bakery_products set product_name = ?, product_type=?," +
                    " product_price=?,product_image=?,product_stock=? " +
                    "WHERE id=?";

            mPreparedStatement = mConnection.prepareStatement(sql);

            InputStream is = new FileInputStream(new File(product_image));

            mPreparedStatement.setString(1, product_name);
            mPreparedStatement.setString(2, product_type);
            mPreparedStatement.setFloat(3, product_price);
            mPreparedStatement.setBlob(4, is);
            mPreparedStatement.setInt(5, product_stock);
            mPreparedStatement.setInt(6, id);

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageBakeryProductsPageAdmin.this, "Updating successful.");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadData(String sql) {
        DefaultTableModel defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn("id");
        defaultTableModel.addColumn("product_name");
        defaultTableModel.addColumn("product_type");
        defaultTableModel.addColumn("product_price");
        defaultTableModel.addColumn("product_image");
        defaultTableModel.addColumn("product_stock");

        for (Product product : getAll(sql)) {
            defaultTableModel.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    product.getType(),
                    product.getPrice(),
                    product.getImage(),
                    product.getStock(),
            });
        }

        productsTable.setModel(defaultTableModel);
        productsTable.getTableHeader().setReorderingAllowed(false);
        productsTable.getColumnModel().getColumn(4).setCellRenderer(new ImageRender());
        productsTable.setRowHeight(200);
        productsTable.getColumnModel().getColumn(4).setPreferredWidth(200);
    }

    private static class ImageRender extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel jLabel = new JLabel();

            byte[] bytes = (byte[]) value;

            ImageIcon imageIcon = new ImageIcon(new ImageIcon(bytes).getImage()
                    .getScaledInstance(200, 200, Image.SCALE_DEFAULT));
            jLabel.setIcon(imageIcon);
            jLabel.setHorizontalAlignment(CENTER);
            return jLabel;
        }
    }

    public List<Product> getAll(String sql) {
        List<Product> products = new ArrayList<Product>();
        try {
            PreparedStatement preparedStatement = mConnection
                    .prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Product product = new Product();
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

    private static Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }
}
