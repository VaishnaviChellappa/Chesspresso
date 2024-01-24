package chessCode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:javabook.db";

    public static void displayAllUsers() {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String selectQuery = "SELECT * FROM users";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                int wins = resultSet.getInt("wins");
                int losses = resultSet.getInt("losses");

                System.out.println("User ID: " + id);
                System.out.println("Username: " + username);
                System.out.println("password: " + password);
                System.out.println("Wins: " + wins);
                System.out.println("Losses: " + losses);
                System.out.println("--------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUser(int userId) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String deleteQuery = "DELETE FROM users WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, userId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User with ID " + userId + " deleted successfully.");
            } else {
                System.out.println("User with ID " + userId + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void deleteTable(String tableName) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            String deleteQuery = "DROP TABLE IF EXISTS " + tableName;
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);

            boolean tableExists = preparedStatement.execute();

            if (tableExists) {
                System.out.println("Table " + tableName + " deleted successfully.");
            } else {
                System.out.println("Table " + tableName + " does not exist.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        

//        // Delete a user by ID (change the ID to the desired user)
//        int userIdToDelete = 7; // Change this to the user ID you want to delete
//        System.out.println("Deleting user with ID " + userIdToDelete + ":");
//        deleteUser(userIdToDelete);
//        userIdToDelete = 8; // Change this to the user ID you want to delete
//        System.out.println("Deleting user with ID " + userIdToDelete + ":");
//        deleteUser(userIdToDelete);
        
        
     // Display all existing users
    	
//    	String tableNameToDelete = "users"; // Change this to the table name you want to delete
//        System.out.println("Deleting table " + tableNameToDelete + ":");
//        deleteTable(tableNameToDelete);
        
        
        System.out.println("Displaying all users:");
        displayAllUsers();
    }
}
