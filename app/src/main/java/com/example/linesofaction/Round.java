package com.example.linesofaction;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import com.example.linesofaction.Rules.Pair;

public class Round {
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private boolean isPlayer1Turn;
    private int player1Score;
    private int player2Score;
    private Player winner;
    private Board gameBoard;
    private Rules rules;

    private static int winsForPlayer1 = 0;
    private static int winsForPlayer2 = 0;
    private List<String> moveLog;
    private Map<Character, Integer> colToIndex;
    private Random random;
    private Scanner scanner;

    /**
     * Initializes a new round with specified players and initial settings.
     * @param p1 Player representing player 1.
     * @param p2 Player representing player 2.
     * @param initialWinner Optional Player who won the previous game.
     * @param boardSetup Optional Integer to choose a pre-defined board setup.
     * @param Player1Score Optional initial score for player 1.
     * @param Player2Score Optional initial score for player 2.
     * @param WinsForPlayer1 Optional total wins count for player 1.
     * @param WinsForPlayer2 Total wins count for player 2.
     */
    public Round(Player p1, Player p2,@Nullable Player initialWinner,@Nullable Integer boardSetup,@Nullable Integer Player1Score,@Nullable Integer Player2Score,@Nullable Integer WinsForPlayer1,Integer WinsForPlayer2) {
        player1 = p1;
        player2 = p2;
        currentPlayer = null;
        isPlayer1Turn = true;

        gameBoard = new Board();
        rules = new Rules();

        moveLog = new ArrayList<>();
        colToIndex = new HashMap<>();
        colToIndex.put('A', 0);
        colToIndex.put('B', 1);
        random = new Random();

        System.out.println("Name of player 1:"+p1.getName());
        System.out.println("Name of player 2:"+p2.getName());
        if(Player1Score !=null && Player2Score!= null){
            this.player1Score = Player1Score;
            this.player2Score = Player2Score;
        }
        if (initialWinner != null) {
            if (initialWinner.getName().equals(player1.getName())) {
                player1.setPieceType('B');
                player2.setPieceType('W');
                currentPlayer = player1;
            } else {
                player1.setPieceType('W');
                player2.setPieceType('B');
                currentPlayer = player2;


            }
        }
        winsForPlayer1 = (WinsForPlayer1 != null) ? WinsForPlayer1 : 0;
        winsForPlayer2 = (WinsForPlayer2 != null) ? WinsForPlayer2 : 0;

        scanner = new Scanner(System.in);
        //gameBoard.resetBoard();
        if (boardSetup != null) {
            switch (boardSetup) {
                case 1:
                    gameBoard.case1Board();
                    player2.setPieceType('W');
                    player1.setPieceType('B');
                    currentPlayer = player2;
                    player1Score = (Player1Score != null) ? Player1Score : 0;
                    player2Score = (Player2Score != null) ? Player2Score : 0;
                    break;
                case 2:
                    gameBoard.case2Board();
                    player2.setPieceType('B');
                    player1.setPieceType('W');
                    currentPlayer = player2;
                    player1Score = (Player1Score != null) ? Player1Score : 0;
                    player2Score = (Player2Score != null) ? Player2Score : 0;
                    break;
                case 3:
                    gameBoard.case3Board();
                    player2.setPieceType('B');
                    player1.setPieceType('W');
                    currentPlayer = player2;
                    player1Score = (Player1Score != null) ? Player1Score : 0;
                    player2Score = (Player2Score != null) ? Player2Score : 0;
                    break;
                case 4:
                    gameBoard.case4Board();
                    player2.setPieceType('W');
                    player1.setPieceType('B');
                    currentPlayer = player1;
                    player1Score = (Player1Score != null) ? Player1Score : 0;
                    player2Score = (Player2Score != null) ? Player2Score : 0;
                    break;
                case 5:
                    gameBoard.case5Board();
                    player2.setPieceType('W');
                    player1.setPieceType('B');
                    currentPlayer = player2;
                    player1Score = (Player1Score != null) ? Player1Score : 0;
                    player2Score = (Player2Score != null) ? Player2Score : 0;
                    break;
                default:
                    gameBoard.resetBoard();
                    break;
            }
        } else {
            gameBoard.resetBoard();
        }

        System.out.println("Scores for player1 "+ player1Score);
        System.out.println("Scores for player2 "+player2Score);
    }
    /**
     * Decides the starting player by a coin toss and sets the piece type accordingly.
     * @param userChoiceHeads Boolean representing the user's guess; true if heads.
     * @return true if the toss matches the user's guess, false otherwise.
     */
    public boolean coinToss(boolean userChoiceHeads) {
        boolean tossResult = random.nextBoolean();

        if (userChoiceHeads == tossResult) {
            currentPlayer = player1;
            player1.setPieceType('B');
            player2.setPieceType('W');
        } else {
            currentPlayer = player2;
            player2.setPieceType('B');
            player1.setPieceType('W');
        }

        return userChoiceHeads == tossResult;
    }
    /**
     * Switches the turn to the other player and announces it.
     */
    public void switchTurn() {
        if (currentPlayer.equals(player1)) {
            // switch to player 2
            currentPlayer = player2;
        } else {
            // switch to player 1
            currentPlayer = player1;
        }
        // indicate whose turn it is
        System.out.println("It's now " + currentPlayer.getName() + "'s turn.");
    }

    /**
     * Retrieves the current player of the game.
     * @return The current player.
     */
    public Player getCurrentPlayer(){
        return currentPlayer;
    }


    /**
     * Starts the game with an optional starting player.
     *
     * @param startingPlayer The player to start the game, null if a coin toss should decide.
     * @param userWonCoinToss inidcates who won the coin toss
     */
    public void startGame(@Nullable Player startingPlayer, @Nullable Boolean userWonCoinToss) {
        System.out.println("Starting a new game...");
        if (winner != null) {
            System.out.println("Last round's winner: " + winner.getName() + " (" + winner.getPieceType() + ")");
        } else {
            System.out.println("No winner from the last round.");
        }

        // Determine the starting player based on the coin toss or an explicitly set starting player
        if (userWonCoinToss != null) {
            currentPlayer = userWonCoinToss ? player1 : player2;
            System.out.println("Coin toss decided: " + currentPlayer.getName() + " will start (User choice was " + userWonCoinToss + ")");
        } else if (startingPlayer != null) {
            currentPlayer = startingPlayer;
            System.out.println("Starting player set explicitly: " + currentPlayer.getName());
        } else {
            // Default to a coin toss if no previous winner and no explicit setting
            currentPlayer = coinToss(true) ? player1 : player2;
            System.out.println("No starting player specified, defaulting to coin toss. " + currentPlayer.getName() + " will start.");
        }
        //set pieces accordingly
        if (winner == null) {
            if (currentPlayer == player1) {
                player1.setPieceType('B');
                player2.setPieceType('W');
            } else {
                player1.setPieceType('W');
                player2.setPieceType('B');
            }
            System.out.println(player1.getName() + " has pieces of type '" + player1.getPieceType() + "'");
            System.out.println(player2.getName() + " has pieces of type '" + player2.getPieceType() + "'");
        } else {

            System.out.println("Maintaining previous piece settings from last game: " + player1.getName() + " (" + player1.getPieceType() + "), " + player2.getName() + " (" + player2.getPieceType() + ")");
        }

        System.out.println("The game has started. It is now " + currentPlayer.getName() + "'s turn with pieces of type '" + currentPlayer.getPieceType() + "'.");
    }
    /**
     * Executes a move from a starting position to a destination and processes game logic after the move.
     * @param fromRow The starting row index of the piece to move.
     * @param fromCol The starting column index of the piece to move.
     * @param toRow The destination row index for the move.
     * @param toCol The destination column index for the move.
     * @return true if the move was successfully executed and false if the move failed or was invalid.
     */
    public boolean nextMove(int fromRow , int fromCol, int toRow, int toCol) {

        System.out.println("Attempting to move piece from (" + fromRow + "," + fromCol + ") to (" + toRow + "," + toCol + ")");

        char selectedPiece = gameBoard.getPieceAt(fromRow, fromCol);
        char playerPieceType = currentPlayer.getPieceType();

        // Display the current player's piece type for debugging
        System.out.println("Current player piece type: " + playerPieceType);

        // Check if there is a piece at the starting position and if it belongs to the current player
        if (selectedPiece == '.' || selectedPiece != playerPieceType) {
            System.out.println("No piece to move from the selected position (" + fromRow + "," + fromCol + ") or piece does not belong to player.");
            gameBoard.displayBoard();
            return false;
        }

        // Check if the move is valid according to the game rules
        if (!rules.isValidMove(gameBoard, currentPlayer, fromRow, fromCol, toRow, toCol)) {
            System.out.println("The move is not valid according to the game rules.");
            return false;
        }

        // Execute the move if valid
        if (gameBoard.movePiece(fromRow, fromCol, toRow, toCol, playerPieceType)) {
            System.out.println("Move executed successfully.");
            gameBoard.displayBoard();

            //Board clonedBoard = gameBoard.cloneBoard();  // Clone the board

            System.out.println("After cloning:");
            //clonedBoard.showBoard();
            // Log the move
            logMove(fromRow, fromCol, toRow, toCol);

            // Check if the move results in a game over condition
            boolean blackConnected = checkConnectedGroup('B');
            boolean whiteConnected = checkConnectedGroup('W');
            if (blackConnected || whiteConnected) {
                determineWinner();  // Determine and announce the winner
                updateScores();  // Update scores based on the outcome
                System.out.println("Round over! " + winner.getName() + " wins!");
                return true;
            } else {
                switchTurn();  // Continue with the next player's turn
                //gameBoard.showBoard();  // Optionally refresh the board display
                System.out.println("It is now " + currentPlayer.getName() + "'s turn.");
            }
        } else {
            System.out.println("Move could not be executed.");
            return false;
        }

        return true;
    }

    /**
     * Logs a move made by a player from one position to another.
     * @param fromRow The starting row index of the piece that was moved.
     * @param fromCol The starting column index of the piece that was moved.
     * @param toRow The destination row index of the piece.
     * @param toCol The destination column index of the piece.
     */
    private void logMove(int fromRow, int fromCol, int toRow, int toCol) {
        String fromPosition = "[" + fromRow + "," + fromCol + "]";
        String toPosition = "[" + toRow + "," + toCol + "]";
        System.out.println(currentPlayer.getName() + " moves from " + fromPosition + " to " + toPosition);
    }





    /**
     * Updates the scores for the players after determining the winner.
     */
    public void updateScores() {
        // Count pieces for each player
        int[] counts = gameBoard.countPiecesByColor();
        int blackCount = counts[0];
        int whiteCount = counts[1];

        int player1Pieces = player1.getPieceType() == 'B' ? blackCount : whiteCount;
        int player2Pieces = player2.getPieceType() == 'B' ? blackCount : whiteCount;

        // Adjust scores based on winners count
        if (winner == player1) {
            // Calculate score difference for the winner
            int scoreDifference = player1Pieces - player2Pieces;
            // Only winner's score updated
            player1Score += scoreDifference;
            System.out.println("Score difference: " + scoreDifference);
        } else if (winner == player2) {
            // Calculate score difference for the winner
            int scoreDifference = player2Pieces - player1Pieces;
            player2Score += scoreDifference;
            System.out.println("Score difference: " + scoreDifference);
        }
        // The loser does not gain or lose points from the round outcome
    }



    /**
     * Determines the winner of the current round based on the game state.
     */
    public void determineWinner() {
        // Check if all pieces of one color are connected for both players
        boolean player1Connected = checkConnectedGroup(player1.getPieceType());
        boolean player2Connected = checkConnectedGroup(player2.getPieceType());

        // Display the winner and update rounds won
        if (player1Connected && !player2Connected) {
            System.out.println(player1.getName() + " wins this round!");
            winner = player1;
            player1.setRoundsWon(player1.getRoundsWon() + 1);
        } else if (!player1Connected && player2Connected) {
            System.out.println(player2.getName() + " wins this round!");
            winner = player2;
            player2.setRoundsWon(player2.getRoundsWon() + 1);
        } else if (player1Connected) { // Simplified check when both are connected or none
            System.out.println("Both players seem to have connected groups. Check for a possible error.");
            //winner = null;
        } else {
            System.out.println("No player has formed a connected group yet.");
            //winner = null;
        }

        if (winner != null) {
            System.out.println("Current winner: " + winner.getName());
        } else {
            System.out.println("No winner determined at this point.");
        }

    }
    /**
     *Checks if a round has finished, used in UI
     */
    public boolean checkForRoundCompletion() {
        determineWinner();  // This sets the 'winner' based on the current game state

        if (winner != null) {
            System.out.println(winner.getName() + " has won the game!");
            // Increase win count
            if (winner.equals(player1)) {
                winsForPlayer1++;

                }
             else if (winner.equals(player2)) {
                winsForPlayer2++;

                }


            // Log the updated scores for transparency and debugging
            System.out.println("Updated scores: Player 1 (wins: " + winsForPlayer1 + "), Player 2 (wins: " + winsForPlayer2 + ")");

            return true;
        }

        return false;
    }



    /**
     * Return wins for player 1
     */
    static public int getWinsForPlayer1() {
        return winsForPlayer1;
    }
    /**
     * Return wins for player 2
     */
    static public int getWinsForPlayer2() {
        return winsForPlayer2;
    }

    /**
     * Checks if all pieces of a specified color are connected.
     * @param color The color of the pieces to check for connectivity.
     * @return true if all pieces of the color are connected, false otherwise.
     */
    public boolean checkConnectedGroup(char color) {
        int startRow = -1;
        int startCol = -1;
        boolean[][] visited = new boolean[8][8];

        // Find the starting piece of the given color
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (gameBoard.getPieceAt(row, col) == color) {
                    startRow = row;
                    startCol = col;
                    break;
                }
            }
            if (startRow != -1) break; // Break outer loop if starting piece is found
        }

        // If no starting piece was found, return false
        if (startRow == -1) return false;

        // Perform DFS from the first found piece
        dfs(startRow, startCol, color, visited);

        // Check if all pieces of the color are visited
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (gameBoard.getPieceAt(row, col) == color && !visited[row][col]) {
                    return false; // Found an unvisited piece of the specified color
                }
            }
        }

        return true; // All pieces of the color are connected
    }

    /**
     * Performs a depth-first search to mark connected pieces.
     * @param row The current row index.
     * @param col The current column index.
     * @param color The color of pieces to check for connectivity.
     * @param visited A 2D array tracking visited positions.
     */
    private void dfs(int row, int col, char color, boolean[][] visited) {
        // Directions arrays for moving in 8 possible directions around a piece
        int[] rowNbr = {-1, 1, 0, 0, -1, -1, 1, 1};
        int[] colNbr = {0, 0, -1, 1, -1, 1, -1, 1};

        // Mark the current position as visited
        visited[row][col] = true;

        // Explore all 8 adjacent locations
        for (int k = 0; k < 8; k++) {
            int newRow = row + rowNbr[k];
            int newCol = col + colNbr[k];

            if (isSafe(newRow, newCol, color, visited)) {
                dfs(newRow, newCol, color, visited);
            }
        }
    }

    /**
     * Checks if a cell is safe to include in the DFS for checking connectivity.
     * @param row The row index of the cell to check.
     * @param col The column index of the cell to check.
     * @param color The color of the pieces to check for connectivity.
     * @param visited A 2D array of boolean values indicating if a cell has been visited.
     * @return true if the cell is within bounds, matches the specified color, and has not been visited; false otherwise.
     */
    private boolean isSafe(int row, int col, char color, boolean[][] visited) {
        // Ensure the cell is within the board limits
        boolean withinBounds = row >= 0 && row < 8 && col >= 0 && col < 8;

        // Check if the cell can be visited
        if (withinBounds && !visited[row][col] && gameBoard.getPieceAt(row, col) == color) {
            return true;
        }

        return false;
    }
    // Other class members and methods...

    /**
     * Sets the starting player for the game.
     * @param startingPlayer The player who will start the game.
     */
    public void setStartingPlayer(Player startingPlayer) {
        this.currentPlayer = startingPlayer;
        // Determine if it is player 1's turn based on the starting player
        this.isPlayer1Turn = (this.currentPlayer == this.player1);
    }

    /**
     * Retrieves the winner of the current round.
     * @return The player who won the round.
     */
    public Player getRoundWinner() {
        return this.winner;
    }


    /**
     * Displays winner
     */
    public void showRoundWinner() {
        if (winner != null) {
            System.out.println("Round completed. Winner: " + winner.getName());
        } else {
            System.out.println("Round completed. No winner determined.");
        }
    }


    /**
     * Change the current player
     */
    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Determines and sets the starting player based on the result of a coin toss.
     * @param tossResult A boolean indicating the outcome of the coin toss; true if player1 wins.
     * @return The player who won the coin toss and will start the game.
     */
    public Player determineStartingPlayer(boolean tossResult) {
        if (tossResult) {
            // Assuming 'true' means player1 wins the toss
            currentPlayer = player1;
            System.out.println("Player " + player1.getName() + " wins the coin toss and will go first.");
        } else {
            currentPlayer = player2;
            System.out.println("Player " + player2.getName() + " wins the coin toss and will go first.");
        }
        return currentPlayer;
    }
    /**
     * Retrieves the current score of player 1.
     * @return The score of player 1.
     */
    public int getPlayer1Score() {
        return player1Score;
    }
    /**
     * Retrieves the current score of player 2.
     * @return The score of player 2.
     */
    public int getPlayer2Score(){
        return player2Score;
    }
    /**
     * Retrieves the instance of player 1.
     * @return The Player object representing player 1.
     */
    public Player getPlayer1() {
        return player1;
    }
    /**
     * Retrieves the instance of player 2.
     * @return The Player object representing player 2.
     */
    public Player getPlayer2() {
        return player2;
    }




}
