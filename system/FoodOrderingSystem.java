package system;

import model.*;
import exception.InvalidPaymentMethodException;

import java.sql.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FoodOrderingSystem {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS users (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255))";
    private static final String INSERT_USER_QUERY = "INSERT INTO users (name) VALUES (?)";
    private static final String SELECT_USERS_QUERY = "SELECT * FROM users";

    private static Scanner scanner = new Scanner(System.in);
   private static final String ORDER_FILE_PATH = "order_details.txt";


    private static Order currentOrder; 

    public static void main(String[] args) {
        createTable();
        loadUsersFromDatabase();

        System.out.println("Welcome to the food ordering system!");

        while (true) {
            printMenu();

            System.out.println("Please select an item from the menu:");

            int itemSelection = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (itemSelection < 1 || itemSelection > 4) {
                System.out.println("Invalid selection. Please try again.");
            } else {
                FoodItem selectedItem = getMenu(itemSelection);

                System.out.println("Please enter the quantity of the item:");

                int quantity = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                Order order = new Order(selectedItem, quantity);

                addToCart(order);
            }

            System.out.println("Do you want to view your current order? (Y/N)");

            String response = scanner.next().trim();

            if (response.equalsIgnoreCase("Y")) {
                viewOrder();
            }

            System.out.println("Do you want to remove items from your order? (Y/N)");

            response = scanner.next().trim();

            if (response.equalsIgnoreCase("Y")) {
                removeItemFromOrder();
            }

            System.out.println("Do you want to place another order? (Y/N)");

            response = scanner.next().trim();

            if (!response.equalsIgnoreCase("Y")) {
                break;
            }
        }

        try {
            checkout();
            // Save order details to a file
            saveOrderToFile();
        } catch (InvalidPaymentMethodException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Thank you for your order!");
    }

    private static void createTable() {
        String DB_USERNAME = "root";
        String DB_PASSWORD = "#Shashank9672";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TABLE_QUERY);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadUsersFromDatabase() {
    }

    private static void printMenu() {
        System.out.println("Menu:");
        System.out.println("1. Burger - $5.99");
        System.out.println("2. Pizza - $8.99");
        System.out.println("3. Salad - $3.99");
        System.out.println("4. Soda - $1.99");
    }

    private static FoodItem getMenu(int itemSelection) {
        switch (itemSelection) {
            case 1:
                return new FoodItem("Burger", 5.99);
            case 2:
                return new FoodItem("Pizza", 8.99);
            case 3:
                return new FoodItem("Salad", 3.99);
            case 4:
                return new FoodItem("Soda", 1.99);
            default:
                throw new IllegalArgumentException("Invalid menu item selection");
        }
    }

    private static void addToCart(Order order) {
        // Assuming you still want to prompt the user for their name and add it to the database
        System.out.println("Please enter your name:");
        String userName = scanner.nextLine();

        try (Connection connection = DriverManager.getConnection(DB_URL, "root", "#Shashank9672");
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_QUERY)) {
            preparedStatement.setString(1, userName);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set the currentOrder to the newly added order
        currentOrder = order;

        System.out.println("Item added to the cart!");
    }

    private static void viewOrder() {
        if (currentOrder == null || currentOrder.getQuantity() == 0) {
            System.out.println("Your order is empty.");
        } else {
            System.out.println("Your current order:");
            System.out.println(currentOrder.getQuantity() + "x " + currentOrder.getItem().getName() +
                    " - $" + currentOrder.getItem().getPrice());
        }
    }

    private static void removeItemFromOrder() {
        if (currentOrder == null || currentOrder.getQuantity() == 0) {
            System.out.println("Your order is empty. Nothing to remove.");
        } else {
            System.out.println("Select the item number to remove:");
            System.out.println(currentOrder.getQuantity() + "x " + currentOrder.getItem().getName() +
                    " - $" + currentOrder.getItem().getPrice());

            // Reset the currentOrder after removing the item
            currentOrder = null;

            System.out.println("Item removed from your order.");
        }
    }

    private static void checkout() throws InvalidPaymentMethodException {
        if (currentOrder == null || currentOrder.getQuantity() == 0) {
            System.out.println("Your order is empty. Cannot proceed to checkout.");
            return;
        }

        System.out.println("Order summary:");
        double totalAmount = currentOrder.getQuantity() * currentOrder.getItem().getPrice();
        System.out.println(currentOrder.getQuantity() + "x " + currentOrder.getItem().getName() +
                " - $" + currentOrder.getItem().getPrice());
        System.out.println("Total: $" + totalAmount);

        System.out.println("Payment methods: credit card, debit card, cash");
        System.out.println("Please enter your payment method:");
        String paymentMethod = scanner.next().trim();

        if (!isValidPaymentMethod(paymentMethod)) {
            throw new InvalidPaymentMethodException("Invalid payment method. Only credit card, debit card, or cash are accepted.");
        }

        System.out.println("Payment successful. Thank you for your order!");
        currentOrder = null;  
    }

   
private static void saveOrderToFile() {
    if (currentOrder == null || currentOrder.getQuantity() == 0) {
        System.out.println("No order details to save.");
        return;
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(ORDER_FILE_PATH))) {
        writer.write("Order details:\n");
        writer.write(currentOrder.getQuantity() + "x " + currentOrder.getItem().getName() +
                " - $" + currentOrder.getItem().getPrice() + "\n");
        writer.write("Total: $" + currentOrder.getItem().getPrice() * currentOrder.getQuantity() + "\n");

        System.out.println("Order details saved to: " + ORDER_FILE_PATH);
    } catch (IOException e) {
        e.printStackTrace();
    }
}



    private static boolean isValidPaymentMethod(String paymentMethod) {
        return paymentMethod.equalsIgnoreCase("credit") || paymentMethod.equalsIgnoreCase("debit") || paymentMethod.equalsIgnoreCase("cash");
    }
}
