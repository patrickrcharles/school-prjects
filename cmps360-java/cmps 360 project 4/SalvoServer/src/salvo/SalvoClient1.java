/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package salvo;

import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Patrick
 */
public class SalvoClient1 extends Application implements SalvoConstants {

    // Indicate whether the player has the turn
    private boolean myTurn = true;

    // Indicate the token for the player
    private char myToken = ' ';

// Indicate the token for the other player
    private char otherToken = ' ';

    // Create and initialize cells
    //private final Cell[][] cell = new Cell[10][10];
    static Cell[][] playerCell = new Cell[10][10];
    static Cell[][] opponentCell = new Cell[10][10];

    // Indicate selected row and column by the current move
    private int rowSelected;
    private int columnSelected;

    private DataInputStream fromServer;
    private DataOutputStream toServer;

    // Continue to play?
    private boolean continueToPlay = true;

    // Wait for the player to mark a cell
    private boolean waiting = true;

    // Host name or ip
    private final String host = "localhost";

    private GridPane playerGrid;
    private GridPane opponentGrid;
    // Create and initialize a title label

    static Label lblTitle;
    // Create and initialize a status label
    static Label lblStatus;

    static Ship[] ships = new Ship[4];
    static boolean allShipsPlaced = false;
    static int shipsPlaced;
    static int sendMoveCounter = 0;

    static boolean hit;
    static boolean miss;
    static boolean occupied;

    static Label playerText;
    static Label playerStatus;

    static Label enemyText;
    static Label enemyStatus;

    @Override // Override the start method in the Application class
    public void start(Stage stage) throws IOException {

        System.out.println("-------- client started\n");

        playerGrid = new GridPane();
        playerGrid.setGridLinesVisible(true);
        playerGrid.setMaxSize(300, 300);

        opponentGrid = new GridPane();
        opponentGrid.setGridLinesVisible(true);
        opponentGrid.setMaxSize(300, 300);

        // Pane to hold cell
        BorderPane player = new BorderPane();
        BorderPane enemy = new BorderPane();

        playerText = new Label();
        playerStatus = new Label();
        enemyText = new Label();
        enemyStatus = new Label();
        lblTitle = new Label();
        lblStatus = new Label();

        playerText.setText("Place ships here");
        player.setTop(playerText);
        player.setCenter(playerGrid);
        player.setBottom(playerStatus);

        enemyText.setText("Attack opponent here");
        enemy.setTop(enemyText);
        enemy.setCenter(opponentGrid);
        enemy.setBottom(enemyStatus);

        BorderPane root = new BorderPane();
        root.setLeft(player);
        root.setTop(lblTitle);
        root.setRight(enemy);
        root.setBottom(lblStatus);

        //create cells for grids
        for (int i = 0; i < 10; i++) {
            //int k = 0; //for cell id
            for (int j = 0; j < 10; j++) {
                playerGrid.add(playerCell[i][j] = new Cell(i, j), j, i);
                playerCell[i][j].type = "player cell";
                opponentGrid.add(opponentCell[i][j] = new Cell(i, j), j, i);
                opponentCell[i][j].type = "opponent cell";
            }
        }
        //need to create ships

        for (int i = 0; i < 4; i++) {
            int x = (5 - i);
            ships[i] = new Ship(x, false);

        }
        placeShips();
        checkBoard();

        Scene scene = new Scene(root, 650, 400);
        stage.setTitle("SalvoClient"); // Set the stage title
        stage.setScene(scene); // Place the scene in the stage
        stage.show(); // Display the stage

        // Connect to the server\
        connectToServer();
    }

    static void placeShips() {

        if (allShipsPlaced == false && shipsPlaced < 4) {
            lblStatus.setText(" place " + ships[shipsPlaced].getShipType() + " on board ");
            //System.out.println("-- place ship " + ships[shipsPlaced].getShipType() + "\n");
            // somehow

        }
    }

    static void checkBoard() {

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {

                if (playerCell[i][j].occupied == true && playerCell[i][j].hit == false) {
                    playerCell[i][j].setStyle("-fx-background-color: blue;");

                    //opponentCell[i][j].setStyle("-fx-background-color: blue;");
                    //System.out.println("--checkboard()  occupied==true hit==false  :  Cell color changed to blue");
                    int x = playerCell[i][j].shipID;
                    int a = i, b = j;
                    //Platform.runLater(() ->playerStatus.setText(ships[x].getShipType() + " placed at [" + a + "][" + b + "]\n"));

                }
                if (playerCell[i][j].occupied == true && playerCell[i][j].hit == true) {
                    playerCell[i][j].setStyle("-fx-background-color: red;");

                    System.out.println("--CHECKBOARD()  occupied==true hit==true :  Cell color changed to red");

                    //get ship that was selected
                    int x = playerCell[i][j].shipID;
                    System.out.println("--CHECKBOARD()  ships[x] type " + ships[x].getShipType() + "\n");
                    System.out.println("--CHECKBOARD()  ships[x].health " + ships[x].health + "\n");
                    //System.out.println("--CHECKBOARD() " + ships[x].getShipType() + " health is : " + ships[x].health + "\n");

                    Platform.runLater(() -> playerStatus.setText(ships[x].getShipType() + " was hit \n"));

                }
                if (playerCell[i][j].occupied == false && (playerCell[i][j].hit == true
                        || playerCell[i][j].miss == true)) {
                    playerCell[i][j].setStyle("-fx-background-color: yellow;");

                    //int x = playerCell[i][j].shipNum;
                    //System.out.println("--CHECKBOARD()  playerCell[" + i + "][" + j + "]\n");
                    //System.out.println("--CHECKBOARD()  playerCell[" + i + "][" + j + "] type : "+playerCell[i][j].type+"\n");
                    Platform.runLater(() -> playerStatus.setText("shot missed \n"));
                    //opponentCell[i][j].setStyle("-fx-background-color: yellow;");
                    System.out.println("--checkboard()  occupied==false hit==true || miss==true :  Cell color changed to yellow");
                }
            }
        }
    }

    private void connectToServer() {
        try {
            System.out.println("-------- connect to server\n");
            // Create a socket to connect to the server
            Socket socket = new Socket(host, 8000);

            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());

        } catch (Exception ex) {
            System.out.println("-------- failed to connect\n");
        }
// Control the game on a separate thread
        new Thread(() -> {
            try {
                // Get notification from the server
                int player = fromServer.readInt();
                System.out.println("------------ new client thread started\n");
                // Get notification from the server
                System.out.println(" while (continueToPlay) : " + continueToPlay + "\n");
                // Am I player 1 or 2?
                if (player == PLAYER1) {
                    myToken = 'X';
                    otherToken = 'O';
                    Platform.runLater(() -> {
                        lblTitle.setText("Player 1");
                        lblStatus.setText("Waiting for player 2 to join");
                    });

                    // Receive startup notification from the server
                    fromServer.readInt(); // Whatever read is ignored

                    // The other player has joined
                    Platform.runLater(()
                            -> lblStatus.setText("Player 2 has joined. I start first"));

                    // It is my turn
                    myTurn = true;
                } else if (player == PLAYER2) {
                    myToken = 'O';
                    otherToken = 'X';
                    Platform.runLater(() -> {
                        lblTitle.setText("Player 2");
                        lblStatus.setText("Waiting for player 1 to move");
                        //System.out.println("------------ lblTitle = " + lblTitle.getText() + " \n");
                        //System.out.println("------------ lblStatus = " + lblStatus.getText() + " \n");
                    });
                }
                System.out.println(" while (continueToPlay) : " + continueToPlay + "\n");
                // Continue to play
                while (continueToPlay) {
                    System.out.println(" while (continueToPlay) {\n");
                    if (player == PLAYER1) {
                        System.out.println("  if (player == PLAYER1) {\n");

                        System.out.println("PLAYER1 waitForPlayerAction(); // Wait for player 1 to move\n");
                        waitForPlayerAction(); // Wait for player 1 to move

                        System.out.println("PLAYER1 sendMove(); // Send player 1 move to the server\n");
                        sendMove(); // Send the move to the server

                        System.out.println("PLAYER1 receiveInfoFromServer(); // Receive info from the server\n");
                        receiveInfoFromServer(); // Receive info from the server

                        //System.out.println("PLAYER1 waitForPlayerAction(); // Wait for player 1 to move\n");
                        //waitForPlayerAction(); // Wait for player 1 to move
                        //System.out.println("PLAYER1 receiveBooleanInfoFromServer(); // Receive info from the server\n");
                        //receiveBooleanInfoFromServer(); // Receive info from the server
                        //System.out.println("PLAYER1 waitForPlayerAction(); // Wait for player 1 to move\n");
                        //waitForPlayerAction(); // Wait for player 1 to move
                        //System.out.println("PLAYER1 receiveInfoFromServer(); // Receive info from the server\n");
                        //receiveInfoFromServer(); // Receive info from the server
                        //System.out.println("PLAYER1 sendMove(); // Send player 1 move to the server\n");
                        //sendMove(); // Send the move to the server
                        //System.out.println("PLAYER1 receiveInfoFromServer(); // Receive info from the server\n");
                        //receiveInfoFromServer(); // Receive info from the server
                    } else if (player == PLAYER2) {
                        System.out.println("  if (player == PLAYER2) {\n");

                        System.out.println("PLAYER2 receiveInfoFromServer(); // receive info from server\n");
                        receiveInfoFromServer(); // Receive info from the server

                        //System.out.println("PLAYER2 sendBoolean(); //send boolean info from server\n");
                        //sendBoolean();
                        System.out.println("PLAYER2 waitForPlayerAction();  // wait for player 2\n");
                        waitForPlayerAction(); // Wait for player 2 to move

                        //System.out.println("PLAYER2 receiveInfoFromServer(); // receive info from server\n");
                        //receiveInfoFromServer(); // Receive info from the server
                        System.out.println("PLAYER2 sendMove() // send player 2 move to server\n");
                        sendMove(); // Send player 2's move to the server   

                        //System.out.println("PLAYER2 receiveInfoFromServer(); // receive info from server\n");
                        //receiveInfoFromServer(); // Receive info from the server
                        //System.out.println("PLAYER1 receiveBooleanInfoFromServer(); // Receive info from the server\n");
                        //receiveBooleanInfoFromServer(); // Receive info from the server
                        //System.out.println("PLAYER1 sendMove(); // Send player 1 move to the server\n");
                        //sendMove(); // Send the move to the server
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        ).start();
    }

    /**
     * Wait for the player to mark a cell
     */
    private void waitForPlayerAction() throws InterruptedException {
        while (waiting) {
            Thread.sleep(100);
        }
        waiting = true;
        System.out.println("-------- waiting for player \n");
    }

    /**
     * Send this player's move to the server
     */
    private void sendMove() throws IOException, java.io.IOException {

        System.out.println("-------- send move \n");
        //System.out.println("-------- toServer.writeInt(rowSelected); \n");
        //System.out.println("-------- toServer.writeInt(columnSelected); \n");

        toServer.writeInt(rowSelected); // Send the selected row
        toServer.writeInt(columnSelected); // Send the selected column

        //System.out.println("-------- send move \n");
        System.out.println("---------sent rowSelected : " + rowSelected
                + " sent columnSelected : " + columnSelected + "\n");

    }

    private void sendBoolean() throws IOException, java.io.IOException {

        System.out.println("-------- send Boolean \n");
        System.out.println("-------- toServer.writeBoolean(hit); \n");
        System.out.println("-------- toServer.writeBoolean(miss); \n");
        System.out.println("-------- toServer.writeBoolean(occupied); \n");

        toServer.writeBoolean(hit); // Send the selected row
        toServer.writeBoolean(miss); // Send the selected row
        toServer.writeBoolean(occupied); // Send the selected row

        System.out.println("-------- send boolean \n");
        System.out.println("---------sent hit : " + hit
                + " sent miss : " + miss
                + " send occupied : " + occupied + "\n");
    }

    /**
     * Receive info from the server
     */
    private void receiveInfoFromServer() throws IOException, java.io.IOException {
// Receive game status

        int status = fromServer.readInt();
        //int status = 4;
        System.out.println("######## receiveInfoFromServer()    STATUS : " + status + " \n");
        System.out.println("######## receiveInfoFromServer()    STATUS 4 = CONTINUE \n");

        switch (status) {

            case PLAYER1_WON:
                // Player 1 won, stop playing
                continueToPlay = false;
                if (myToken == 'X') {
                    Platform.runLater(() -> lblStatus.setText("I won! (X)"));
                } else if (myToken == 'O') {
                    Platform.runLater(()
                            -> lblStatus.setText("Player 1 (X) has won!"));
                    receiveMove();
                }
                break;
            case PLAYER2_WON:
                // Player 2 won, stop playing
                continueToPlay = false;
                if (myToken == 'O') {
                    Platform.runLater(() -> lblStatus.setText("I won! (O)"));
                } else if (myToken == 'X') {
                    Platform.runLater(()
                            -> lblStatus.setText("Player 2 (O) has won!"));
                    receiveMove();
                }
                break;
            case DRAW:
                // No winner, game is over
                continueToPlay = false;
                Platform.runLater(()
                        -> lblStatus.setText("Game is over, no winner!"));
                if (myToken == 'O') {
                    receiveMove();
                }
                break;
            default:
                System.out.println("******* receiveInfoFromServer() DEFAULT:  \n");
                System.out.println("******* receiveMove();  \n");
                receiveMove();

                System.out.println("******* SalvoClient.checkBoard();  \n");
                SalvoClient1.checkBoard();
                myTurn = true; // It is my turn
                Platform.runLater(() -> lblStatus.setText("My turn"));

                //myTurn = false; 
                System.out.println("******* myTurn : " + myTurn + " \n");
                break;
        }

    }

    private void receiveBooleanInfoFromServer() throws IOException, java.io.IOException {
// Receive game status

        int status = fromServer.readInt();
        
        System.out.println("######## receiveBooleanInfoFromServer()    STATUS : " + status + " \n");
        System.out.println("######## receiveBooleanInfoFromServer()    STATUS 4 = CONTINUE \n");

        switch (status) {

            case PLAYER1_WON:
                // Player 1 won, stop playing
                continueToPlay = false;
                if (myToken == 'X') {
                    Platform.runLater(() -> lblStatus.setText("I won! (X)"));
                } else if (myToken == 'O') {
                    Platform.runLater(()
                            -> lblStatus.setText("Player 1 (X) has won!"));
                    receiveMove();
                }
                break;
            case PLAYER2_WON:
                // Player 2 won, stop playing
                continueToPlay = false;
                if (myToken == 'O') {
                    Platform.runLater(() -> lblStatus.setText("I won! (O)"));
                } else if (myToken == 'X') {
                    Platform.runLater(()
                            -> lblStatus.setText("Player 2 (O) has won!"));
                    receiveMove();
                }
                break;
            case DRAW:
                // No winner, game is over
                continueToPlay = false;
                Platform.runLater(()
                        -> lblStatus.setText("Game is over, no winner!"));
                if (myToken == 'O') {
                    receiveMove();
                }
                break;
            default:
                System.out.println("******* receiveBooleanInfoFromServer() DEFAULT:  \n");
                System.out.println("******* receiveBooleanMove();  \n");
                receiveBoolean();

                System.out.println("******* SalvoClient.checkBoard();  \n");
                SalvoClient1.checkBoard();

                Platform.runLater(() -> lblStatus.setText("My turn"));

                myTurn = true; // It is my turn
                System.out.println("******* myTurn : " + myTurn + " \n");
                break;
        }

    }

    private void receiveMove() throws IOException, java.io.IOException {

        System.out.println("-------- receive move \n");

        // Get the other player's move
        int row = fromServer.readInt();
        int column = fromServer.readInt();

        //System.out.println("+++++++ playerCell[row][column].updateCells(); ");
        if (sendMoveCounter > 0) {

            playerCell[row][column].hit = true;
            playerCell[row][column].updateCells();
            checkBoard();

        }
        sendMoveCounter++;

        System.out.println("+++++++ private void receiveMove() rowSelected : " + row
                + "-------- sent columnSelected : " + column + "\n");

    }

    private void receiveBoolean() throws IOException, java.io.IOException {

        System.out.println("-------- receive BOOLEAN \n");

        // Get the other player's move
        int row = fromServer.readInt();
        int column = fromServer.readInt();

        hit = fromServer.readBoolean();
        miss = fromServer.readBoolean();
        occupied = fromServer.readBoolean();

        //if (sendMoveCounter > 0) {
        playerCell[this.rowSelected][this.columnSelected].hit = hit;
        playerCell[this.rowSelected][this.columnSelected].miss = miss;
        playerCell[this.rowSelected][this.columnSelected].occupied = occupied;

        playerCell[this.rowSelected][column].updateCells();
        //}

        //Platform.runLater(() -> playerCell[row][column].setToken(otherToken));
        //playerCell[row][column].updateCells();
        //opponentCell[row][column].updateCells();
        //opponentCell[row][column].updateCells();
        System.out.println("private void receiveBoolean()  receive booleans : " + hit
                + " receive miss : " + miss + " receive occupied : " + occupied + "\n");

    }

    // An inner class for a cell
    public class Cell extends Pane {
// Indicate the row and column of this cell in the board

        String type;
        //coordinates in grid of cell
        private final int row; //use for ship coordinates too
        private final int column; //use for ship coordinates too
        //used to determine status of Cell
        boolean occupied, hit, miss;

        // which ship, determined by size
        int shipID;

        // health of ship located in 
        int shipHealth;

        // if occupied=true, hit=true, shiphit=true. decrease health
        boolean shipHit;

        private char token = ' ';

        public Cell(int row, int column) {

            this.row = row;
            this.column = column;
            this.setPrefSize(2000, 2000); // What happens without this?
            this.occupied = false;
            this.hit = false;
            this.miss = false;
            setStyle("-fx-border-color: black"); // Set cell's border
            // add mouse click handler
            this.setOnMouseClicked(e -> handleMouseClick());
        }

        /**
         * Return token
         *
         * @return
         */
        public char getToken() {
            return token;
        }

        /**
         * Set a new token
         *
         * @param c
         */
        public void setToken(char c) {
            //System.out.println("setToken() token : " + c + "\n");
            token = c;
        }

        protected void updateCells() {

//            System.out.println("updateCells()  " + ships[this.shipNum].getShipType() + " was hit\n");
//            System.out.println("updateCells()  " + ships[this.shipNum].size + " ship size\n");
//            System.out.println("updateCells()  " + ships[this.shipNum].health + " ship health\n");
            if (this.occupied == true && this.hit == false && sendMoveCounter > 0) {
                //this.hit = false;
                this.miss = true;
                this.setStyle("-fx-background-color: blue;");
                System.out.println("--updateCells()  occupied==true hit==false  :  Cell color changed to blue");

            }
            //occupied and hit
            if (this.occupied == true && this.hit == true && sendMoveCounter > 0) {

                this.miss = false;
                this.setStyle("-fx-background-color: red;");
                System.out.println("--updateCells()  occupied==true hit==true :  Cell color changed to red");

                int x = playerCell[this.row][this.column].shipID;

                //playerStatus.setText("updateCells()  "+ships[x].getShipType() + " was hit current health is : " + ships[x].health + "\n");
                System.out.println("updateCells()  " + ships[this.shipID].getShipType() + " was hit\n");
                System.out.println("updateCells()  " + ships[this.shipID].getShipType() + "current health : " + ships[this.shipID].health + "\n");

            }
            //if not occupied and not hit/missed yet
            if (this.occupied == false && sendMoveCounter > 0 && (this.hit == false
                    || this.miss == false)) {

                this.setStyle("-fx-background-color: yellow;");
                System.out.println("--updateCells()  occupied==false hit==true || miss==true :  Cell color changed to yellow");

            }
        }

        protected void placeShips(int size) {
            //int temp = size;
            switch (size) {
                case 5:
                    ships[0].health = 5;
                    ships[0].x1 = this.row;
                    ships[0].y1 = this.column + 1;
                    ships[0].x2 = this.row;
                    ships[0].y2 = this.column + 2;
                    ships[0].x3 = this.row;
                    ships[0].y3 = this.column + 3;
                    ships[0].x4 = this.row;
                    ships[0].y4 = this.column + 4;
                    ships[0].x5 = this.row;
                    ships[0].y5 = this.column + 5;

                    playerCell[this.row][this.column].shipHealth = 5;
                    playerCell[this.row][this.column].shipID = 0;

                    for (int i = 0; i < size; i++) {

                        playerCell[this.row][this.column + i].occupied = true;
                    }
                    break;

                case 4:
                    ships[1].health = 4;
                    ships[1].x1 = this.row;
                    ships[1].y1 = this.column + 1;
                    ships[1].x2 = this.row;
                    ships[1].y2 = this.column + 2;
                    ships[1].x3 = this.row;
                    ships[1].y3 = this.column + 3;
                    ships[1].x4 = this.row;
                    ships[1].y4 = this.column + 4;

                    playerCell[this.row][this.column].shipHealth = 4;
                    playerCell[this.row][this.column].shipID = 1;

                    for (int i = 0; i < size; i++) {

                        playerCell[this.row][this.column + i].occupied = true;
                    }

                    break;
                case 3:

                    ships[2].health = 3;
                    ships[2].x1 = this.row;
                    ships[2].y1 = this.column + 1;
                    ships[2].x2 = this.row;
                    ships[2].y2 = this.column + 2;
                    ships[2].x3 = this.row;
                    ships[2].y3 = this.column + 3;

                    playerCell[this.row][this.column].shipHealth = 3;
                    playerCell[this.row][this.column].shipID = 2;

                    for (int i = 0; i < size; i++) {

                        playerCell[this.row][this.column + i].occupied = true;
                    }
                    break;

                case 2:
                    ships[3].health = 2;
                    ships[3].x1 = this.row;
                    ships[3].y1 = this.column + 1;
                    ships[3].x2 = this.row;
                    ships[3].y2 = this.column + 2;

                    playerCell[this.row][this.column].shipHealth = 2;
                    playerCell[this.row][this.column].shipID = 3;

                    for (int i = 0; i < size; i++) {

                        playerCell[this.row][this.column + i].occupied = true;
                    }
                    break;
            }
        }

        /* Handle a mouse click event */
        private void handleMouseClick() {

            System.out.println(this.type + " clicked : [" + this.row + "][" + this.column + "]\n");

            if (myTurn == true) {
                //setToken(myToken);   

                //if all ships placed, keep placing them
                if (shipsPlaced < 4) {

                    // ex shipsplaced =0, need to place battleship which is size 5
                    //5-0 = placeship(5)
                    placeShips((5 - shipsPlaced));
                    shipsPlaced++;

                    System.out.println("--- if (shipsPlaced < 4) { : " + shipsPlaced + "\n");
                    SalvoClient1.placeShips();
                    SalvoClient1.checkBoard();
                    updateCells();
                    //System.out.println("if (shipsPlaced <= 4 ) : allShipsPlaced : " + allShipsPlaced + "\n");
                }
                if (shipsPlaced == 4) {
                    allShipsPlaced = true;
                    System.out.println("--- mouse allShips placed : " + allShipsPlaced + "\n");
                }

                if (allShipsPlaced == true) {

                    playerGrid.setDisable(true);

                    rowSelected = opponentCell[this.row][this.column].row;
                    columnSelected = opponentCell[this.row][this.column].column;

                    // update cell clicked
                    updateCells();
                    SalvoClient1.checkBoard();

                    //myTurn = false;
                    lblStatus.setText("Waiting for the other player to move");

                    // Just completed a successful move
                   
                    
                    myTurn = false;
                    waiting = false;
                     System.out.println("--- private void handleMouseClick() { : " + myTurn + "\n");
                    System.out.println("--- private void handleMouseClick() { : " + waiting + "\n");

                    //System.out.println("--- myTurn over, waiting == false \n");
                }
            }
        }
    }
}
