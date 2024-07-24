package com.example.linesofaction;
import java.util.Scanner;
import com.example.linesofaction.Rules.Pair;

public abstract class Player {
    protected String playerName;
    protected String name;

    protected int pieceRow;
    protected int pieceCol;
    protected int destinationRow;
    protected int destinationCol;
    protected char pieceType;
    protected boolean wonLastRound = false;
    protected int roundsWon = 0;
    protected int score = 0;

    public static final Scanner scanner = new Scanner(System.in);
    protected boolean isComputer;


    /**
     * Default constructor for creating a Player object with default settings.
     */
    public Player() {
    }

    /**
     * Constructs a Player with a specific name.
     * @param name String representing the name of the player.
     */
    public Player(String name) {
        playerName= name;
    }

    /**
     * Abstract method to prompt the player for move input.
     */
    public abstract void askPlayer();

    /**
     * Selects a piece on the board to move.
     * @param row int representing the row index of the piece.
     * @param col int representing the column index of the piece.
     */
    public void selectPiece(int row, int col) {
        this.pieceRow = row;
        this.pieceCol = col;
    }

    /**
     * Sets the desired destination for the piece's move.
     * @param row int representing the row index of the destination.
     * @param col int representing the column index of the destination.
     */
    public void setDestination(int row, int col) {
        this.destinationRow = row;
        this.destinationCol = col;
    }

     /**
     * Retrieves the selected piece's position.
     * @return int[] array containing row and column indexes of the selected piece.
     */
    public int[] getSelectedPiece() {
        return new int[]{ pieceRow, pieceCol };
    }

    /**
     * Retrieves the destination for the piece's move.
     * @return int[] array containing row and column indexes of the move's destination.
     */
    public int[] getDestination() {
        return new int[]{ destinationRow, destinationCol };
    }

    /**
     * Converts row and column positions to a chess-like notation.
     * @param row int specifying the row index.
     * @param col int specifying the column index.
     * @return String representing the position in chess notation (e.g., "A1").
     */
    public String properNotation(int row, int col) {
        return "" + (char)('A' + col) + (row + 1);
    }

    /**
     * Retrieves the player's name.
     * @return String representing the player's name.
     */
    public String getName() {
        return playerName;
    }

    /**
     * Sets the type of pieces the player uses (e.g., 'B' for black, 'W' for white).
     * @param type char representing the type of the pieces.
     */
    public void setPieceType(char type) {
        this.pieceType = type;
    }

    /**
     * Retrieves the type of pieces the player uses.
     * @return char representing the type of the pieces.
     */
    public char getPieceType() {
        return pieceType;
    }

    /**
     * Sets whether the player won the last round.
     * @param won boolean representing if the player won the last round.
     */
    public void setWonLastRound(boolean won) {
        this.wonLastRound = won;
    }

    /**
     * Checks if the player won the last round.
     * @return boolean true if the player won the last round, otherwise false.
     */
    public boolean getWonLastRound() {
        return wonLastRound;
    }

    /**
     * Sets the total number of rounds won by the player.
     * @param newRoundsWon int representing the total number of rounds won.
     */
    public void setRoundsWon(int newRoundsWon) {
        this.roundsWon = newRoundsWon;
    }


    // Retrieves the total number of rounds won by the player.
    public int getRoundsWon() {
        return roundsWon;
    }

    /**
     * Gets the column of the selected piece as an integer (0-7).
     * @return The row index of the selected piece.
     */
    public int getSelectedPieceRow() {
        return this.pieceRow;
    }

    /**
     * Gets the column of the selected piece as an integer (0-7).
     * @return The column index of the selected piece.
     */
    public int getSelectedPieceCol() {
        return this.pieceCol; // Assuming pieceCol is an integer representing columns 0-7
    }

    /**
     * Gets the row of the move destination as a character ('A'-'H').
     * @return The row character of the destination.
     */
    public int getDestinationRow() {
        return this.destinationRow; // Assuming destinationRow is a char
    }

    /**
     * Gets the column of the move destination as an integer (0-7).
     * @return The column index of the destination.
     */
    public int getDestinationCol() {
        return this.destinationCol; // Assuming destinationCol is an integer
    }


}
