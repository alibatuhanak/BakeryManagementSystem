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

public class ManageEmployeesPageAdmin extends JFrame {
    private JPanel employeesPanel;
    private JScrollPane scrollPane;
    private JTable employeesTable;
    private JTextField fullnameTF;
    private JTextField genderTF;
    private JTextField ageTF;
    private JTextField salaryTF;
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
    DefaultTableModel employeeModel;
    String sql = "SELECT * FROM employees";
    private DefaultTableModel RecordTable;

    public ManageEmployeesPageAdmin(JList list){
        ImageIcon imageIcon = new ImageIcon(Objects
                .requireNonNull(this.getClass()
                        .getResource("/images/logo_178x204.png")));
        this.setIconImage(imageIcon.getImage());
        this.setTitle("Safran Fırın Employees Management");
        mConnection = Settings.connectDB();
        updatingDatabase(sql);
        this.add(employeesPanel);
        this.setResizable(false);
        this.setSize(1000, 600);
        Settings.centerWindow(ManageEmployeesPageAdmin.this, 2);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        Settings.setIndex(this, list);
        this.setVisible(true);
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fullName = fullnameTF.getText();
                String gender = genderTF.getText();
                int age = Integer.parseInt(ageTF.getText());
                float salary = Float.parseFloat(salaryTF.getText());
                String department = departmentTF.getText();

                if (fullName.isEmpty() || gender.isEmpty()
                        || String.valueOf(age).isEmpty() || String.valueOf(salary).isEmpty() || department.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(ManageEmployeesPageAdmin.this,
                            "Enter all fields.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                addEmployee(fullName, gender, age, salary, department);

                employeeModel = null;
                 sql = "SELECT * FROM employees";
                updatingDatabase(sql);
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = employeesTable.getSelectedRow();
                int id = Integer.parseInt(employeesTable.getModel().getValueAt(selectedRow, 0).toString());

                deleteEmployee(id);
                resetTF();
                employeeModel = null;
                 sql = "SELECT * FROM employees";
                updatingDatabase(sql);


            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String fullName = fullnameTF.getText();
                String gender = genderTF.getText();
                int age = Integer.parseInt(ageTF.getText());
                float salary = Float.parseFloat(salaryTF.getText());
                String department = departmentTF.getText();


                if (fullName.isEmpty() || gender.isEmpty()  || String.valueOf(age).isEmpty()
                        || String.valueOf(salary).isEmpty() || department.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(ManageEmployeesPageAdmin.this,
                            "Enter all fields.", "Try Again", JOptionPane.ERROR_MESSAGE);
                    return;
                }



                if (employeeModel != null) {

                    int selectedRow = employeesTable.getSelectedRow();
                    int id = Integer.parseInt(employeesTable.getModel().getValueAt(selectedRow, 0).toString());

                    updateEmployee(id,fullName, gender, age, salary, department);
                    sql = "SELECT * FROM employees";
                    updatingDatabase(sql);
                }
                resetTF();

            }
        });
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                resetTF();
                JOptionPane.showMessageDialog(ManageEmployeesPageAdmin.this, "Reseting successful.");

                employeesTable.clearSelection();
                employeeModel = null;
                sql = "SELECT * FROM employees";
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

        employeesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                employeeModel = (DefaultTableModel) employeesTable.getModel();

                int id = (int) employeeModel.getValueAt(employeesTable.getSelectedRow(), 0);
                String employee_fullName = (String) employeeModel.getValueAt(employeesTable.getSelectedRow(), 1);
                String employee_gender = (String) employeeModel.getValueAt(employeesTable.getSelectedRow(), 2);
                int employee_age = (int) employeeModel.getValueAt(employeesTable.getSelectedRow(), 3);
                float employee_salary = (Float) employeeModel.getValueAt(employeesTable.getSelectedRow(), 4);
                String employee_department = (String) employeeModel.getValueAt(employeesTable.getSelectedRow(), 5);

                idTF.setText(String.valueOf(id));
                fullnameTF.setText(employee_fullName);
                genderTF.setText(employee_gender);
                ageTF.setText(String.valueOf(employee_age));
                salaryTF.setText(String.valueOf(employee_salary));
                departmentTF.setText(employee_department);


            }
        });
    }


    private void searchEmployee(String fullName)  {

        try {
            sql = "SELECT * FROM employees WHERE employee_fullName LIKE '" + fullName + "%'";
            updatingDatabase(sql);

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void resetTF() {
        idTF.setText("");
        fullnameTF.setText("");
        genderTF.setText("");
        ageTF.setText("");
        salaryTF.setText("");
        departmentTF.setText("");
        searchTF.setText("");
    }

    private void addEmployee(String fullName, String gender, int age,float salary,String department) {
        try {

            mStatement = mConnection.createStatement();
            sql = "INSERT INTO employees(employee_fullName,employee_gender,employee_age,employee_salary" +
                    ",employee_department) VALUES(?,?,?,?,?)";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, fullName);
            mPreparedStatement.setString(2, gender);
            mPreparedStatement.setInt(3, age);
            mPreparedStatement.setFloat(4, salary);
            mPreparedStatement.setString(5, department);

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageEmployeesPageAdmin.this, "Adding successful.");
            resetTF();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void deleteEmployee(int id) {
        try {
            mStatement = mConnection.createStatement();

            String sql = "delete from employees where id=?";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setInt(1, id);

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageEmployeesPageAdmin.this, "Deleting successful.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateEmployee(int id, String fullName, String gender, int age,float salary,String department) {
        try {
            mStatement = mConnection.createStatement();
            sql = "update employees set employee_fullName = ?, employee_gender=?, " +
                    "employee_age=?,employee_salary=?, employee_department=? WHERE id=?";

            mPreparedStatement = mConnection.prepareStatement(sql);

            mPreparedStatement.setString(1, fullName);
            mPreparedStatement.setString(2, gender);
            mPreparedStatement.setInt(3, age);
            mPreparedStatement.setFloat(4, salary);
            mPreparedStatement.setString(5, department);
            mPreparedStatement.setInt(6, id);

            mPreparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(ManageEmployeesPageAdmin.this, "Updating successful.");


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    private DefaultTableModel employeesDataModel(String sql) {
        Object[][] data = {};
        String[] columnNames = {"id", "employee_fullName", "employee_gender", "employee_age",
                "employee_salary", "employee_department"};
        DefaultTableModel employeeModel = new DefaultTableModel(data, columnNames);
        employeeModel.setColumnIdentifiers(columnNames);
        try {
            Statement statement = mConnection.createStatement();

            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                int id = result.getInt("id");
                String fullName = result.getString("employee_fullName");
                String gender = result.getString("employee_gender");
                int age = result.getInt("employee_age");
                float salary = result.getFloat("employee_salary");
                String department = result.getString("employee_department");

                Object[] rowData = {id, fullName, gender, age, salary, department};
                employeeModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employeeModel;
    }

    public void updatingDatabase(String sql) {
        try {
            mStatement = mConnection.createStatement();
            mResultSet = mStatement.executeQuery(sql);
            ResultSetMetaData stData = mResultSet.getMetaData();
            ColumnCount = stData.getColumnCount();
            RecordTable = (DefaultTableModel) employeesTable.getModel();
            RecordTable.setRowCount(0);
            employeesTable.setModel(employeesDataModel(sql));
            TableRowSorter<TableModel> tableSorter1 = new TableRowSorter<>(employeesTable.getModel());
            tableSorter1.setModel(employeesTable.getModel());
            employeesTable.setRowSorter(tableSorter1);

            while (mResultSet.next()) {
                Vector<String> columnData = new Vector<>();
                for (i = 1; i <= ColumnCount; i++) {
                    columnData.add(String.valueOf(mResultSet.getInt("id")));
                    columnData.add(mResultSet.getString("employee_fullName"));
                    columnData.add(mResultSet.getString("employee_gender"));
                    columnData.add(String.valueOf(mResultSet.getInt("employee_age")));
                    columnData.add(String.valueOf(mResultSet.getFloat("employee_salary")));
                    columnData.add(mResultSet.getString("employee_department"));
                }
                RecordTable.addRow(columnData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
