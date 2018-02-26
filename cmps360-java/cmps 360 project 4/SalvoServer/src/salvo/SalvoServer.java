/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package salvo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import static salvo.SalvoConstants.CONTINUE;
import static salvo.SalvoConstants.DRAW;
import static salvo.SalvoConstants.PLAYER1_WON;
import static salvo.SalvoConstants.PLAYER2_WON;

/**
 *
 * @author Patrick
 */
public class SalvoServer extends Application implements SalvoConstants {

    private int sessionNo = 1; // Number a session 

    // client1 sends move
    //client 2 sends back booeleans
    //private boolean hit, miss, occupied;
    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) throws IOException {

        System.out.println("-----server started \n");

        TextArea taLog = new TextArea();
        // Create a scene and place it in the stage
        Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
        primaryStage.setTitle("SalvoServer"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        new Thread(() -> {
            try {
                System.out.println("----------new thread \n");

                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(8000);
                Platform.runLater(() -> taLog.appendText(new Date()
                        + ": Server started at socket 8000\n"));

                // Ready to create a session for every two players
                while (true) {
                    Platform.runLater(() -> taLog.appendText(new Date()
                            + ": Wait for players to join session " + sessionNo + '\n'));

// Connect to player 1
                    Socket player1 = serverSocket.accept();

                    Platform.runLater(() -> {
                        taLog.appendText(new Date() + ": Player 1 joined session "
                                + sessionNo + '\n');
                        taLog.appendText("Player 1's IP address"
                                + player1.getInetAddress().getHostAddress() + '\n');
                    });

// Notify that the player is Player 1
                    new DataOutputStream(
                            player1.getOutputStream()).writeInt(SalvoConstants.PLAYER1);

// Connect to player 2
                    Socket player2 = serverSocket.accept();

                    Platform.runLater(() -> {
                        taLog.appendText(new Date()
                                + ": Player 2 joined session " + sessionNo + '\n');
                        taLog.appendText("Player 2's IP address"
                                + player2.getInetAddress().getHostAddress() + '\n');
                    });

// Notify that the player is Player 2
                    new DataOutputStream(
                            player2.getOutputStream()).writeInt(SalvoConstants.PLAYER2);

// Display this session and increment session number
                    Platform.runLater(()
                            -> taLog.appendText(new Date()
                                    + ": Start a thread for session " + sessionNo++ + '\n'));

                    // Launch a new thread for this session of two players
                    new Thread(new HandleASession(player1, player2)).start();
                    
                    System.out.println("-----new Thread(new HandleASession("
                            + player1 + ", " + player2 + ")).start();\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();

        //start player1
        SalvoClient1 client = new SalvoClient1();
        client.start(primaryStage);

    }

    // Define the thread class for handling a new session for two players 
    public class HandleASession implements Runnable, SalvoConstants {

        // Define the thread class for handling a new session for two players
        private Socket player1;
        private Socket player2;

// Create and initialize cells
        private int[][] cell = new int[10][10];

        private DataInputStream fromPlayer1;
        private DataOutputStream toPlayer1;
        private DataInputStream fromPlayer2;
        private DataOutputStream toPlayer2;

        // Continue to play
        private boolean continueToPlay = true;

        /**
         * Construct a thread
         *
         * @param player1
         * @param player2
         */
        public HandleASession(Socket player1, Socket player2) {
            this.player1 = player1;
            this.player2 = player2;

            // Initialize cells
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    cell[i][j] = ' ';
                }
            }
        }

        /**
         * Implement the run() method for the thread
         */
        @Override
        public void run() {
            try {

                System.out.println("----public void run() \n");
// Create data input and output streams
                fromPlayer1 = new DataInputStream(
                        player1.getInputStream());
                toPlayer1 = new DataOutputStream(
                        player1.getOutputStream());
                fromPlayer2 = new DataInputStream(
                        player2.getInputStream());
                toPlayer2 = new DataOutputStream(
                        player2.getOutputStream());

                // Write anything to notify player 1 to start
                // This is just to let player 1 know to start
                toPlayer1.writeInt(1);
                System.out.println("*********SERVER toPlayer1.writeInt(1); \n");

// Continuously serve the players and determine and report
                // the game status to the players
                while (true) {

                    //System.out.println("*********SERVER toPlayer1.writeInt(1); \n");
//*********************************
// spot to add BOOLS
                    // Receive a move from player 1
                    int row = fromPlayer1.readInt();
                    int column = fromPlayer1.readInt();
                    
                    System.out.println("*********SERVER int row = fromPlayer1.readInt(); : "+row+" \n");
                    System.out.println("*********SERVER int column = fromPlayer1.readInt(); : "+column+" \n");
                    
                    //Boolean hit = fromPlayer1.readBoolean();
                    //Boolean miss = fromPlayer1.readBoolean();
                    //Boolean occupied = fromPlayer1.readBoolean();
                    
                    //System.out.println("*********SERVER Boolean hit = fromPlayer1.readBoolean(); : "+hit+" \n");
                    //System.out.println("*********SERVER Boolean miss = fromPlayer1.readBoolean(); : "+miss+" \n");
                    //System.out.println("*********SERVER Boolean occupied = fromPlayer1.readBoolean(); : "+occupied+" \n");

                    //cell[row][column] = 'X';

                    // Check if Player 1 wins
                    if (isWon('X')) {
                        toPlayer1.writeInt(PLAYER1_WON);
                        toPlayer2.writeInt(PLAYER1_WON);
                        sendMove(toPlayer2, row, column);
                        break; // Break the loop
                    } else if (isFull()) { // Check if all cells are filled
                        toPlayer1.writeInt(DRAW);
                        toPlayer2.writeInt(DRAW);
                        sendMove(toPlayer2, row, column);
                        break;
                    } else {

                        toPlayer2.writeInt(CONTINUE);
                        System.out.println("*********SERVER toPlayer2.writeInt(CONTINUE); \n");
                        
                        // Send player 1's selected row and column to player 2
                        
                        System.out.println("*********SERVER sendMove(toPlayer2, row, column); \n");
                        sendMove(toPlayer2, row, column);
                        //sendBoolean(toPlayer2, hit, miss, occupied);
                        
                        //sendMove(toPlayer2, hit, miss, occupied);
                        //System.out.println("*********SERVER SEND BOOLS to player2 \n");
                                      
                    }
                    
                    
                    // player1 sent move to cell, get current boolean status of player 2's cell and send to player1 
                    /*
                    Boolean hit = fromPlayer2.readBoolean();
                    Boolean miss = fromPlayer2.readBoolean();
                    Boolean occupied = fromPlayer2.readBoolean();
                    
                    System.out.println("*********SERVER Boolean hit = fromPlayer2.readBoolean(); : "+hit+" \n");
                    System.out.println("*********SERVER Boolean miss = fromPlayer2.readBoolean(); : "+miss+" \n");
                    System.out.println("*********SERVER Boolean occupied = fromPlayer2.readBoolean(); : "+occupied+" \n");

                    toPlayer1.writeInt(CONTINUE);
                    sendBoolean(toPlayer1, hit,miss, occupied);
                    */

                    // Receive a move from Player 2
                    row = fromPlayer2.readInt();
                    column = fromPlayer2.readInt();

                    
                    System.out.println("*********SERVER row = fromPlayer2.readInt(); " + row + " \n");
                    System.out.println("*********SERVER column = fromPlayer2.readInt(); " + column + " \n");

                   // cell[row][column] = 'O';


                    // Check if Player 2 wins
                    if (isWon('O')) {
                        toPlayer1.writeInt(PLAYER2_WON);
                        toPlayer2.writeInt(PLAYER2_WON);
                        
                        sendMove(toPlayer1, row, column);
                        
                        break;
                    } else {
// Notify player 1 to take the turn

                        toPlayer1.writeInt(CONTINUE);
                        System.out.println("*********SERVER toPlayer1.writeInt(CONTINUE); \n");
                        // Send player 2's selected row and column to player 1
                        System.out.println("*********SERVER sendMove(toPlayer1, row, column); \n");
                        sendMove(toPlayer1, row, column);
                        //sendBoolean(toPlayer2, hit, miss, occupied);
                        //sendMove(toPlayer1, hit, miss, occupied);

                    }
                    
 /*
                    hit = fromPlayer1.readBoolean();
                    miss = fromPlayer1.readBoolean();
                    occupied = fromPlayer1.readBoolean();
                    
                    System.out.println("*********SERVER Boolean hit = toPlayer2.readBoolean(); : "+hit+" \n");
                    System.out.println("*********SERVER Boolean miss = toPlayer2.readBoolean(); : "+miss+" \n");
                    System.out.println("*********SERVER Boolean occupied = toPlayer2.readBoolean(); : "+occupied+" \n");
                    
                    toPlayer2.writeInt(CONTINUE);
                    sendBoolean(toPlayer2, hit,miss, occupied);
      */              
                }
            } catch (IOException ex) {
            }
        }

        /**
         * Send the move to other player
         */
        private void sendMove(DataOutputStream out, int row, int column)
                throws IOException {
            out.writeInt(row); // Send row index
            out.writeInt(column); // Send column index
            System.out.println("*********SERVER sendMove(DataOutputStream out, int row, int column) \n");
        }

        private void sendBoolean(DataOutputStream out, boolean hit, boolean miss, boolean occupied)
                throws IOException {
            out.writeBoolean(hit); // Send row index
            out.writeBoolean(miss); // Send column index
            out.writeBoolean(occupied);
            System.out.println("*********SERVER sendMove(DataOutputStream out, boolean hit, boolean miss, boolean occupied) \n");
        }
        

        /**
         * Determine if the cells are all occupied
         */
        private boolean isFull() {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    if (cell[i][j] == ' ') {
                        return false; // At least one cell is not filled
                    }
                }
            }
            // All cells are filled
            return true;
        }

        /**
         * Determine if the player with the specified token wins
         */
        private boolean isWon(char token) {
// Check all rows
            for (int i = 0; i < 10; i++) {
                if ((cell[i][0] == token)
                        && (cell[i][1] == token)
                        && (cell[i][2] == token)) {
                    return true;
                }
            }

            /**
             * Check all columns
             */
            for (int j = 0; j < 10; j++) {
                if ((cell[0][j] == token)
                        && (cell[1][j] == token)
                        && (cell[2][j] == token)) {
                    return true;
                }
            }

            /**
             * Check major diagonal
             */
            if ((cell[0][0] == token)
                    && (cell[1][1] == token)
                    && (cell[2][2] == token)) {
                return true;
            }

            /**
             * Check subdiagonal
             */
            if ((cell[0][2] == token)
                    && (cell[1][1] == token)
                    && (cell[2][0] == token)) {
                return true;
            }

            /**
             * All checked, but no winner
             */
            return false;
        }
    }
}
