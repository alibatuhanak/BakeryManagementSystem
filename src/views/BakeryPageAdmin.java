package views;

import services.Settings;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

public class BakeryPageAdmin extends JFrame {
    private JPanel adminPanel;
    private JList list1;
    private JLabel adminL;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem aItem;
    private JMenuItem uItem;
    private JMenuItem eItem;
    private JMenuItem bpItem;
    private JMenuItem osItem;
    private JMenuItem exitItem;
    public BakeryPageAdmin() {
        ImageIcon imageIcon = new ImageIcon(
                Objects.requireNonNull(
                        this.getClass().
                                getResource("/images/logo_178x204.png")));
        this.setIconImage(imageIcon.getImage());
        this.setTitle("Safran Fırın Admin Dashboard");
        this.setVisible(true);
        this.setSize(900, 600);
        this.add(adminPanel);
        this.setResizable(false);
        Settings.centerWindow(BakeryPageAdmin.this, 2);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        menuBar = new JMenuBar();
        menuBar.setBackground(new Color(255, 255, 255));
        menuBar.setOpaque(true);

        fileMenu = new JMenu("File");

        aItem = new JMenuItem("Manage Admins");
        uItem = new JMenuItem("Manage Users");
        eItem = new JMenuItem("Manage Employees");
        bpItem = new JMenuItem("Manage Bakery Products");
        osItem = new JMenuItem("Manage Order Statistics");
        exitItem = new JMenuItem("Exit");

        fileMenu.setMnemonic(KeyEvent.VK_F);

        fileMenu.add(aItem);
        fileMenu.add(uItem);
        fileMenu.add(eItem);
        fileMenu.add(bpItem);
        fileMenu.add(osItem);
        fileMenu.add(exitItem);


        menuBar.add(fileMenu);

        this.setJMenuBar(menuBar);

        list1.setFixedCellHeight(40);
        list1.setSelectedIndex(0);

        list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedItem = list1.getSelectedIndex();

                boolean isAdjusting = e.getValueIsAdjusting();
                if (!isAdjusting) {
                    switch (selectedItem) {
                        case 0:
                            //default home
                            break;
                        case 1:
                            new ManageAdminsPageAdmin(list1);
                            break;
                        case 2:
                            new ManageUsersPageAdmin(list1);
                            break;
                        case 3:
                            new ManageEmployeesPageAdmin(list1);
                            break;
                        case 4:
                            new ManageBakeryProductsPageAdmin(list1);
                            break;
                        case 5:
                            new ManageOrderStatisticsPageAdmin(list1);
                            break;
                        default:
                            break;
                    }
                }
            }
        });


        aItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageAdminsPageAdmin(list1);
            }
        });
        uItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageUsersPageAdmin(list1);
            }
        });

        eItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageEmployeesPageAdmin(list1);
            }
        });
        bpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageBakeryProductsPageAdmin(list1);
            }
        });
        osItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 new ManageOrderStatisticsPageAdmin(list1);
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage("Admin");
            }
        });
    }
}
