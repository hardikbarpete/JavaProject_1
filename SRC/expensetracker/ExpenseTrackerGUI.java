package expensetracker;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Main GUI window for the Personal Expense Tracker.
 * Built using Java Swing with a clean, tabbed interface.
 */
public class ExpenseTrackerGUI extends JFrame {

    private final ExpenseManager manager = new ExpenseManager();

    // --- Input fields ---
    private JTextField amountField, descField;
    private JComboBox<String> categoryCombo;
    private JSpinner dateSpinner;

    // --- Table ---
    private DefaultTableModel tableModel;
    private JTable expenseTable;

    // --- Summary labels ---
    private JLabel totalLabel, topCategoryLabel, countLabel;

    public ExpenseTrackerGUI() {
        setTitle("Personal Expense Tracker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        // App-wide font & color
        UIManager.put("Panel.background", new Color(245, 247, 250));
        UIManager.put("Button.background", new Color(70, 130, 180));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 13));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.addTab("📋  Expenses", buildExpenseTab());
        tabs.addTab("📊  Summary", buildSummaryTab());

        add(tabs);
        setVisible(true);
        loadSampleData();
    }

    // -----------------------------------------------------------------------
    //  TAB 1 — Expense Entry & Table
    // -----------------------------------------------------------------------
    private JPanel buildExpenseTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        panel.setBackground(new Color(245, 247, 250));

        // --- Input form ---
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1),
                "  Add New Expense  ",
                0, 0, new Font("SansSerif", Font.BOLD, 12), new Color(70, 130, 180)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Amount
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        form.add(label("Amount (Rs):"), gc);
        gc.gridx = 1; gc.weightx = 1;
        amountField = new JTextField(10);
        form.add(amountField, gc);

        // Category
        gc.gridx = 2; gc.weightx = 0;
        form.add(label("Category:"), gc);
        gc.gridx = 3; gc.weightx = 1;
        categoryCombo = new JComboBox<>(ExpenseManager.CATEGORIES);
        form.add(categoryCombo, gc);

        // Description
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        form.add(label("Description:"), gc);
        gc.gridx = 1; gc.weightx = 1;
        descField = new JTextField(20);
        form.add(descField, gc);

        // Date
        gc.gridx = 2; gc.weightx = 0;
        form.add(label("Date:"), gc);
        gc.gridx = 3; gc.weightx = 1;
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        form.add(dateSpinner, gc);

        // Buttons
        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 4;
        gc.fill = GridBagConstraints.NONE; gc.anchor = GridBagConstraints.CENTER;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        JButton addBtn = styledButton("➕  Add Expense", new Color(46, 139, 87));
        JButton removeBtn = styledButton("🗑  Remove Selected", new Color(180, 60, 60));
        btnRow.add(addBtn); btnRow.add(removeBtn);
        form.add(btnRow, gc);

        panel.add(form, BorderLayout.NORTH);

        // --- Table ---
        String[] cols = {"Date", "Category", "Description", "Amount (Rs)"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        expenseTable = new JTable(tableModel);
        expenseTable.setRowHeight(26);
        expenseTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        expenseTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        expenseTable.getTableHeader().setBackground(new Color(70, 130, 180));
        expenseTable.getTableHeader().setForeground(Color.WHITE);
        expenseTable.setSelectionBackground(new Color(173, 216, 230));
        expenseTable.setGridColor(new Color(220, 225, 230));
        expenseTable.getColumnModel().getColumn(3).setCellRenderer(rightAlign());

        JScrollPane scroll = new JScrollPane(expenseTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220)));
        panel.add(scroll, BorderLayout.CENTER);

        // --- Bottom total bar ---
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        totalLabel = new JLabel("Total: Rs 0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        totalLabel.setForeground(new Color(30, 80, 140));
        bottom.add(totalLabel);
        panel.add(bottom, BorderLayout.SOUTH);

        // --- Button actions ---
        addBtn.addActionListener(e -> addExpense());
        removeBtn.addActionListener(e -> removeExpense());

        return panel;
    }

    // -----------------------------------------------------------------------
    //  TAB 2 — Summary / Analytics
    // -----------------------------------------------------------------------
    private JPanel buildSummaryTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        panel.setBackground(new Color(245, 247, 250));

        // Stats cards row
        JPanel cards = new JPanel(new GridLayout(1, 3, 14, 0));
        cards.setOpaque(false);

        countLabel = new JLabel("0", SwingConstants.CENTER);
        topCategoryLabel = new JLabel("N/A", SwingConstants.CENTER);
        JLabel totalCard = new JLabel("Rs 0.00", SwingConstants.CENTER);
        totalCard.setName("totalCard");

        cards.add(statCard("Total Expenses", countLabel, new Color(70, 130, 180)));
        cards.add(statCard("Top Category", topCategoryLabel, new Color(46, 139, 87)));
        cards.add(statCard("Total Spent", totalCard, new Color(180, 100, 0)));
        panel.add(cards, BorderLayout.NORTH);

        // Category breakdown table
        String[] cols = {"Category", "Total (Rs)", "# Expenses"};
        DefaultTableModel catModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable catTable = new JTable(catModel);
        catTable.setRowHeight(28);
        catTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        catTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        catTable.getTableHeader().setBackground(new Color(46, 139, 87));
        catTable.getTableHeader().setForeground(Color.WHITE);
        catTable.getColumnModel().getColumn(1).setCellRenderer(rightAlign());
        catTable.getColumnModel().getColumn(2).setCellRenderer(rightAlign());
        catTable.setGridColor(new Color(220, 225, 230));

        JScrollPane scroll = new JScrollPane(catTable);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220)),
                "  Spending by Category  ",
                0, 0, new Font("SansSerif", Font.BOLD, 12), new Color(46, 139, 87)));
        panel.add(scroll, BorderLayout.CENTER);

        // Refresh button
        JButton refreshBtn = styledButton("🔄  Refresh Summary", new Color(70, 130, 180));
        refreshBtn.addActionListener(e -> {
            catModel.setRowCount(0);
            HashMap<String, Double> byCategory = manager.getTotalByCategory();

            // Count per category using another HashMap
            HashMap<String, Integer> countMap = new HashMap<>();
            for (var exp : manager.getAllExpenses()) {
                countMap.merge(exp.getCategory(), 1, Integer::sum);
            }

            // Sort categories by total descending
            byCategory.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .forEach(entry -> {
                        catModel.addRow(new Object[]{
                            entry.getKey(),
                            String.format("%.2f", entry.getValue()),
                            countMap.getOrDefault(entry.getKey(), 0)
                        });
                    });

            // Update stat cards
            totalCard.setText(String.format("Rs %.2f", manager.getTotal()));
            countLabel.setText(String.valueOf(manager.size()));
            topCategoryLabel.setText(manager.getTopCategory());
        });

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setOpaque(false);
        south.add(refreshBtn);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    // -----------------------------------------------------------------------
    //  Actions
    // -----------------------------------------------------------------------
    private void addExpense() {
        try {
            String amtText = amountField.getText().trim();
            String desc = descField.getText().trim();
            if (amtText.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double amount = Double.parseDouble(amtText);
            if (amount <= 0) throw new NumberFormatException();

            String category = (String) categoryCombo.getSelectedItem();
            java.util.Date d = (java.util.Date) dateSpinner.getValue();
            LocalDate date = d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            manager.addExpense(amount, category, desc, date);
            tableModel.addRow(new Object[]{date, category, desc, String.format("%.2f", amount)});

            totalLabel.setText(String.format("Total: Rs %.2f", manager.getTotal()));
            amountField.setText(""); descField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid positive amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeExpense() {
        int row = expenseTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to remove.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        manager.removeExpense(row);
        tableModel.removeRow(row);
        totalLabel.setText(String.format("Total: Rs %.2f", manager.getTotal()));
    }

    // -----------------------------------------------------------------------
    //  Sample Data
    // -----------------------------------------------------------------------
    private void loadSampleData() {
        Object[][] samples = {
            {450.0, "Food", "Grocery shopping", LocalDate.now().minusDays(2)},
            {1200.0, "Transport", "Monthly bus pass", LocalDate.now().minusDays(5)},
            {299.0, "Entertainment", "Movie ticket", LocalDate.now().minusDays(1)},
            {3500.0, "Healthcare", "Doctor visit & medicines", LocalDate.now().minusDays(10)},
            {850.0, "Shopping", "Stationery & supplies", LocalDate.now().minusDays(3)},
        };
        for (Object[] row : samples) {
            manager.addExpense((double) row[0], (String) row[1], (String) row[2], (LocalDate) row[3]);
            tableModel.addRow(new Object[]{row[3], row[1], row[2], String.format("%.2f", row[0])});
        }
        totalLabel.setText(String.format("Total: Rs %.2f", manager.getTotal()));
    }

    // -----------------------------------------------------------------------
    //  UI helpers
    // -----------------------------------------------------------------------
    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        return l;
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel statCard(String title, JLabel value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 2),
                BorderFactory.createEmptyBorder(14, 10, 14, 10)));
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("SansSerif", Font.BOLD, 12));
        t.setForeground(accent);
        value.setFont(new Font("SansSerif", Font.BOLD, 22));
        value.setForeground(new Color(40, 40, 40));
        card.add(t, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        return card;
    }

    private DefaultTableCellRenderer rightAlign() {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(SwingConstants.RIGHT);
        return r;
    }

    // -----------------------------------------------------------------------
    //  Entry Point
    // -----------------------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new ExpenseTrackerGUI();
        });
    }
}
