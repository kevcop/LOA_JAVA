package com.example.linesofaction;
import java.util.Scanner;

public class HumanPlayer extends Player {
    /**
     * Default constructor for HumanPlayer that initializes a player with default settings.
     */
    public HumanPlayer() {
        super();
    }
    /**
     * Constructs a HumanPlayer with a specified name.
     * @param name The name of the player.
     */
    public HumanPlayer(String name) {
        super(name);
    }
    /**
     * Prompts the player to enter their name and sets it.
     */
    @Override
    public void askPlayer() {
        System.out.print("Enter your name: ");
        this.playerName = scanner.nextLine();
    }
    /**
     * Selects a piece on the board to be moved, specified by its row and column indexes.
     * @param row The row index of the piece to select.
     * @param col The column index of the piece to select.
     */
    @Override
    public void selectPiece(int row, int col) {
        this.pieceRow = row;
        this.pieceCol = col;
    }
    /**
     * Sets the destination for the selected piece, specified by row and column indexes.
     * @param row The row index of the destination.
     * @param col The column index of the destination.
     */
    @Override
    public void setDestination(int row, int col) {
        this.destinationRow = row;
        this.destinationCol = col;
    }
    /**
     * Retrieves the currently selected piece's location as an array of row and column indexes.
     * @return An int array with two elements, [row, col], representing the position of the selected piece.
     */
    @Override
    public int[] getSelectedPiece() {
        return new int[]{this.pieceRow, this.pieceCol};
    }
    /**
     * Retrieves the destination set for the piece's move as an array of row and column indexes.
     * @return An int array with two elements, [row, col], representing the destination of the move.
     */
    @Override
    public int[] getDestination() {
        return new int[]{this.destinationRow, this.destinationCol};
    }
    /**
     * Converts row and column positions to a chess-like notation for easier readability and traditional game notation.
     * @param row The row index of the position.
     * @param col The column index of the position.
     * @return A string representing the position in chess notation (e.g., "A1").
     */
    @Override
    public String properNotation(int row, int col) {
        // Convert column index to a character starting from 'A' and row index to a 1-based value
        return "" + (char)('A' + col) + (row + 1);
    }
}
