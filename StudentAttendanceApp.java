
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

// Student Class
class Student {
    String prn, name, branch;

    Student(String prn, String name, String branch) {
        this.prn = prn;
        this.name = name;
        this.branch = branch;
    }
}

// Attendance Class
class Attendance {
    String prn, date, status;

    Attendance(String prn, String date, String status) {
        this.prn = prn;
        this.date = date;
        this.status = status;
    }
}

// Main GUI Class
public class StudentAttendanceApp extends JFrame {

    ArrayList<Student> students = new ArrayList<>();
    ArrayList<Attendance> records = new ArrayList<>();

    DefaultTableModel studentModel, attendanceModel;

    Color lavender = new Color(230, 220, 245);
    Color pink = new Color(250, 220, 230);
    Color mint = new Color(215, 240, 225);
    Color cream = new Color(255, 248, 235);

    StudentAttendanceApp() {

        setTitle("Student Attendance Management System");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Dashboard", dashboard());
        tabs.add("Add Student", addStudent());
        tabs.add("Mark Attendance", markAttendance());
        tabs.add("Records", recordsPanel());
        tabs.add("Summary", summaryPanel());

        add(tabs);
        getContentPane().setBackground(cream);
        setVisible(true);
    }

    // Dashboard
    JPanel dashboard() {

        JPanel p = new JPanel(new GridLayout(3, 1, 15, 15));
        p.setBackground(cream);

        JLabel title = new JLabel(
            "STUDENT ATTENDANCE SYSTEM",
            SwingConstants.CENTER
        );

        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(new Color(90, 75, 120));

        JLabel name = new JLabel(
            "Developed by: Tanvi Sevak | PRN: 20250802305",
            SwingConstants.CENTER
        );

        JLabel date = new JLabel(
            "Date: " + LocalDate.now(),
            SwingConstants.CENTER
        );

        p.add(title);
        p.add(name);
        p.add(date);

        return p;
    }

    // Add Student
    JPanel addStudent() {

        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.setBackground(lavender);
        p.setBorder(BorderFactory.createEmptyBorder(40, 70, 40, 70));

        JTextField prn = new JTextField();
        JTextField name = new JTextField();
        JTextField branch = new JTextField();

        JButton add = new JButton("Add Student");

        p.add(new JLabel("PRN:"));
        p.add(prn);

        p.add(new JLabel("Name:"));
        p.add(name);

        p.add(new JLabel("Branch:"));
        p.add(branch);

        p.add(new JLabel(""));
        p.add(add);

        add.addActionListener(e -> {

            if (prn.getText().isEmpty() ||
                name.getText().isEmpty() ||
                branch.getText().isEmpty()) {

                JOptionPane.showMessageDialog(p, "Fill all details!");
                return;
            }

            for (Student s : students) {
                if (s.prn.equals(prn.getText())) {
                    JOptionPane.showMessageDialog(p, "PRN already exists!");
                    return;
                }
            }

            Student s = new Student(
                prn.getText(),
                name.getText(),
                branch.getText()
            );

            students.add(s);

            studentModel.addRow(
                new Object[]{s.prn, s.name, s.branch}
            );

            saveData();

            JOptionPane.showMessageDialog(p, "Student Added!");

            prn.setText("");
            name.setText("");
            branch.setText("");
        });

        return p;
    }

    // Mark Attendance
    JPanel markAttendance() {

        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.setBackground(pink);
        p.setBorder(BorderFactory.createEmptyBorder(50, 70, 50, 70));

        JTextField prn = new JTextField();
        JTextField date = new JTextField(LocalDate.now().toString());

        JComboBox<String> status = new JComboBox<>(
            new String[]{"Present", "Absent"}
        );

        JButton mark = new JButton("Save Attendance");

        p.add(new JLabel("Student PRN:"));
        p.add(prn);

        p.add(new JLabel("Date:"));
        p.add(date);

        p.add(new JLabel("Status:"));
        p.add(status);

        p.add(new JLabel(""));
        p.add(mark);

        mark.addActionListener(e -> {

            Student found = null;

            for (Student s : students) {
                if (s.prn.equals(prn.getText())) {
                    found = s;
                    break;
                }
            }

            if (found == null) {
                JOptionPane.showMessageDialog(p, "Student not found!");
                return;
            }

            try {
                LocalDate.parse(date.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Enter date as YYYY-MM-DD!");
                return;
            }

            String selectedStatus = (String) status.getSelectedItem();

            for (int i = 0; i < records.size(); i++) {
                Attendance a = records.get(i);

                if (a.prn.equals(prn.getText()) &&
                    a.date.equals(date.getText())) {

                    a.status = selectedStatus;
                    attendanceModel.setValueAt(selectedStatus, i, 3);
                    saveData();

                    JOptionPane.showMessageDialog(p, "Attendance Updated!");
                    return;
                }
            }

            Attendance a = new Attendance(
                prn.getText(),
                date.getText(),
                selectedStatus
            );

            records.add(a);

            attendanceModel.addRow(new Object[]{
                a.prn, found.name, a.date, a.status
            });

            saveData();

            JOptionPane.showMessageDialog(p, "Attendance Saved!");
        });

        return p;
    }

    // Attendance Records
    JPanel recordsPanel() {

        JPanel p = new JPanel(new BorderLayout());

        attendanceModel = new DefaultTableModel(
            new String[]{"PRN", "Name", "Date", "Status"}, 0
        );

        JTable table = new JTable(attendanceModel);
        table.setRowHeight(25);

        p.add(new JScrollPane(table));

        return p;
    }

    // Attendance Summary
    JPanel summaryPanel() {

        JPanel p = new JPanel(new BorderLayout());

        JButton show = new JButton("Generate Summary");

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"PRN", "Name", "Present", "Absent"}, 0
        );

        JTable table = new JTable(model);

        show.addActionListener(e -> {

            model.setRowCount(0);

            for (Student s : students) {

                int present = 0, absent = 0;

                for (Attendance a : records) {

                    if (a.prn.equals(s.prn)) {

                        if (a.status.equals("Present"))
                            present++;
                        else
                            absent++;
                    }
                }

                model.addRow(new Object[]{
                    s.prn, s.name, present, absent
                });
            }
        });

        p.add(show, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        return p;
    }

    // Save Data
    void saveData() {

        try {
            PrintWriter out = new PrintWriter("attendance.txt");

            for (Attendance a : records) {
                out.println(
                    a.prn + "," + a.date + "," + a.status
                );
            }

            out.close();

        } catch (Exception e) {
            System.out.println("File Error!");
        }
    }

    public static void main(String[] args) {
        new StudentAttendanceApp();
    }
}
