package com.example.linesofaction;
import com.example.linesofaction.BoardObserver;
import java.util.List;
import java.util.ArrayList;

public class Board {
    private static final int ROWS = 8;
    private static final int COLS = 8;
    private char[][] board = new char[ROWS][COLS];
    private List<BoardObserver> observers = new ArrayList<>();
    private int lastStartRow = -1;
    private int lastStartCol = -1;
    private int lastEndRow = -1;
    private int lastEndCol = -1;
    /**
     *Initializes the board and resets it to starting configuration.
     */
    public Board() {
        initializeBoard();
    }
    /**
     * Adds an observer to the list of observers.
     * @param observer BoardObserver to be added to notifications list.
     */
    public void addObserver(BoardObserver observer) {
        observers.add(observer);
    }

    /**
     * Notifies all registered observers of a change in the board's state.
     */
    private void notifyObservers() {
        for (BoardObserver observer : observers) {
            observer.onBoardChanged(this);
        }
    }
    /**
     * Initializes the board with a default layout and notifies observers.
     */
    public void initializeBoard() {
        resetBoard();
        notifyObservers();
    }
    /**
     * Resets the board to its initial configuration and notifies observers.
     */
    public void resetBoard() {
        // Clear all spaces
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = '.';
            }
        }
        // Place black pieces along the top and bottom rows
        for (int col = 1; col < COLS - 1; col++) {
            board[0][col] = 'B'; // Black pieces on top row
            board[ROWS - 1][col] = 'B'; // Black pieces on bottom row
        }
        // Place white pieces along the left and right columns
        for (int row = 1; row < ROWS - 1; row++) {
            board[row][0] = 'W'; // White pieces on left column
            board[row][COLS - 1] = 'W'; // White pieces on right column
        }
        notifyObservers();
    }

    public void setPieceAt(int row, int col, char piece) {
        if (row >= 0 && row < 8 && col >= 0 && col < 8) {
            board[row][col] = piece;
        } else {
            System.out.println("Attempted to set piece out of bounds: (" + row + "," + col + ")");
        }
    }


    /**
     * Moves a piece from the start position to the end position if the move is valid.
     * @param startRow int representing the starting row index.
     * @param startCol int representing the starting column index.
     * @param endRow int representing the ending row index.
     * @param endCol int representing the ending column index.
     * @param player char representing the player making the move.
     * @return boolean true if the move was successful, false otherwise.
     */
    public boolean movePiece(int startRow, int startCol, int endRow, int endCol, char player) {
            board[endRow][endCol] = board[startRow][startCol];
            board[startRow][startCol] = '.';
            lastStartRow = startRow;
            lastStartCol = startCol;
            lastEndRow = endRow;
            lastEndCol = endCol;
            notifyObservers();
            return true;

    }

    /**
     * Returns the piece present at the specified board location.
     * @param row int specifying the row index.
     * @param col int specifying the column index.
     * @return char representing the piece at the specified location, or ' ' if invalid position.
     */
    public char getPieceAt(int row, int col) {
        return isPositionValid(row, col) ? board[row][col] : ' ';
    }
    /**
     * Validates whether the specified position is within the board bounds.
     * @param row int specifying the row index.
     * @param col int specifying the column index.
     * @return boolean true if the position is valid, false otherwise.
     */
    public boolean isPositionValid(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }
    /**
     * Removes an observer from the list of observers.
     * @param observer BoardObserver to be removed from notifications list.
     */
    public void removeObserver(BoardObserver observer) {
        observers.remove(observer);
    }
    /**
     * Displays the current state of the board on the console.
     */
    public void displayBoard() {
        System.out.println("Current Board State:");
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                System.out.print(board[row][col] + " ");
            }
            System.out.println(); // Move to the next line after printing all columns in a row
        }
    }
    /**
     * Counts the number of black and white pieces on the board.
     * @return int[] where the first element is the count of black pieces and the second is the count of white pieces.
     */
    public int[] countPiecesByColor() {
        int blackCount = 0;
        int whiteCount = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == 'B') {
                    blackCount++;
                } else if (board[row][col] == 'W') {
                    whiteCount++;
                }
            }
        }
        return new int[] {blackCount, whiteCount};
    }
    /**
     * sets up case 1 board
     */
    public void case1Board() {
        // empty board
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = '.';
            }
        }

        board[1][0] = 'W';

        board[2][0] = 'B';
        board[2][1] = 'B';
        board[2][2] = 'B';

        board[3][0] = 'B';
        board[3][2] = 'W';
        board[3][5] = 'B';

        board[4][0] = 'W';
        board[4][3] = 'B';
        board[4][4] = 'B';

        board[5][1] = 'B';
        board[5][2] = 'W';
        board[5][3] = 'B';
        board[5][5] = 'W';
        board[5][6] = 'W';

        board[6][7] = 'W';

        //notify observers
        notifyObservers();
    }

    /**
     * sets up case 2 board
     */
    public void case2Board(){
        //empty board
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = '.';
            }
        }
        board[1][0] = 'W';

        board[2][0] = 'B';
        board[2][1] = 'B';
        board[2][2] = 'B';

        board[3][0] = 'B';
        board[3][2] = 'W';
        board[3][5] = 'B';

        board[4][0] = 'W';
        board[4][3] = 'B';
        board[4][4] = 'B';

        board[5][1] = 'B';
        board[5][2] = 'W';
        board[5][3] = 'B';
        board[5][5] = 'W';
        board[5][6] = 'W';

        board[6][7] = 'W';

        //notify observers
        notifyObservers();
    }
    /**
     * sets up case 3 board
     */
    public void case3Board() {
        // empty board
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = '.';
            }
        }

        board[1][0] = 'W';

        board[2][0] = 'B';
        board[2][1] = 'B';
        board[2][2] = 'B';

        board[3][0] = 'B';
        board[3][2] = 'B';

        board[4][0] = 'W';
        board[4][3] = 'B';
        board[4][4] = 'B';
        board[4][5] = 'W';

        board[5][1] = 'B';
        board[5][2] = 'W';
        board[5][3] = 'B';
        board[5][5] = 'W';
        board[5][6] = 'W';
        //notify observers

        notifyObservers();
    }
    /**
     * sets up case 4 board
     */
    public void case4Board() {
        // empty board
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = '.';
            }
        }
        board[1][0] = 'W';

        board[2][0] = 'B';
        board[2][1] = 'B';
        board[2][2] = 'B';

        board[3][0] = 'B';
        board[3][2] = 'B';

        board[4][0] = 'W';
        board[4][3] = 'B';
        board[4][4] = 'B';
        board[4][5] = 'W';

        board[5][1] = 'B';
        board[5][2] = 'W';
        board[5][3] = 'B';
        board[5][5] = 'W';
        board[5][6] = 'W';

        //notify observers
        notifyObservers();
    }
    /**
     * sets up case 5 board
     */
    public void case5Board() {
        //emptying board
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = '.';
            }
        }

        //setting up pieces according to the specified layout:
        board[0][3] = 'W';

        board[1][0] = 'W';
        board[1][2] = 'B';


        board[2][0] = 'B';
        board[2][1] = 'B';

        board[3][0] = 'B';
        board[3][1] = 'B';
        board[3][5] = 'B';

        board[4][0] = 'W';
        board[4][3] = 'B';
        board[4][4] = 'B';
        board[4][5] = 'W';

        board[5][3] = 'B';
        board[5][5] = 'W';
        board[5][6] = 'W';
        board[5][7] = 'W';
        //notify observers
        notifyObservers();
    }

    public void clearBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = '.';  // Empty spot
            }
        }
    }


}



