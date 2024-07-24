package com.example.linesofaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Rules {
    /**
     * Determines if the path from the start position to the destination is clear of other pieces.
     * @param board The game board.
     * @param startRow Starting row of the piece.
     * @param startCol Starting column of the piece.
     * @param endRow Destination row of the piece.
     * @param endCol Destination column of the piece.
     * @param playerPieceType The type of the player's piece.
     * @param capturePositions A list to hold any positions that could be captured.
     * @return true if the path is clear, otherwise false.
     */
/*    public boolean isPathClear(Board board, int startRow, int startCol, int endRow, int endCol, char playerPieceType, List<Pair<Integer, Integer>> capturePositions) {
        int dRow = Integer.compare(endRow, startRow);
        int dCol = Integer.compare(endCol, startCol);

        int currentRow = startRow + dRow;
        int currentCol = startCol + dCol;
        char pieceAtDestination = board.getPieceAt(endRow, endCol);

        while (currentRow != endRow || currentCol != endCol) {
            char pieceAtCurrent = board.getPieceAt(currentRow, currentCol);
            if (pieceAtCurrent != '.' && pieceAtCurrent != playerPieceType) {
                return false; // Path is blocked
            }
            currentRow += dRow;
            currentCol += dCol;
        }

        if (pieceAtDestination != '.' && pieceAtDestination != playerPieceType) {
            capturePositions.add(new Pair<>(endRow, endCol));
            return true;
        } else if (pieceAtDestination == playerPieceType) {
            return false; // Destination blocked by player's own piece
        }
        return true; // Path is clear
    }*/

    public boolean isPathClear(Board board, int startRow, int startCol, int endRow, int endCol, char playerPieceType, List<Pair<Integer, Integer>> capturePositions) {
        // Calculate the direction increments for rows and columns
        int dRow = Integer.compare(endRow, startRow);
        int dCol = Integer.compare(endCol, startCol);

        // Initialize current position to the next step from the start
        int currentRow = startRow + dRow;
        int currentCol = startCol + dCol;
        char pieceAtDestination = board.getPieceAt(endRow, endCol);

        // Traverse the path from start to end position
        while (currentRow != endRow || currentCol != endCol) {
            char pieceAtCurrent = board.getPieceAt(currentRow, currentCol);
            if (pieceAtCurrent != '.' && pieceAtCurrent != playerPieceType) {
                return false; // Path is blocked by an opponent's piece
            }
            currentRow += dRow;
            currentCol += dCol;
        }

        // Check the piece at the destination
        if (pieceAtDestination != '.' && pieceAtDestination != playerPieceType) {
            capturePositions.add(new Pair<>(endRow, endCol));
            return true; // Path is clear, and an opponent's piece can be captured
        } else if (pieceAtDestination == playerPieceType) {
            return false; // Path is blocked by player's own piece
        }
        return true; // Path is clear
    }



    /**
     * Determines if the ending position of a move is valid.
     * @param board The game board.
     * @param endRow Destination row of the piece.
     * @param endCol Destination column of the piece.
     * @param pieceType The type of the piece being moved.
     * @return true if the ending position is valid, otherwise false.
     */
    public boolean isValidEndingPosition(Board board, int endRow, int endCol, char pieceType) {
        char endPosPiece = board.getPieceAt(endRow, endCol);
        return endPosPiece == '.' || endPosPiece != pieceType;
    }
    /**
     * Validates if a move from a start to an end position is legal according to game rules.
     * @param board The game board.
     * @param player The player making the move.
     * @param startRow Starting row of the piece.
     * @param startCol Starting column of the piece.
     * @param endRow Destination row of the piece.
     * @param endCol Destination column of the piece.
     * @return true if the move is valid, otherwise false.
     */
    public boolean isValidMove(Board board, Player player, int startRow, int startCol, int endRow, int endCol) {
        char pieceAtStart = board.getPieceAt(startRow, startCol);

        // Verify that the piece at the start position belongs to the player and is within bounds
        if (pieceAtStart == '.' || Character.toUpperCase(pieceAtStart) != Character.toUpperCase(player.getPieceType())) {
            System.out.println("Debug: No piece to move or piece does not belong to player.");
            return false;
        }

        // Check if the move is within board bounds and in a straight line
        if (!board.isPositionValid(startRow, startCol) || !board.isPositionValid(endRow, endCol)) {
            System.out.println("Debug: Move is out of board bounds.");
            return false;
        }
        /*boolean isStraightLine = (startRow == endRow) || (startCol == endCol) || (Math.abs(startRow - endRow) == Math.abs(startCol - endCol));
        if (!isStraightLine) {
            System.out.println("Debug: Move is not in a straight line.");
            return false;
        }*/

        // Ensure the path between start and end positions is clear
        List<Pair<Integer, Integer>> tempCaptures = new ArrayList<>();
        if (!isPathClear(board, startRow, startCol, endRow, endCol, player.getPieceType(), tempCaptures)) {
            System.out.println("Debug: Path is not clear for the move.");
            return false;
        }

        // Calculate the amount of pieces along the path
        int piecesInLine;
        if (startRow == endRow) {
            piecesInLine = countPiecesInLine(board, startRow, startCol, 'H');
        } else if (startCol == endCol) {
            piecesInLine = countPiecesInLine(board, startRow, startCol, 'V');
        } else {
            char direction = determineDiagonalDirection(startRow, startCol, endRow, endCol);
            //piecesInLine = countDiagonalPieces(board, startRow, startCol, direction);
            piecesInLine = countDiagonalPieces(board, startRow, startCol, endRow, endCol);
            System.out.println("The amount of pieces counted on the line: "+piecesInLine);

        }

        int moveDistance = Math.max(Math.abs(startRow - endRow), Math.abs(startCol - endCol));
        //piecesInLine = piecesInLine+1;
        System.out.println("Debug: Move Distance: " + moveDistance + ", Pieces in Line: " + piecesInLine);

        // Check if desired move distance is equal to the number of pieces along the line
        if (moveDistance != piecesInLine) {
            System.out.println("Debug: Invalid move: Move distance does not match the total pieces in line.");
            return false;
        }

        // Check for potential capturing
        char pieceAtDestination = board.getPieceAt(endRow, endCol);
        if ((pieceAtDestination != '.' && pieceAtDestination != player.getPieceType() && moveDistance > piecesInLine) ||
                (pieceAtDestination == '.' && moveDistance != piecesInLine)) {
            System.out.println("Debug: Conditions for capturing or non-capturing moves not met.");
            return false;
        }

        return isValidEndingPosition(board, endRow, endCol, player.getPieceType());
    }
    /**
     * Counts the number of pieces in a line from a start position either horizontally or vertically.
     * @param board The game board.
     * @param startRow Starting row index.
     * @param startCol Starting column index.
     * @param direction Direction to count ('H' for horizontal, 'V' for vertical).
     * @return The number of pieces in the specified line.
     */
    public int countPiecesInLine(Board board, int startRow, int startCol, char direction) {
        int count = 0;
        int delta = (direction == 'H') ? 1 : 0; // Horizontal or vertical counting

        // Forward direction
        for (int i = 1; i < 8; i++) {
            int currentRow = startRow + ((direction == 'V') ? i : 0);
            int currentCol = startCol + ((direction == 'H') ? i : 0);
            if (!board.isPositionValid(currentRow, currentCol)) break;
            if (board.getPieceAt(currentRow, currentCol) != '.') count++;
        }

        // Backward direction
        for (int i = 1; i < 8; i++) {
            int currentRow = startRow - ((direction == 'V') ? i : 0);
            int currentCol = startCol - ((direction == 'H') ? i : 0);
            if (!board.isPositionValid(currentRow, currentCol)) break;
            if (board.getPieceAt(currentRow, currentCol) != '.') count++;
        }

        return count;
    }
    /**
     * Counts the number of pieces on a diagonal from a starting position.
     * @param board The game board.
     * @param startRow Starting row index.
     * @param startCol Starting column index.
     //* @param direction Direction to count pieces ('N' northeast, 'W' northwest, 'E' southeast, 'S' southwest).
     * @return The total number of pieces on the diagonal.
     */
    //LATEST WORKING VERSION, DOES NOT ACCOUNT FOR DIRECTIONS PROPERLY
/*    public int countDiagonalPieces(Board board, int startRow, int startCol, int endRow, int endCol) {
        int SIZE = 8; // Assuming an 8x8 board
        String[] columnNotation = {"A", "B", "C", "D", "E", "F", "G", "H"};

        // Find the starting point of the diagonal
        int startRowDiagonal = startRow;
        int startColDiagonal = startCol;
        while (startRowDiagonal > 0 && startColDiagonal > 0) {
            startRowDiagonal--;
            startColDiagonal--;
        }

        // Find the ending point of the diagonal
        int endRowDiagonal = startRow;
        int endColDiagonal = startCol;
        while (endRowDiagonal < SIZE - 1 && endColDiagonal < SIZE - 1) {
            endRowDiagonal++;
            endColDiagonal++;
        }

        // Debug statements for starting and ending positions using game-style notation
        String startPosition = columnNotation[startColDiagonal] + (SIZE - startRowDiagonal);
        String endPosition = columnNotation[endColDiagonal] + (SIZE - endRowDiagonal);
        System.out.println("Debug: Starting position of the diagonal: " + startPosition);
        System.out.println("Debug: Ending position of the diagonal: " + endPosition);

        // Count pieces along the diagonal from start to end
        int totalDiagonalCount = 0;
        int currentRow = startRowDiagonal;
        int currentCol = startColDiagonal;

        while (currentRow <= endRowDiagonal && currentCol <= endColDiagonal) {
            if (board.getPieceAt(currentRow, currentCol) != '.') {
                totalDiagonalCount++;
            }
            currentRow++;
            currentCol++;
        }

        return totalDiagonalCount;
    }*/

    public int countDiagonalPieces(Board board, int startRow, int startCol, int endRow, int endCol) {
        int SIZE = 8; // Assuming an 8x8 board
        String[] columnNotation = {"A", "B", "C", "D", "E", "F", "G", "H"};

        // Determine the direction increments
        int dRow = Integer.compare(endRow, startRow);
        int dCol = Integer.compare(endCol, startCol);

        // Find the starting point of the diagonal
        int startRowDiagonal = startRow;
        int startColDiagonal = startCol;
        while (startRowDiagonal > 0 && startColDiagonal > 0) {
            startRowDiagonal--;
            startColDiagonal--;
        }
        while (startRowDiagonal < SIZE - 1 && startColDiagonal < SIZE - 1) {
            startRowDiagonal++;
            startColDiagonal++;
        }

        // Find the ending point of the diagonal
        int endRowDiagonal = startRow;
        int endColDiagonal = startCol;
        while (endRowDiagonal < SIZE - 1 && endColDiagonal < SIZE - 1) {
            endRowDiagonal++;
            endColDiagonal++;
        }
        while (endRowDiagonal > 0 && endColDiagonal > 0) {
            endRowDiagonal--;
            endColDiagonal--;
        }

        // Adjust based on the move direction
        if (dRow > 0 && dCol > 0) { // Bottom-left to top-right
            startRowDiagonal = startRow;
            startColDiagonal = startCol;
            while (startRowDiagonal > 0 && startColDiagonal > 0) {
                startRowDiagonal--;
                startColDiagonal--;
            }
            endRowDiagonal = startRow;
            endColDiagonal = startCol;
            while (endRowDiagonal < SIZE - 1 && endColDiagonal < SIZE - 1) {
                endRowDiagonal++;
                endColDiagonal++;
            }
        } else if (dRow > 0 && dCol < 0) { // Bottom-right to top-left
            startRowDiagonal = startRow;
            startColDiagonal = startCol;
            while (startRowDiagonal > 0 && startColDiagonal < SIZE - 1) {
                startRowDiagonal--;
                startColDiagonal++;
            }
            endRowDiagonal = startRow;
            endColDiagonal = startCol;
            while (endRowDiagonal < SIZE - 1 && endColDiagonal > 0) {
                endRowDiagonal++;
                endColDiagonal--;
            }
        } else if (dRow < 0 && dCol > 0) { // Top-left to bottom-right
            startRowDiagonal = startRow;
            startColDiagonal = startCol;
            while (startRowDiagonal < SIZE - 1 && startColDiagonal > 0) {
                startRowDiagonal++;
                startColDiagonal--;
            }
            endRowDiagonal = startRow;
            endColDiagonal = startCol;
            while (endRowDiagonal > 0 && endColDiagonal < SIZE - 1) {
                endRowDiagonal--;
                endColDiagonal++;
            }
        } else if (dRow < 0 && dCol < 0) { // Top-right to bottom-left
            startRowDiagonal = startRow;
            startColDiagonal = startCol;
            while (startRowDiagonal < SIZE - 1 && startColDiagonal < SIZE - 1) {
                startRowDiagonal++;
                startColDiagonal++;
            }
            endRowDiagonal = startRow;
            endColDiagonal = startCol;
            while (endRowDiagonal > 0 && endColDiagonal > 0) {
                endRowDiagonal--;
                endColDiagonal--;
            }
        }

        // Debug statements for starting and ending positions using game-style notation
        String startPosition = columnNotation[startColDiagonal] + (SIZE - startRowDiagonal);
        String endPosition = columnNotation[endColDiagonal] + (SIZE - endRowDiagonal);
        System.out.println("Debug: Starting position of the diagonal: " + startPosition);
        System.out.println("Debug: Ending position of the diagonal: " + endPosition);

        // Count pieces along the diagonal from start to end
        int totalDiagonalCount = 0;
        int currentRow = startRowDiagonal;
        int currentCol = startColDiagonal;

        while ((dRow > 0 && currentRow <= endRowDiagonal) || (dRow < 0 && currentRow >= endRowDiagonal) ||
                (dCol > 0 && currentCol <= endColDiagonal) || (dCol < 0 && currentCol >= endColDiagonal)) {
            if (board.getPieceAt(currentRow, currentCol) != '.') {
                totalDiagonalCount++;
            }
            currentRow += dRow;
            currentCol += dCol;
        }

        return totalDiagonalCount;
    }




/*
    public int countDiagonalPieces(Board board, int startRow, int startCol, char direction) {
        int totalDiagonalCount = 1; // Start by counting the selected piece itself
        int SIZE = 8; // Assuming an 8x8 board

        int dRow = (direction == 'N' || direction == 'E') ? -1 : 1;
        int dCol = (direction == 'E' || direction == 'W') ? 1 : -1;

        // Forward direction
        for (int i = 1; i < SIZE; i++) {
            int newRow = startRow + i * dRow;
            int newCol = startCol + i * dCol;
            if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE) {
                if (board.getPieceAt(newRow, newCol) != '.') {
                    totalDiagonalCount++;
                }
            } else {
                break; // Stop if out of bounds
            }
        }

        // Backward direction
        for (int i = 1; i < SIZE; i++) {
            int newRow = startRow - i * dRow;
            int newCol = startCol - i * dCol;
            if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE) {
                if (board.getPieceAt(newRow, newCol) != '.') {
                    totalDiagonalCount++;
                }
            } else {
                break; // Stop if out of bounds
            }
        }

        return totalDiagonalCount;
    }*/

  /*  public int countDiagonalPieces(Board board, int startRow, int startCol, char direction) {
        int totalDiagonalCount = 0;
        int SIZE = 8; // Since we're working with an 8x8 board

        // Determine the direction increments for row and column based on the specified direction
        int dRow = (direction == 'N' || direction == 'E') ? 1 : -1;
        int dCol = (direction == 'E' || direction == 'W') ? 1 : -1;

        if (direction == 'W' || direction == 'E') { // Adjust for west and east directions
            dCol *= -1;
        }

        // Count pieces in the positive direction
        for (int i = 1; i < SIZE; i++) {
            int newRow = startRow + i * dRow;
            int newCol = startCol + i * dCol;
            if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE) {
                if (board.getPieceAt(newRow, newCol) != '.') {
                    totalDiagonalCount++;
                }
            } else {
                // Stop count if out of bounds
                break;
            }
        }

        // Count pieces in the negative direction, excluding the starting piece itself
        for (int i = 1; i < SIZE; i++) {
            int newRow = startRow - i * dRow;
            int newCol = startCol - i * dCol;
            if (newRow >= 0 && newRow < SIZE && newCol >= 0 && newCol < SIZE) {
                if (board.getPieceAt(newRow, newCol) != '.') {
                    totalDiagonalCount++;
                }
            } else {
                // Stop count if out of bounds
                break;
            }
        }

        // Include the starting piece in the total count
        return totalDiagonalCount; // Ensure to count the starting piece as well
    }*/
    /**
     * Determines the direction of a diagonal based on start and end positions.
     * @param startRow Starting row index.
     * @param startCol Starting column index.
     * @param endRow Ending row index.
     * @param endCol Ending column index.
     * @return A character representing the diagonal direction ('N', 'W', 'E', 'S').
     */
    /*public char determineDiagonalDirection(int startRow, int startCol, int endRow, int endCol) {
        int rowDiff = endRow - startRow;
        int colDiff = endCol - startCol;

        // Identify diagonal direction based on the row and column differences
        if (rowDiff > 0 && colDiff > 0) {
            System.out.println("Moving Northeast");
            return 'N'; // Northeast
        } else if (rowDiff > 0 && colDiff < 0) {
            System.out.println("Moving Northwest");
            return 'W'; // Northwest
        } else if (rowDiff < 0 && colDiff > 0) {
            System.out.println("Moving Southeast");
            return 'E'; // Southeast
        } else if (rowDiff < 0 && colDiff < 0) {
            System.out.println("Moving Southwest");
            return 'S'; // Southwest
        } else {
            System.out.println("Not a diagonal move");
            return '0'; // Not a diagonal move
        }
    }*/

    public char determineDiagonalDirection(int startRow, int startCol, int endRow, int endCol) {
        int rowDiff = endRow - startRow;
        int colDiff = endCol - startCol;

        // Identify diagonal direction based on the row and column differences
        if (rowDiff < 0 && colDiff > 0) {
            System.out.println("Moving Northeast");
            return 'N'; // Northeast
        } else if (rowDiff < 0 && colDiff < 0) {
            System.out.println("Moving Northwest");
            return 'W'; // Northwest
        } else if (rowDiff > 0 && colDiff > 0) {
            System.out.println("Moving Southeast");
            return 'E'; // Southeast
        } else if (rowDiff > 0 && colDiff < 0) {
            System.out.println("Moving Southwest");
            return 'S'; // Southwest
        } else {
            System.out.println("Not a diagonal move");
            return '0'; // Not a diagonal move
        }
    }


    /**
     * A generic helper class to hold a pair of values.
     * @param <T1> The type of the first value.
     * @param <T2> The type of the second value.
     */
    public static class Pair<T1, T2> {
        private T1 first;
        private T2 second;

        public Pair(T1 first, T2 second) {
            this.first = first;
            this.second = second;
        }

        public T1 getFirst() {
            return first;
        }

        public T2 getSecond() {
            return second;
        }

        public void setFirst(T1 first) {
            this.first = first;
        }

        public void setSecond(T2 second) {
            this.second = second;
        }
    }
}

