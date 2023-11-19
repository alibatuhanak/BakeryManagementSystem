package services;

import views.MainPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Settings {
    public static final String DB_PORT = "3306";
    public static final String DB_URL = "jdbc:mysql://localhost:"+DB_PORT+"/bakerydb";
    public static final String USERNAME = "root";
    public static final  String PASSWORD = "";
    private Connection mConnection = null;

    public static void centerWindow(Window frame, int d) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = frame.getSize();
        int x = (screenSize.width - windowSize.width) / d;
        int y = (screenSize.height - windowSize.height) /d;
        frame.setLocation(x, y);
    }
    public static Connection connectDB() {
       Connection mConnection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            mConnection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            System.out.println("Database connected successfully.");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return mConnection;
    }
   public static void setIndex(JFrame frame,JList list){
       frame.addWindowListener(new WindowAdapter() {
           @Override
           public void windowClosing(WindowEvent e) {
               list.setSelectedIndex(0);
               frame.dispose();
           }
       });
   }
   public static void menuCreater(JMenuBar menuBar,JMenu goMenu, JMenuItem backMenuItem, JFrame frame){
       menuBar = new JMenuBar();
       menuBar.setOpaque(true);

       goMenu = new JMenu("Go");
       backMenuItem = new JMenuItem("Back");

       goMenu.add(backMenuItem);

       menuBar.add(goMenu);

       frame.setJMenuBar(menuBar);
       backMenuItem.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               frame.dispose();
               new MainPage();
           }
       });
   }
}





