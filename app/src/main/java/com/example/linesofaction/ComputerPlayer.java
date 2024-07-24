package com.example.linesofaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;
import com.example.linesofaction.Rules.Pair;


public class ComputerPlayer extends Player {

    Rules rules;
    private ArrayList<MoveDetails> possibleMoves = new ArrayList<>();

    private static Map<Integer, Character> indexToCol = new HashMap<>();

    static {
        indexToCol.put(0, 'A');
        indexToCol.put(1, 'B');
        indexToCol.put(2, 'C');
        indexToCol.put(3, 'D');
        indexToCol.put(4, 'E');
        indexToCol.put(5, 'F');
        indexToCol.put(6, 'G');
        indexToCol.put(7, 'H');
    }


    public ComputerPlayer(String name) {
        super(name);
    }

    @Override
    public void askPlayer() {
        this.playerName = ("AI");
    }


    public String properNotation(Pair<Integer, Integer> position) {
        char columnLetter = indexToCol.get(position.getSecond());
        int rowNumber = position.getFirst() + 1;
        return "" + columnLetter + rowNumber;
    }

    public void generateAllPossibleMoves(Board board) {
        possibleMoves.clear(); // Clear the list of possible moves at the start

        // Iterate through each position on the board to find possible moves for the computer's pieces
        for (int fromRow = 0; fromRow < 8; ++fromRow) {
            for (int fromCol = 0; fromCol < 8; ++fromCol) {
                // Check if the position has a computer piece
                if (board.getPieceAt(fromRow, fromCol) == this.getPieceType()) {
                    // Generate valid horizontal moves
                    int horizontalMovesRequired = rules.countPiecesInLine(board, fromRow, fromCol, 'H');
                    for (int offset = 1; offset <= horizontalMovesRequired; ++offset) {
                        // Adding moves when moving right
                        addMoveIfValid(board, fromRow, fromCol, fromRow, fromCol + offset);
                        // Adding moves when moving left
                        addMoveIfValid(board, fromRow, fromCol, fromRow, fromCol - offset);
                    }

                    // Generate vertical moves
                    int verticalMovesRequired = rules.countPiecesInLine(board, fromRow, fromCol, 'V');
                    for (int offset = 1; offset <= verticalMovesRequired; ++offset) {
                        // Add moves when moving down
                        addMoveIfValid(board, fromRow, fromCol, fromRow + offset, fromCol);
                        // Add moves when moving up
                        addMoveIfValid(board, fromRow, fromCol, fromRow - offset, fromCol);
                    }
                }
            }
        }
    }

    private void addMoveIfValid(Board board, int startRow, int startCol, int endRow, int endCol) {
        // List to hold capture positions
        List<Pair<Integer, Integer>> capturePositions = new ArrayList<>();

        // Check if move is valid based on the rules class validations
        if (board.isPositionValid(endRow, endCol) &&
                rules.isValidMove(board, this, startRow, startCol, endRow, endCol) &&
                rules.isPathClear(board, startRow, startCol, endRow, endCol, this.getPieceType(), capturePositions) &&
                rules.isValidEndingPosition(board, endRow, endCol, this.getPieceType())) {

            // Creates a MoveDetails object with starting position, ending position, and any capture positions
            MoveDetails move = new MoveDetails(startRow, startCol, endRow, endCol);
            for (Pair<Integer, Integer> capture : capturePositions) {
                move.addCapture(capture.getFirst(), capture.getSecond());
            }

            // Add valid move to the selection
            possibleMoves.add(move);
        }
    }

    public void displayPossibleMoves() {
        // Mainly used for debugging
        System.out.println("Possible Moves:");
        // Iterate through possibleMoves list to display moves
        for (MoveDetails moveDetails : possibleMoves) {
            Pair<Integer, Integer> start = moveDetails.getStart();
            Pair<Integer, Integer> end = moveDetails.getEnd();
            String startNotation = properNotation(start.getFirst(), start.getSecond());
            String endNotation = properNotation(end.getFirst(), end.getSecond());
            System.out.print(startNotation + " to " + endNotation);
            if (!moveDetails.getCaptures().isEmpty()) {
                System.out.print(" with captures at: ");
                for (Pair<Integer, Integer> capture : moveDetails.getCaptures()) {
                    System.out.print(properNotation(capture.getFirst(), capture.getSecond()) + " ");
                }
            }
            System.out.println(); // Move to the next line after printing all details of a move
        }
    }

    public Pair<String, String> selectAndExecuteMove(Board board, char playerPieceType) {
        // Conditional check for empty moves
        if (possibleMoves.isEmpty()) {
            System.out.println("No possible moves to select from.");
            return null; // Java equivalent of returning an empty pair is returning null
        }

        // Randomly select a move from the list of possible moves
        Random rand = new Random(); // Java's way to generate random numbers
        int moveIndex = rand.nextInt(possibleMoves.size()); // Generate a random index based on size of list
        MoveDetails selectedMove = possibleMoves.get(moveIndex);

        // Extract the start and end positions from the selected move
        int startRow = selectedMove.getStart().getFirst();
        int startCol = selectedMove.getStart().getSecond();
        int endRow = selectedMove.getEnd().getFirst();
        int endCol = selectedMove.getEnd().getSecond();

        // Convert start and end positions to chess notation for output
        String startNotation = properNotation(startRow, startCol);
        String endNotation = properNotation(endRow, endCol);

        // Attempt to execute the move
        if (board.movePiece(startRow, startCol, endRow, endCol, playerPieceType)) {
            System.out.println("Move executed: " + startNotation + " to " + endNotation);

            // Process potential captures
            if (!selectedMove.getCaptures().isEmpty()) {
                for (Pair<Integer, Integer> capture : selectedMove.getCaptures()) {
                    System.out.println("Captured piece at " + properNotation(capture.getFirst(), capture.getSecond()));
                }
            }
            // Return move details for logging or further use
            return new Pair<>(startNotation, endNotation);
        } else {
            // Indicate move failed
            System.out.println("Failed to execute move: " + startNotation + " to " + endNotation);
            return null;
        }
    }


    public class Move {
        private int startRow, startCol;
        private int endRow, endCol;

        public Move(int startRow, int startCol, int endRow, int endCol) {
            this.startRow = startRow;
            this.startCol = startCol;
            this.endRow = endRow;
            this.endCol = endCol;
        }

        // Getters
        public int getStartRow() { return startRow; }
        public int getStartCol() { return startCol; }
        public int getEndRow() { return endRow; }
        public int getEndCol() { return endCol; }
    }



    public class MoveDetails {
        private Pair<Integer, Integer> start;
        private Pair<Integer, Integer> end;
        private ArrayList<Pair<Integer, Integer>> captures;

        public MoveDetails(int startRow, int startCol, int endRow, int endCol) {
            this.start = new Pair<>(startRow, startCol);
            this.end = new Pair<>(endRow, endCol);
            this.captures = new ArrayList<>();
        }

        public Pair<Integer, Integer> getStart() {
            return start;
        }

        public Pair<Integer, Integer> getEnd() {
            return end;
        }

        public ArrayList<Pair<Integer, Integer>> getCaptures() {
            return captures;
        }

        public void addCapture(int row, int col) {
            captures.add(new Pair<>(row, col));
        }
    }


}
