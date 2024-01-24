package chessCode;

import javax.swing.*;
import java.awt.event.*;
import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginSignupScreen extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
	private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;

    public LoginSignupScreen() {
        setTitle("Welcome to Chess Multiplayer");
        setSize(500, 550);
        setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(150, 200, 80, 25); 
        add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(240, 200, 160, 25); 
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(150, 230, 80, 25); 
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(240, 230, 160, 25); 
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(200, 280, 100, 25); 
        loginButton.addActionListener(this);
        add(loginButton);

        signupButton = new JButton("Signup");
        signupButton.setBounds(200, 320, 100, 25); 
        signupButton.addActionListener(this);
        add(signupButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            try {
                Connection connection = DriverManager.getConnection("jdbc:sqlite:javabook.db");

                
                String createTableQuery = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, salt TEXT, wins INTEGER, losses INTEGER)";
                java.sql.Statement statement = connection.createStatement();
                statement.execute(createTableQuery);

                
                String selectQuery = "SELECT * FROM users WHERE username = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setString(1, username);
                //preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                	
                	String storedHashedPassword = resultSet.getString("password");
                    String storedSalt = resultSet.getString("salt");
                    
                    String hashedInputPassword = SaltAndPepperHashing.hashPassword(password, storedSalt);
                	
                    if (storedHashedPassword.equals(hashedInputPassword)) {
	                	int userID = resultSet.getInt("id");
	                	int w = resultSet.getInt("wins");
	                	int l = resultSet.getInt("losses");
	                    
	                    JOptionPane.showMessageDialog(this, "Welcome! Your current ELO rating is " + (800 + 20*(w-l)));
	                    
	                 
	                    this.dispose();
	
	                    
	                    ChessController chessController = new ChessController(userID);
	                 
                    //chessController.showChessGame();
                    } else {
                        // Failed login
                        JOptionPane.showMessageDialog(this, "Login failed. Invalid password.");
                    }
                } else {
                    // User does not exist
                    JOptionPane.showMessageDialog(this, "Login failed. Invalid username.");
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();

            } catch (SQLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e1.getMessage());
            }
        }else if (e.getSource() == signupButton) {
        	
        	String username = usernameField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);
            
            String salt = SaltAndPepperHashing.generateSalt();
            String hashedPassword = SaltAndPepperHashing.hashPassword(password, salt);

            
        	try {
				Connection connection = DriverManager.getConnection("jdbc:sqlite:javabook.db");
				
				// Create the "users" table if it doesn't exist
				String createTableQuery = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, salt TEXT, wins INTEGER, losses INTEGER)";
				java.sql.Statement statement = connection.createStatement(); 
				statement.execute(createTableQuery);

				
				String insertQuery = "INSERT INTO users (username, password, salt, wins, losses) VALUES (?, ?, ?, 0, 0)";

				PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
				preparedStatement.setString(1, username);
				preparedStatement.setString(2, hashedPassword);
				preparedStatement.setString(3, salt);

				int rowsAffected = preparedStatement.executeUpdate();


                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Signup successful! with password " + hashedPassword);
                } else {
                    JOptionPane.showMessageDialog(this, "Signup failed. Please try again.");
                }
                
                preparedStatement.close();
                connection.close();
                
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error: " + e1.getMessage());
			}

 catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }



    public static void main(String[] args) {
        new LoginSignupScreen();
    }
}
