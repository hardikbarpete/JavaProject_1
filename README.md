# 💰 Personal Expense Tracker

A Java Swing desktop application to track personal expenses, categorize them, and view spending summaries — built as a BYOP project for *Programming in Java*.

---

## Problem Statement

Managing day-to-day expenses is a challenge many students and working individuals face. Without a simple way to record and review spending, it's easy to overspend or lose track of where money is going. This application provides a lightweight, offline desktop solution to log expenses and analyze spending patterns at a glance.

---

## Features

- ✅ Add expenses with amount, category, description, and date
- 🗑️ Remove selected expense entries
- 📊 View category-wise spending breakdown
- 🏆 Instantly see which category you spend most on
- 💾 Pre-loaded with sample data so the app feels live on first run
- 🖥️ Clean, tabbed GUI built with Java Swing

---

## Java Concepts Used

| Concept | Where Used |
|---|---|
| `ArrayList` | Stores all expense records |
| `HashMap` | Maps categories to total spending |
| `Collections.max()` | Finds the top spending category |
| `List.sort()` / lambda | Sorts expenses by amount |
| `Collections.unmodifiableList()` | Encapsulates internal data |
| OOP (classes, encapsulation) | `Expense`, `ExpenseManager`, `ExpenseTrackerGUI` |
| Java Swing | Full GUI: JFrame, JTable, JTabbedPane, JSpinner |
| Exception Handling | Input validation for amount field |

---

## Project Structure

```
ExpenseTracker/
└── src/
    └── expensetracker/
        ├── Expense.java            # Data model
        ├── ExpenseManager.java     # Business logic + Collections
        └── ExpenseTrackerGUI.java  # Swing GUI + entry point (main())
```

---

## How to Set Up and Run

### Prerequisites
- Java JDK 11 or higher installed
- Any Java IDE (IntelliJ IDEA, Eclipse, NetBeans) **or** command line

### Option A — Using an IDE (Recommended)
1. Clone or download this repository
2. Open the `ExpenseTracker/` folder as a project in your IDE
3. Mark `src/` as the Sources Root
4. Run `ExpenseTrackerGUI.java` (it contains `main()`)

### Option B — Command Line

```bash
# Navigate to the project folder
cd ExpenseTracker

# Compile all source files
javac -d out src/expensetracker/*.java

# Run the application
java -cp out expensetracker.ExpenseTrackerGUI
```

---

## How to Use

### Tab 1 — Expenses
| Action | How |
|---|---|
| Add an expense | Fill in Amount, Category, Description, Date → click **➕ Add Expense** |
| Remove an expense | Click a row in the table → click **🗑 Remove Selected** |
| See running total | Check the bottom-right of the tab |

### Tab 2 — Summary
- Click **🔄 Refresh Summary** to see:
  - Total number of expenses logged
  - Your top spending category
  - Grand total amount spent
  - A full breakdown of spending by category

---

## Screenshots

> Run the app — it loads with sample data so you can explore all features immediately.

---

## Author

**[Lakshya Bansal]**  
Course: Programming in Java  
Submitted: March 2026
