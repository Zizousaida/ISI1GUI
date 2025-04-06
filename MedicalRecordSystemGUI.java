import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class MedicalRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String patientName;
    private Date dateOfBirth;
    private String diagnosis;

    public MedicalRecord(String patientName, Date dateOfBirth, String diagnosis) {
        this.patientName = patientName;
        this.dateOfBirth = dateOfBirth;
        this.diagnosis = diagnosis;
    }

    public String getPatientName() {
        return patientName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return "Patient: " + patientName + 
               ", Date of Birth: " + sdf.format(dateOfBirth) + 
               ", Diagnosis: " + diagnosis;
    }
}

public class MedicalRecordSystemGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private ArrayList<MedicalRecord> records = new ArrayList<>();
    private JTable recordsTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, dobField, diagnosisField;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    public MedicalRecordSystemGUI() {
        setTitle("Medical Records Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Medical Record"));
        
        formPanel.add(new JLabel("Patient Name:"));
        nameField = new JTextField(20);
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Date of Birth (dd/MM/yyyy):"));
        dobField = new JTextField(10);
        formPanel.add(dobField);
        
        formPanel.add(new JLabel("Diagnosis:"));
        diagnosisField = new JTextField(30);
        formPanel.add(diagnosisField);
        
        JButton addButton = new JButton("Add Record");
        addButton.addActionListener(e -> addRecord());
        formPanel.add(addButton);
        
        JButton clearButton = new JButton("Clear Fields");
        clearButton.addActionListener(e -> clearFields());
        formPanel.add(clearButton);
        
        // Table panel
        String[] columns = {"Patient Name", "Date of Birth", "Diagnosis"};
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recordsTable = new JTable(tableModel);
        recordsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(recordsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Medical Records"));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton deleteButton = new JButton("Delete Selected Record");
        deleteButton.addActionListener(e -> deleteRecord());
        buttonPanel.add(deleteButton);
        
        JButton saveButton = new JButton("Save Records");
        saveButton.addActionListener(e -> saveRecords());
        buttonPanel.add(saveButton);
        
        JButton loadButton = new JButton("Load Records");
        loadButton.addActionListener(e -> loadRecords());
        buttonPanel.add(loadButton);
        
        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private void addRecord() {
        String name = nameField.getText().trim();
        String dobString = dobField.getText().trim();
        String diagnosis = diagnosisField.getText().trim();
        
        if (name.isEmpty() || dobString.isEmpty() || diagnosis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Date dob = dateFormat.parse(dobString);
            
            MedicalRecord record = new MedicalRecord(name, dob, diagnosis);
            records.add(record);
            
            // Add to table
            tableModel.addRow(new Object[]{name, dobString, diagnosis});
            
            clearFields();
            JOptionPane.showMessageDialog(this, "Record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use dd/MM/yyyy format.", 
                                         "Date Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteRecord() {
        int selectedRow = recordsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                            "Are you sure you want to delete this record?", 
                            "Confirm Deletion", 
                            JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                records.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a record to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void saveRecords() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Medical Records");
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String fileName = fileToSave.getAbsolutePath();
            if (!fileName.endsWith(".dat")) {
                fileName += ".dat";
            }
            
            try {
                FileOutputStream fileOut = new FileOutputStream(fileName);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(records);
                out.close();
                fileOut.close();
                JOptionPane.showMessageDialog(this, "Records saved successfully to " + fileName, 
                                             "Save Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), 
                                             "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadRecords() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Medical Records");
        
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            
            try {
                FileInputStream fileIn = new FileInputStream(fileToLoad);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                records = (ArrayList<MedicalRecord>) in.readObject();
                in.close();
                fileIn.close();
                
                // Update table
                updateTable();
                
                JOptionPane.showMessageDialog(this, "Records loaded successfully!", 
                                             "Load Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(), 
                                             "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateTable() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Add all records to table
        for (MedicalRecord record : records) {
            tableModel.addRow(new Object[]{
                record.getPatientName(), 
                dateFormat.format(record.getDateOfBirth()), 
                record.getDiagnosis()
            });
        }
    }
    
    private void clearFields() {
        nameField.setText("");
        dobField.setText("");
        diagnosisField.setText("");
        nameField.requestFocus();
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            MedicalRecordSystemGUI app = new MedicalRecordSystemGUI();
            app.setVisible(true);
        });
    }
}
