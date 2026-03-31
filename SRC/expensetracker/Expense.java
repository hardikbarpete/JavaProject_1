package expensetracker;

import java.time.LocalDate;

/**
 * Represents a single expense entry.
 * Encapsulates amount, category, description, and date.
 */
public class Expense {
    private double amount;
    private String category;
    private String description;
    private LocalDate date;

    public Expense(double amount, String category, String description, LocalDate date) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public double getAmount()       { return amount; }
    public String getCategory()     { return category; }
    public String getDescription()  { return description; }
    public LocalDate getDate()      { return date; }

    @Override
    public String toString() {
        return String.format("%s | %-15s | %-25s | Rs %.2f",
                date, category, description, amount);
    }
}
