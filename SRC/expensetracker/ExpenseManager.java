package expensetracker;

import java.util.*;
import java.time.LocalDate;

/**
 * Manages all expenses using ArrayList and HashMap.
 * Demonstrates core Collections & Data Structures concepts.
 */
public class ExpenseManager {

    // Primary data structure: ArrayList to store all expenses in insertion order
    private final ArrayList<Expense> expenses = new ArrayList<>();

    // Categories available for selection
    public static final String[] CATEGORIES = {
        "Food", "Transport", "Rent", "Entertainment",
        "Healthcare", "Shopping", "Education", "Other"
    };

    /** Add a new expense to the list */
    public void addExpense(double amount, String category, String description, LocalDate date) {
        expenses.add(new Expense(amount, category, description, date));
    }

    /** Remove expense at a given index */
    public void removeExpense(int index) {
        expenses.remove(index);
    }

    /** Return all expenses as an unmodifiable list */
    public List<Expense> getAllExpenses() {
        return Collections.unmodifiableList(expenses);
    }

    /** Total of all expenses */
    public double getTotal() {
        double total = 0;
        for (Expense e : expenses) total += e.getAmount();
        return total;
    }

    /**
     * Returns a HashMap mapping each category to its total spending.
     * Demonstrates HashMap usage from Collections framework.
     */
    public HashMap<String, Double> getTotalByCategory() {
        HashMap<String, Double> map = new HashMap<>();
        for (Expense e : expenses) {
            map.merge(e.getCategory(), e.getAmount(), Double::sum);
        }
        return map;
    }

    /**
     * Returns expenses filtered by category.
     * Demonstrates ArrayList iteration and filtering.
     */
    public List<Expense> getByCategory(String category) {
        List<Expense> result = new ArrayList<>();
        for (Expense e : expenses) {
            if (e.getCategory().equalsIgnoreCase(category)) result.add(e);
        }
        return result;
    }

    /**
     * Returns the category with highest spending using Collections methods.
     */
    public String getTopCategory() {
        HashMap<String, Double> map = getTotalByCategory();
        if (map.isEmpty()) return "N/A";
        return Collections.max(map.entrySet(),
                Map.Entry.comparingByValue()).getKey();
    }

    /** Returns expenses sorted by amount descending */
    public List<Expense> getSortedByAmount() {
        ArrayList<Expense> sorted = new ArrayList<>(expenses);
        sorted.sort((a, b) -> Double.compare(b.getAmount(), a.getAmount()));
        return sorted;
    }

    public int size() { return expenses.size(); }
}
