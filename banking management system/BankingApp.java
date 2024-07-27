import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

// Account class to hold account details and handle account operations
class Account {
    private static int accountNumberCounter = 1000; // Static counter to generate unique account numbers
    private int accountNumber;
    private String accountHolderName;
    private String accountType;
    private double balance;

    // Constructor for creating a new account with an initial deposit
    public Account(String accountHolderName, String accountType, double initialDeposit) {
        this.accountNumber = ++accountNumberCounter;
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.balance = initialDeposit;
    }

    // Constructor for creating an account object from existing data
    public Account(int accountNumber, String accountHolderName, String accountType, double balance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.balance = balance;
    }

    // Getters for account details
    public int getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getAccountType() {
        return accountType;
    }

    public double getBalance() {
        return balance;
    }

    // Method to deposit money into the account
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println(amount + " deposited successfully.");
        } else {
            System.out.println("Invalid amount. Deposit failed.");
        }
    }

    // Method to withdraw money from the account
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println(amount + " withdrawn successfully.");
            return true;
        } else {
            System.out.println("Insufficient balance or invalid amount. Withdrawal failed.");
            return false;
        }
    }

    // Method to display account details
    @Override
    public String toString() {
        return "Account Number: " + accountNumber +
                ", Holder Name: " + accountHolderName +
                ", Type: " + accountType +
                ", Balance: " + balance;
    }

    // Method to convert account details to a string for file storage
    public String toFileString() {
        return accountNumber + "," + accountHolderName + "," + accountType + "," + balance;
    }

    // Static method to create an account object from a file string
    public static Account fromFileString(String fileString) {
        String[] parts = fileString.split(",");
        int accountNumber = Integer.parseInt(parts[0]);
        String accountHolderName = parts[1];
        String accountType = parts[2];
        double balance = Double.parseDouble(parts[3]);
        return new Account(accountNumber, accountHolderName, accountType, balance);
    }

    // Static methods to get and set the account number counter
    public static int getAccountNumberCounter() {
        return accountNumberCounter;
    }

    public static void setAccountNumberCounter(int counter) {
        accountNumberCounter = counter;
    }
}

// Transaction class to hold transaction details
class Transaction {
    private LocalDateTime transactionDateTime;
    private int fromAccountNumber;
    private int toAccountNumber;
    private double amount;

    // Constructor to create a new transaction
    public Transaction(int fromAccountNumber, int toAccountNumber, double amount) {
        this.transactionDateTime = LocalDateTime.now();
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
    }

    // Method to display transaction details
    @Override
    public String toString() {
        return "Transaction Date/Time: " + transactionDateTime +
                ", From Account: " + fromAccountNumber +
                ", To Account: " + toAccountNumber +
                ", Amount: " + amount;
    }

    // Method to convert transaction details to a string for file storage
    public String toFileString() {
        return transactionDateTime + "," + fromAccountNumber + "," + toAccountNumber + "," + amount;
    }

    // Static method to create a transaction object from a file string
    public static Transaction fromFileString(String fileString) {
        String[] parts = fileString.split(",");
        LocalDateTime transactionDateTime = LocalDateTime.parse(parts[0]);
        int fromAccountNumber = Integer.parseInt(parts[1]);
        int toAccountNumber = Integer.parseInt(parts[2]);
        double amount = Double.parseDouble(parts[3]);
        return new Transaction(fromAccountNumber, toAccountNumber, amount);
    }
}

// Bank class to handle bank operations
class Bank {
    private static final String ACCOUNTS_FILE = "accounts.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";

    private List<Account> accounts;
    private List<Transaction> transactions;

    // Constructor to initialize bank and load existing data
    public Bank() {
        accounts = new ArrayList<>();
        transactions = new ArrayList<>();
        loadAccounts();
        loadTransactions();
    }

    // Method to create a new account
    public Account createAccount(String accountHolderName, String accountType, double initialDeposit) {
        // Validate account type
        if (!isValidAccountType(accountType)) {
            System.out.println("Invalid account type. Account type must be 'saving' or 'current'.");
            return null;
        }
        if (initialDeposit <= 0) {
            System.out.println("Invalid initial deposit amount. Initial deposit cannot be zero or negative.");
            return null;
        }
    

        Account newAccount = new Account(accountHolderName, accountType, initialDeposit);
        accounts.add(newAccount);
        System.out.println("Account created successfully. Account Number: " + newAccount.getAccountNumber());
        saveAccounts();
        return newAccount;
    }

    // Helper method to validate account type
    private boolean isValidAccountType(String accountType) {
        return accountType.equalsIgnoreCase("saving") || accountType.equalsIgnoreCase("current");
    }


    // Method to find an account by account number
    public Account findAccount(int accountNumber) {
        for (Account account : accounts) {
            if (account.getAccountNumber() == accountNumber) {
                return account;
            }
        }
        return null;
    }

    // Method to delete an account by account number
    public boolean deleteAccount(int accountNumber) {
        Account accountToDelete = findAccount(accountNumber);
        if (accountToDelete != null) {
            accounts.remove(accountToDelete);
            saveAccounts(); // Update accounts.txt after removal
            return true;
        } else {
            System.out.println("Account not found. Deletion failed.");
            return false;
        }
    }

    // Method to display all accounts
    public void displayAllAccounts() {
        for (Account account : accounts) {
            System.out.println(account);
        }
    }

    // Method to deposit money into an account
    public boolean deposit(int accountNumber, double amount) {
        Account account = findAccount(accountNumber);
        if (account != null) {
            account.deposit(amount);
            transactions.add(new Transaction(0, accountNumber, amount));
            saveAccounts();
            saveTransactions();
            return true;
        } else {
            System.out.println("Account not found. Deposit failed.");
            return false;
        }
    }

    // Method to withdraw money from an account
    public boolean withdraw(int accountNumber, double amount) {
        Account account = findAccount(accountNumber);
        if (account != null) {
            if (account.withdraw(amount)) {
                transactions.add(new Transaction(accountNumber, 0, amount));
                saveAccounts();
                saveTransactions();
                return true;
            } else {
                System.out.println("Withdrawal failed.");
                return false;
            }
        } else {
            System.out.println("Account not found. Withdrawal failed.");
            return false;
        }
    }

    // Method to display all transactions
    public void displayAllTransactions() {
        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    // Method to check the balance of an account
    public double checkBalance(int accountNumber) {
        Account account = findAccount(accountNumber);
        if (account != null) {
            return account.getBalance();
        } else {
            System.out.println("Account not found.");
            return -1; // Return -1 to indicate account not found
        }
    }

    // Method to save accounts to a file
    private void saveAccounts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE))) {
            for (Account account : accounts) {
                writer.write(account.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    // Method to load accounts from a file
    private void loadAccounts() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Account account = Account.fromFileString(line);
                accounts.add(account);
                if (account.getAccountNumber() > Account.getAccountNumberCounter()) {
                    Account.setAccountNumberCounter(account.getAccountNumber());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }

    // Method to save transactions to a file
    private void saveTransactions() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Transaction transaction : transactions) {
                writer.write(transaction.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    // Method to load transactions from a file
    private void loadTransactions() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Transaction transaction = Transaction.fromFileString(line);
                transactions.add(transaction);
            }
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }
   
}

// BankingApp class with main method to run the application
public class BankingApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Bank bank = new Bank();

        while (true) {
            System.out.println("\n--- Banking System Menu ---");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Check Balance");
            System.out.println("5. Display All Accounts");
            System.out.println("6. Display All Transactions");
            System.out.println("7. Transfer Money");
            System.out.println("8. Delete Account");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Account Holder Name: ");
                    String accountHolderName = scanner.nextLine();
                    System.out.print("Enter Account Type (Saving/Current): ");
                    String accountType = scanner.nextLine();
                    System.out.print("Enter Initial Deposit Amount: ");
                    double initialDeposit = scanner.nextDouble();
                    bank.createAccount(accountHolderName, accountType, initialDeposit);
                    break;
                case 2:
                    System.out.print("Enter Account Number: ");
                    int depositAccountNumber = scanner.nextInt();
                    System.out.print("Enter Deposit Amount: ");
                    double depositAmount = scanner.nextDouble();
                    bank.deposit(depositAccountNumber, depositAmount);
                    break;
                case 3:
                    System.out.print("Enter Account Number: ");
                    int withdrawAccountNumber = scanner.nextInt();
                    System.out.print("Enter Withdrawal Amount: ");
                    double withdrawAmount = scanner.nextDouble();
                    bank.withdraw(withdrawAccountNumber, withdrawAmount);
                    break;
                case 4:
                    System.out.print("Enter Account Number: ");
                    int checkBalanceAccountNumber = scanner.nextInt();
                    double balance = bank.checkBalance(checkBalanceAccountNumber);
                    if (balance >= 0) {
                        System.out.println("Current Balance: " + balance);
                    }
                    break;
                case 5:
                    bank.displayAllAccounts();
                    break;
                case 6:
                    bank.displayAllTransactions();
                    break;
                case 7:
                    System.out.print("Enter Your Account Number: ");
                    int fromAccountNumber = scanner.nextInt();
                    System.out.print("Enter Recipient's Account Number: ");
                    int toAccountNumber = scanner.nextInt();
                    System.out.print("Enter Transfer Amount: ");
                    double transferAmount = scanner.nextDouble();
                    if (bank.withdraw(fromAccountNumber, transferAmount) && bank.deposit(toAccountNumber, transferAmount)) {
                        System.out.println("Transfer Successful.");
                    } else {
                        System.out.println("Transfer Failed.");
                    }
                    break;
                case 8:
                    System.out.print("Enter Account Number to delete: ");
                    int deleteAccountNumber = scanner.nextInt();
                    if (bank.deleteAccount(deleteAccountNumber)) {
                        System.out.println("Account deleted successfully.");
                    }
                    break;
                case 9:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
