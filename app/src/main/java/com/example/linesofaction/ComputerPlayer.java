package com.example.linesofaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;
import com.example.linesofaction.Rules.Pair;

public class ComputerPlayer extends Player {
    private Rules rules;
    private ArrayList<MoveDetails> possibleMoves;
    private Random random;
    private Pair<Integer, Integer> moveStart;
    private Pair<Integer, Integer> moveEnd;

    private static Map<Integer, Character> indexToCol = new HashMap<>();
    private static Map<Character, Integer> colToIndex = new HashMap<>();

    static {
        indexToCol.put(0, 'A');
        indexToCol.put(1, 'B');
        indexToCol.put(2, 'C');
        indexToCol.put(3, 'D');
        indexToCol.put(4, 'E');
        indexToCol.put(5, 'F');
        indexToCol.put(6, 'G');
        indexToCol.put(7, 'H');

        colToIndex.put('A', 0);
        colToIndex.put('B', 1);
        colToIndex.put('C', 2);
        colToIndex.put('D', 3);
        colToIndex.put('E', 4);
        colToIndex.put('F', 5);
        colToIndex.put('G', 6);
        colToIndex.put('H', 7);
    }

    public ComputerPlayer(String name) {
        super(name);
        this.rules = new Rules();
        this.possibleMoves = new ArrayList<>();
        this.random = new Random();
    }

    public void generateAllPossibleMoves(Board board) {
        possibleMoves.clear();

        for (int fromRow = 0; fromRow < 8; ++fromRow) {
            for (int fromCol = 0; fromCol < 8; ++fromCol) {
                if (board.getPieceAt(fromRow, fromCol) == this.getPieceType()) {
                    // Horizontal moves
                    int horizontalMovesRequired = rules.countPiecesInLine(board, fromRow, fromCol, 'H');
                    for (int offset = 1; offset <= horizontalMovesRequired; ++offset) {
                        addMoveIfValid(board, fromRow, fromCol, fromRow, fromCol + offset);
                        addMoveIfValid(board, fromRow, fromCol, fromRow, fromCol - offset);
                    }

                    // Vertical moves
                    int verticalMovesRequired = rules.countPiecesInLine(board, fromRow, fromCol, 'V');
                    for (int offset = 1; offset <= verticalMovesRequired; ++offset) {
                        addMoveIfValid(board, fromRow, fromCol, fromRow + offset, fromCol);
                        addMoveIfValid(board, fromRow, fromCol, fromRow - offset, fromCol);
                    }

                    // Diagonal moves (top-left to bottom-right and top-right to bottom-left)
                    int diagonalMoves1 = rules.countPiecesInLine(board, fromRow, fromCol, 'D');
                    for (int offset = 1; offset <= diagonalMoves1; ++offset) {
                        addMoveIfValid(board, fromRow, fromCol, fromRow + offset, fromCol + offset);
                        addMoveIfValid(board, fromRow, fromCol, fromRow - offset, fromCol - offset);
                    }

                    // Diagonal moves (bottom-left to top-right and bottom-right to top-left)
                    int diagonalMoves2 = rules.countPiecesInLine(board, fromRow, fromCol, 'd');
                    for (int offset = 1; offset <= diagonalMoves2; ++offset) {
                        addMoveIfValid(board, fromRow, fromCol, fromRow + offset, fromCol - offset);
                        addMoveIfValid(board, fromRow, fromCol, fromRow - offset, fromCol + offset);
                    }
                }
            }
        }
    }

    private void addMoveIfValid(Board board, int startRow, int startCol, int endRow, int endCol) {
        List<Pair<Integer, Integer>> capturePositions = new ArrayList<>();

        if (board.isPositionValid(endRow, endCol) &&
                rules.isValidMove(board, this, startRow, startCol, endRow, endCol) &&
                rules.isPathClear(board, startRow, startCol, endRow, endCol, this.getPieceType(), capturePositions) &&
                rules.isValidEndingPosition(board, endRow, endCol, this.getPieceType())) {

            MoveDetails move = new MoveDetails(startRow, startCol, endRow, endCol);
            for (Pair<Integer, Integer> capture : capturePositions) {
                move.addCapture(capture.getFirst(), capture.getSecond());
            }

            possibleMoves.add(move);
        }
    }

    public void displayPossibleMoves() {
        System.out.println("Possible Moves:");
        for (MoveDetails moveDetails : possibleMoves) {
            Pair<Integer, Integer> start = moveDetails.getStart();
            Pair<Integer, Integer> end = moveDetails.getEnd();
            String startNotation = properNotation(start);
            String endNotation = properNotation(end);
            System.out.print(startNotation + " to " + endNotation);
            if (!moveDetails.getCaptures().isEmpty()) {
                System.out.print(" with captures at: ");
                for (Pair<Integer, Integer> capture : moveDetails.getCaptures()) {
                    System.out.print(properNotation(capture) + " ");
                }
            }
            System.out.println();
        }
    }

    public Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> getNextMove(Board board) {
        if (possibleMoves.isEmpty()) {
            generateAllPossibleMoves(board);
        }

        // Filter to capture moves
        List<MoveDetails> captureMoves = new ArrayList<>();
        for (MoveDetails move : possibleMoves) {
            if (!move.getCaptures().isEmpty()) {
                captureMoves.add(move);
            }
        }

        // If there are capture moves, prioritize them
        MoveDetails selectedMove;
        if (!captureMoves.isEmpty()) {
            selectedMove = captureMoves.get(random.nextInt(captureMoves.size()));
        } else {
            selectedMove = possibleMoves.get(random.nextInt(possibleMoves.size()));
        }

        moveStart = selectedMove.getStart();
        moveEnd = selectedMove.getEnd();
        return new Pair<>(moveStart, moveEnd);
    }

    public Pair<Integer, Integer> getMoveStart() {
        return moveStart;
    }

    public Pair<Integer, Integer> getMoveEnd() {
        return moveEnd;
    }

    public List<Pair<Integer, Integer>> getCapturesForMove(Pair<Integer, Integer> start, Pair<Integer, Integer> end) {
        List<Pair<Integer, Integer>> capturePositions = new ArrayList<>();
        // Implement logic to find capture positions based on the game's rules and the start and end positions
        // This is just a placeholder implementation
        return capturePositions;
    }

    public String properNotation(Pair<Integer, Integer> position) {
        char columnLetter = indexToCol.get(position.getSecond());
        int rowNumber = 8 - position.getFirst();  // Adjusted for 1-based row numbering from 8
        return "" + columnLetter + rowNumber;
    }

    public static class MoveDetails {
        private Pair<Integer, Integer> start;
        private Pair<Integer, Integer> end;
        private List<Pair<Integer, Integer>> captures;

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

        public List<Pair<Integer, Integer>> getCaptures() {
            return captures;
        }

        public void addCapture(int row, int col) {
            captures.add(new Pair<>(row, col));
        }
    }
}
