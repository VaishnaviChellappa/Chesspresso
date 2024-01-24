package chessCode;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private MatchmakingServer server;
    private ClientHandler opponent;
    private int clientID;

    public ClientHandler(Socket socket, MatchmakingServer server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
            	if (inputLine.startsWith("LOGIN")) {
                    handleLogin(inputLine);
                } else if (inputLine.equals("SEARCH")) {
                    server.pairClients(this);
                }  else if (inputLine.equals("RESIGN")) {
                	handleResignation();
                    if (opponent != null) {
                        opponent.sendMessage("RESIGN");
                    }
                }else if (opponent != null) {
                    opponent.sendMessage(inputLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void handleLogin(String inputLine) {
        String[] parts = inputLine.split(" ");
        if (parts.length >= 2) {
            try {
                clientID = Integer.parseInt(parts[1]);
                System.out.println("Client ID connected: " + clientID);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                
            }
        }
    }

    
    private void handleResignation() {
        if (opponent != null) {
            opponent.sendMessage("OPPONENT_RESIGNED");
            updateDatabaseForGameEnd(this.clientID, opponent.clientID);
            sendMessage("YOU_WIN");
            opponent.sendMessage("YOU_LOSE");
        }
    }
    
    private void updateDatabaseForGameEnd(int winnerID, int loserID) {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            
            String url = "jdbc:sqlite:javabook.db";
            connection = DriverManager.getConnection(url);

            
            String updateWinSql = "UPDATE users SET wins = wins + 1 WHERE id = ?";
            pstmt = connection.prepareStatement(updateWinSql);
            pstmt.setInt(1, winnerID);
            pstmt.executeUpdate();
            pstmt.close();

            
            String updateLossSql = "UPDATE users SET losses = losses + 1 WHERE id = ?";
            pstmt = connection.prepareStatement(updateLossSql);
            pstmt.setInt(1, loserID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); 
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace(); 
            }
        }
    }


    public void setOpponent(ClientHandler opponent) {
        this.opponent = opponent;
    }

    public void sendMessage(String message) {
    	System.out.println("message sent :" + message);
        out.println(message);
    }

    public void sendPairingDetails() {
        
        sendMessage("PAIRED");
    }
}
