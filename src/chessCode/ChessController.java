package chessCode;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ChessController implements ChessDelegate, ActionListener {
    private String MATCHMAKING_SERVER_ADDR = "localhost";
    private int MATCHMAKING_SERVER_PORT = 50000; 

    private ChessModel chessModel = new ChessModel();
    
    private Player myColor;
    private int userID;

    private JFrame frame;
    private ChessView chessBoardPanel;
    private JButton resetBtn;
    private JButton searchBtn;
    private JLabel statusLabel; 


    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    ChessController(int userID) {
    	
    	this.setUserID(userID);
    	
        chessModel.reset();
        
        frame = new JFrame("Chess");
        frame.setSize(500, 550);
        frame.setLocation(200, 1300);
        frame.setLayout(new BorderLayout());

        chessBoardPanel = new ChessView(this);

        frame.add(chessBoardPanel, BorderLayout.CENTER);
        
        statusLabel = new JLabel("Welcome to Chess");
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        frame.add(statusLabel, BorderLayout.PAGE_START);

        var buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        resetBtn = new JButton("Resign");
        resetBtn.addActionListener(this);
        buttonsPanel.add(resetBtn);

        searchBtn = new JButton("Search Opponent");
        buttonsPanel.add(searchBtn);
        searchBtn.addActionListener(this);

        frame.add(buttonsPanel, BorderLayout.PAGE_END);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                closeConnections();
            }
        });
    }

//    public static void main(String[] args) {
//        new ChessController();
//    }
//    
//    public void showChessGame() {
//        new ChessController(); 
//    }

    @Override
    public ChessPiece pieceAt(int col, int row) {
        return chessModel.pieceAt(col, row);
    }
    

    @Override
    public void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
        ChessPiece piece = chessModel.pieceAt(fromCol, fromRow);
        if (piece != null && piece.getPlayer() == myColor) {
        	boolean isValid = chessModel.isValidMove(chessModel.pieceAt(fromCol, fromRow), fromCol, fromRow, toCol, toRow);
        	if (isValid) {
	        	chessModel.movePiece(fromCol, fromRow, toCol, toRow);
	            chessBoardPanel.repaint();
	
	            if (printWriter != null) {
	                printWriter.println(fromCol + "," + fromRow + "," + toCol + "," + toRow);
	            }
	            onOpponentTurn();
            }
        } 
    }



    private void receiveMove(Scanner scanner) {
        while (scanner.hasNextLine()) {
        	
        	
            var moveStr = scanner.nextLine();
            System.out.println("Received message: " + moveStr);
            
            if ("RESIGN".equals(moveStr)) {
                SwingUtilities.invokeLater(() -> resetForNewGame());
                return;
            } 
            
            if (moveStr.equals("OPPONENT_RESIGNED")) {
                // Handle opponent resignation
                handleOpponentResignation();
                return;
            } 
            
            
            
            System.out.println("Chess move received: " + moveStr);
            onPlayerTurn();
            var moveStrArr = moveStr.split(",");
            var fromCol = Integer.parseInt(moveStrArr[0]);
            var fromRow = Integer.parseInt(moveStrArr[1]);
            var toCol = Integer.parseInt(moveStrArr[2]);
            var toRow = Integer.parseInt(moveStrArr[3]);
            SwingUtilities.invokeLater(() -> {
                chessModel.movePiece(fromCol, fromRow, toCol, toRow);
                chessBoardPanel.repaint();
            });
        }
    }
    
    private void handleOpponentResignation() {

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, "Your opponent has resigned. You win!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            resetForNewGame();
        });
    }

    private void connectToMatchmakingServer() {
        try {
            socket = new Socket(MATCHMAKING_SERVER_ADDR, MATCHMAKING_SERVER_PORT);
            printWriter = new PrintWriter(socket.getOutputStream(), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            


            
            printWriter.println("LOGIN " + userID);
            printWriter.println("SEARCH");

            
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    String response;
                    
                    while ((response = bufferedReader.readLine()) != null) {
                    	System.out.println("Response recieved :" + response);
                    	 if (response.startsWith("COLOR ")) {
                             myColor = Player.valueOf(response.split(" ")[1]);
                             System.out.println("Assigned color: " + myColor);
                         } else if ("PAIRED".equals(response)) {
                            System.out.println("Paired with an opponent.");
                            SwingUtilities.invokeLater(() -> frame.setTitle("Chess - Opponent Found"));
                            break;
                        }
                    }
                    // Start receiving moves
                    var scanner = new Scanner(socket.getInputStream());
                    receiveMove(scanner);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnections() {
        try {
            if (printWriter != null) printWriter.close();
            if (bufferedReader != null) bufferedReader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == resetBtn) {
            chessModel.reset();
            chessBoardPanel.repaint();
            if (printWriter != null) {
                printWriter.println("RESIGN");
            }
            resetForNewGame();
        } else if (e.getSource() == searchBtn) {
            searchBtn.setEnabled(false);
            frame.setTitle("Chess - Searching for Opponent");
            connectToMatchmakingServer();
        }
    }

    
    private void resetForNewGame() {
        chessModel.reset();
        chessBoardPanel.repaint();
        closeConnections();
        searchBtn.setEnabled(true);
        frame.setTitle("Chess");
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
    
    public void onPlayerTurn() {
        chessBoardPanel.setInteractionEnabled(true);
        updateStatus("Your turn.");
    }

    public void onOpponentTurn() {
        chessBoardPanel.setInteractionEnabled(false);
        updateStatus("Opponent's turn.");
    }

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}


}
