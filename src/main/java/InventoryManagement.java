

import java.sql.*;
import java.util.Scanner;

public class InventoryManagement {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/retail_db";
        String username = "root";
        String password = "root";

        Scanner sc = new Scanner(System.in);

        try {
            Connection con = DriverManager.getConnection(
                    url, username, password);

            System.out.println("Database Connected Successfully!");

            int choice;

            do {
                System.out.println("\n===== INVENTORY MANAGEMENT =====");
                System.out.println("1. Insert Products");
                System.out.println("2. Retrieve Products");
                System.out.println("3. Update Product Stock");
                System.out.println("4. Search and Update Price");
                System.out.println("5. Aggregate and Delete Product");
                System.out.println("0. Exit");

                System.out.print("Enter choice: ");
                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    // PART (a): INSERT PRODUCTS
                    case 1: {

                        String query =
                                "INSERT INTO Product VALUES (?, ?, ?, ?, ?)";

                        PreparedStatement ps =
                                con.prepareStatement(query);

                        int[] ids = {101, 102, 103};
                        String[] names = {
                                "Laptop", "Mouse", "Keyboard"
                        };
                        String[] categories = {
                                "Electronics", "Electronics", "Electronics"
                        };
                        int[] quantities = {10, 15, 25};
                        double[] prices = {55000, 500, 1200};

                        for (int i = 0; i < 3; i++) {
                            try {
                                ps.setInt(1, ids[i]);
                                ps.setString(2, names[i]);
                                ps.setString(3, categories[i]);
                                ps.setInt(4, quantities[i]);
                                ps.setDouble(5, prices[i]);

                                ps.executeUpdate();

                                System.out.println(
                                        names[i] + " inserted successfully");

                            } catch (SQLException e) {
                                if (e.getErrorCode() == 1062) {
                                    System.out.println(
                                            "Duplicate Product ID: " + ids[i]);
                                } else {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }

                        ps.close();
                        break;
                    }

                    // PART (b): CONDITIONAL RETRIEVAL AND SORTING
                    case 2: {

                        System.out.print("Enter category: ");
                        String category = sc.nextLine();

                        String query =
                                "SELECT * FROM Product " +
                                        "WHERE category = ? AND quantity < 20 " +
                                        "ORDER BY price DESC";

                        PreparedStatement ps =
                                con.prepareStatement(query);

                        ps.setString(1, category);

                        ResultSet rs = ps.executeQuery();

                        while (rs.next()) {
                            System.out.println(
                                    "ID: " + rs.getInt("product_id"));
                            System.out.println(
                                    "Name: " + rs.getString("product_name"));
                            System.out.println(
                                    "Category: " + rs.getString("category"));
                            System.out.println(
                                    "Quantity: " + rs.getInt("quantity"));
                            System.out.println(
                                    "Price: " + rs.getDouble("price"));
                            System.out.println("-------------------");
                        }

                        rs.close();
                        ps.close();
                        break;
                    }

                    // PART (c): UPDATE PRODUCT STOCK
                    case 3: {

                        System.out.print("Enter Product ID: ");
                        int id = sc.nextInt();

                        System.out.print("Enter quantity to add: ");
                        int qty = sc.nextInt();

                        if (qty <= 0) {
                            System.out.println(
                                    "Quantity must be positive!");
                            break;
                        }

                        String query =
                                "UPDATE Product SET quantity = quantity + ? " +
                                        "WHERE product_id = ?";

                        PreparedStatement ps =
                                con.prepareStatement(query);

                        ps.setInt(1, qty);
                        ps.setInt(2, id);

                        int rows = ps.executeUpdate();

                        if (rows > 0) {
                            System.out.println("Stock updated successfully!");
                        } else {
                            System.out.println("Product does not exist!");
                        }

                        ps.close();
                        break;
                    }

                    // PART (d): SEARCH AND MODIFY PRODUCT PRICE
                    case 4: {

                        System.out.print("Enter Product ID: ");
                        int id = sc.nextInt();

                        System.out.print("Enter new price: ");
                        double price = sc.nextDouble();

                        if (price <= 0) {
                            System.out.println(
                                    "Price must be greater than zero!");
                            break;
                        }

                        String search =
                                "SELECT * FROM Product WHERE product_id = ?";

                        PreparedStatement ps =
                                con.prepareStatement(search);

                        ps.setInt(1, id);

                        ResultSet rs = ps.executeQuery();

                        if (rs.next()) {

                            System.out.println("Product Found!");
                            System.out.println(
                                    "Name: " + rs.getString("product_name"));

                            String update =
                                    "UPDATE Product SET price = ? " +
                                            "WHERE product_id = ?";

                            PreparedStatement ps2 =
                                    con.prepareStatement(update);

                            ps2.setDouble(1, price);
                            ps2.setInt(2, id);

                            ps2.executeUpdate();

                            System.out.println("Price updated successfully!");

                            String display =
                                    "SELECT * FROM Product WHERE product_id = ?";

                            PreparedStatement ps3 =
                                    con.prepareStatement(display);

                            ps3.setInt(1, id);

                            ResultSet rs2 = ps3.executeQuery();

                            if (rs2.next()) {
                                System.out.println("Updated Details:");
                                System.out.println(
                                        "ID: " + rs2.getInt("product_id"));
                                System.out.println(
                                        "Name: " + rs2.getString("product_name"));
                                System.out.println(
                                        "Category: " + rs2.getString("category"));
                                System.out.println(
                                        "Quantity: " + rs2.getInt("quantity"));
                                System.out.println(
                                        "Price: " + rs2.getDouble("price"));
                            }

                            rs2.close();
                            ps3.close();
                            ps2.close();

                        } else {
                            System.out.println("Product not found!");
                        }

                        rs.close();
                        ps.close();
                        break;
                    }

                    // PART (e): AGGREGATE QUERY AND CONDITIONAL DELETION
                    case 5: {

                        String sumQuery =
                                "SELECT SUM(quantity * price) AS total " +
                                        "FROM Product";

                        Statement st = con.createStatement();

                        ResultSet rs = st.executeQuery(sumQuery);

                        if (rs.next()) {
                            System.out.println(
                                    "Total Inventory Value: " +
                                            rs.getDouble("total"));
                        }

                        rs.close();
                        st.close();

                        System.out.print("Enter Product ID to delete: ");
                        int id = sc.nextInt();

                        String check =
                                "SELECT quantity FROM Product " +
                                        "WHERE product_id = ?";

                        PreparedStatement ps =
                                con.prepareStatement(check);

                        ps.setInt(1, id);

                        ResultSet rs2 = ps.executeQuery();

                        if (rs2.next()) {

                            int quantity = rs2.getInt("quantity");

                            if (quantity == 0) {

                                String delete =
                                        "DELETE FROM Product " +
                                                "WHERE product_id = ?";

                                PreparedStatement ps2 =
                                        con.prepareStatement(delete);

                                ps2.setInt(1, id);
                                ps2.executeUpdate();

                                System.out.println(
                                        "Product deleted successfully!");

                                ps2.close();

                            } else {
                                System.out.println(
                                        "Cannot delete! Quantity is not zero.");
                            }

                        } else {
                            System.out.println("Product not found!");
                        }

                        rs2.close();
                        ps.close();
                        break;
                    }

                    case 0:
                        System.out.println("Exiting program...");
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }

            } while (choice != 0);

            con.close();

        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }

        sc.close();
    }
}
