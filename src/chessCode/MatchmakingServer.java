package chessCode;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


public class MatchmakingServer {
    private ServerSocket serverSocket;
    private ExecutorService pool;
    private List<ClientHandler> waitingClients;

    public MatchmakingServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(10); 
        waitingClients = new ArrayList<>();
    }

    public void start() throws IOException {
        System.out.println("Matchmaking Server Started");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            pool.execute(clientHandler);
        }
    }

    public synchronized void pairClients(ClientHandler clientHandler) {
        waitingClients.add(clientHandler);
        if (waitingClients.size() >= 2) {
            // Pair the first two clients
            ClientHandler client1 = waitingClients.remove(0);
            ClientHandler client2 = waitingClients.remove(0);
            client1.setOpponent(client2);
            client2.setOpponent(client1);
            
            client1.sendMessage("COLOR WHITE");
            client2.sendMessage("COLOR BLACK");
            
            client1.sendPairingDetails();
            client2.sendPairingDetails();
        }
    }


    public static void main(String[] args) throws IOException {
        int port = 50000; 
        MatchmakingServer server = new MatchmakingServer(port);
        server.start();
    }
}
