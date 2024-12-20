import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class AttendanceManagementSystem extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField studentNameField;
    private JButton addStudentButton;
    private JButton markPresentButton;
    private JButton markAbsentButton;
    private JButton saveButton;

    public AttendanceManagementSystem() {
        setTitle("Attendance Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set modern font for the UI
        Font font = new Font("Arial", Font.PLAIN, 14);

        // Initialize table with columns
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Student Name");
        tableModel.addColumn("Date");
        tableModel.addColumn("Attendance Status");

        table = new JTable(tableModel);
        table.setFont(font);
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Attendance Records"));

        // Load students from file
        loadStudentNames();

        // Input for adding a new student name
        studentNameField = new JTextField(15);
        studentNameField.setFont(font);
        studentNameField.setToolTipText("Enter the name of the student");

        // Buttons
        addStudentButton = createStyledButton("Add Student");
        markPresentButton = createStyledButton("Mark Present");
        markAbsentButton = createStyledButton("Mark Absent");
        saveButton = createStyledButton("Save to File");

        // Panel for input and buttons
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Manage Attendance"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Student Name:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(studentNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(addStudentButton, gbc);
        gbc.gridx = 1;
        inputPanel.add(markPresentButton, gbc);
        gbc.gridx = 2;
        inputPanel.add(markAbsentButton, gbc);
        gbc.gridx = 3;
        inputPanel.add(saveButton, gbc);

        // Add listeners to buttons
        addStudentButton.addActionListener(new AddStudentAction());
        markPresentButton.addActionListener(new MarkAttendanceAction("Present"));
        markAbsentButton.addActionListener(new MarkAttendanceAction("Absent"));
        saveButton.addActionListener(new SaveAttendanceAction());

        // Layout
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(59, 89, 182));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEtchedBorder());
        button.setToolTipText(text);
        return button;
    }

    // Load student names from file and add them to the table
    private void loadStudentNames() {
        File file = new File("students.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String name;
            while ((name = reader.readLine()) != null) {
                Vector<Object> row = new Vector<>();
                row.add(name);
                row.add(""); // Date column empty initially
                row.add(""); // Attendance status column empty initially
                tableModel.addRow(row);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading student names: " + e.getMessage());
        }
    }

    // Save student names to file permanently
    private void saveStudentNames(String studentName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("students.txt", true))) {
            writer.write(studentName);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving student name: " + e.getMessage());
        }
    }

    // Action for adding a new student
    private class AddStudentAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String studentName = studentNameField.getText().trim();

            if (studentName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a student name.");
                return;
            }

            // Check if student already exists in the table
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(studentName)) {
                    JOptionPane.showMessageDialog(null, "Student already exists.");
                    return;
                }
            }

            // Add student to table and save permanently
            Vector<Object> row = new Vector<>();
            row.add(studentName);
            row.add(""); // Date column empty initially
            row.add(""); // Attendance status column empty initially
            tableModel.addRow(row);
            saveStudentNames(studentName);

            studentNameField.setText(""); // Clear input field
        }
    }

    // Action for marking attendance
    private class MarkAttendanceAction implements ActionListener {
        private String status;

        public MarkAttendanceAction(String status) {
            this.status = status;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(null, "Please select one or more students to mark attendance.");
                return;
            }

            for (int row : selectedRows) {
                tableModel.setValueAt(currentDate, row, 1); // Set current date
                tableModel.setValueAt(status, row, 2);      // Set status (Present/Absent)
            }
        }
    }

    // Action for saving attendance to file
    private class SaveAttendanceAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try (FileWriter writer = new FileWriter("attendance.csv")) {
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        writer.write(tableModel.getValueAt(i, j).toString() + ",");
                    }
                    writer.write("\n");
                }
                JOptionPane.showMessageDialog(null, "Attendance saved to attendance.csv");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error saving file: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AttendanceManagementSystem frame = new AttendanceManagementSystem();
            frame.setVisible(true);
        });
    }
}