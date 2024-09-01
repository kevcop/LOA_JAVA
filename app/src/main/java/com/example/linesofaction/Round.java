package com.example.linesofaction;

import androidx.annotation.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import android.content.Context;
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
    private ComputerPlayer computerPlayer;

    private static int winsForPlayer1 = 0;
    private static int winsForPlayer2 = 0;
    private List<String> moveLog;
    private Map<Character, Integer> colToIndex;
    private Random random;
    private Scanner scanner;
    private Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> lastComputerMove;
    private List<HumanPlayer.MoveDetails> humanPlayerMoves;
    private List<String> moveHistory;


    public Round(Player p1, Player p2, @Nullable Player initialWinner, @Nullable Integer boardSetup, @Nullable Integer Player1Score, @Nullable Integer Player2Score, @Nullable Integer WinsForPlayer1, Integer WinsForPlayer2) {
        player1 = p1;
        player2 = p2;
        currentPlayer = null;
        isPlayer1Turn = true;

        gameBoard = new Board();
        rules = new Rules();

        if (player2 instanceof ComputerPlayer) {
            computerPlayer = (ComputerPlayer) player2;
        }
        this.humanPlayerMoves = new ArrayList<>();
        this.moveHistory = new ArrayList<>();


        moveLog = new ArrayList<>();
        colToIndex = new HashMap<>();
        colToIndex.put('A', 0);
        colToIndex.put('B', 1);
        random = new Random();

        System.out.println("Name of player 1: " + p1.getName());
        System.out.println("Name of player 2: " + p2.getName());
        if (Player1Score != null && Player2Score != null) {
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

        System.out.println("Scores for player1 " + player1Score);
        System.out.println("Scores for player2 " + player2Score);
    }

    public List<HumanPlayer.MoveDetails> getHumanPlayerMoves() {
        return humanPlayerMoves;
    }

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

    public void switchTurn() {
        if (currentPlayer.equals(player1)) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
        System.out.println("It's now " + currentPlayer.getName() + "'s turn.");
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void startGame(@Nullable Player startingPlayer, @Nullable Boolean userWonCoinToss) {
        System.out.println("Starting a new game...");
        if (winner != null) {
            System.out.println("Last round's winner: " + winner.getName() + " (" + winner.getPieceType() + ")");
        } else {
            System.out.println("No winner from the last round.");
        }

        if (userWonCoinToss != null) {
            currentPlayer = userWonCoinToss ? player1 : player2;
            System.out.println("Coin toss decided: " + currentPlayer.getName() + " will start (User choice was " + userWonCoinToss + ")");
        } else if (startingPlayer != null) {
            currentPlayer = startingPlayer;
            System.out.println("Starting player set explicitly: " + currentPlayer.getName());
        } else {
            currentPlayer = coinToss(true) ? player1 : player2;
            System.out.println("No starting player specified, defaulting to coin toss. " + currentPlayer.getName() + " will start.");
        }

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

    public List<HumanPlayer.MoveDetails> getPossibleMovesForCurrentPlayer(Board board) {
        if (currentPlayer instanceof HumanPlayer) {
            HumanPlayer humanPlayer = (HumanPlayer) currentPlayer;
            humanPlayer.generateAllPossibleMoves1(board);
            return humanPlayer.getPossibleMoves1();
        }
        return new ArrayList<>(); // Return an empty list if it's not a human player's turn
    }

    public boolean nextMove(int fromRow, int fromCol, int toRow, int toCol) {
        String fromPosition = properNotation(new Rules.Pair<>(fromRow, fromCol));
        String toPosition = properNotation(new Rules.Pair<>(toRow, toCol));

// Print the debug statement with proper notation
        System.out.println("Attempting to move piece from " + fromPosition + " to " + toPosition);
        //System.out.println("Current board in round: ");
        //gameBoard.displayBoard();
        System.out.println("Attempting to move piece from (" + fromRow + "," + fromCol + ") to (" + toRow + "," + toCol + ")");

        char selectedPiece = gameBoard.getPieceAt(fromRow, fromCol);
        char playerPieceType = currentPlayer.getPieceType();

        if (selectedPiece == '.' ) {
            System.out.println("No piece to move from the selected position (" + fromRow + "," + fromCol + ") ");
            gameBoard.displayBoard();
            return false;
        }

        if (selectedPiece != playerPieceType) {
            System.out.println(" (" + fromRow + "," + fromCol + ") or piece does not belong to player.");
            gameBoard.displayBoard();
            return false;
        }

        if (!rules.isValidMove(gameBoard, currentPlayer, fromRow, fromCol, toRow, toCol)) {
            System.out.println("The move is not valid according to the game rules.");
            return false;
        }

        if (gameBoard.movePiece(fromRow, fromCol, toRow, toCol, playerPieceType)) {
            addMove(fromRow, fromCol, toRow, toCol, playerPieceType);
            System.out.println("Move executed successfully.");
            gameBoard.displayBoard();

            logMove(fromRow, fromCol, toRow, toCol);

            boolean blackConnected = checkConnectedGroup('B');
            boolean whiteConnected = checkConnectedGroup('W');
            if (blackConnected || whiteConnected) {
                determineWinner();
                updateScores();
                System.out.println("Round over! " + winner.getName() + " wins!");
                return true;
            } else {
                switchTurn();
                if (currentPlayer instanceof ComputerPlayer) {
                    ComputerPlayer computerPlayer = (ComputerPlayer) currentPlayer;
                    computerPlayer.generateAllPossibleMoves(gameBoard);
                    computerPlayer.displayPossibleMoves();
                    Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move = computerPlayer.getNextMove(gameBoard);
                    if (move != null) {
                        Pair<Integer, Integer> start = computerPlayer.getMoveStart();
                        Pair<Integer, Integer> end = computerPlayer.getMoveEnd();
                        System.out.println("Computer player is making a move from (" + start.getFirst() + "," + start.getSecond() + ") to (" + end.getFirst() + "," + end.getSecond() + ")");
                        List<Pair<Integer, Integer>> captures = computerPlayer.getCapturesForMove(start, end);
                        if (!captures.isEmpty()) {
                            System.out.print("Captures at: ");
                            for (Pair<Integer, Integer> capture : captures) {
                                System.out.print("(" + capture.getFirst() + "," + capture.getSecond() + ") ");
                            }
                            System.out.println();
                        }

                        gameBoard.movePiece(start.getFirst(), start.getSecond(), end.getFirst(), end.getSecond(), currentPlayer.getPieceType());
                        addMove(start.getFirst(), start.getSecond(), end.getFirst(), end.getSecond(), currentPlayer.getPieceType());
                        showMoveHistory();
                        logMove(start.getFirst(), start.getSecond(), end.getFirst(), end.getSecond());
                        gameBoard.displayBoard();
                    }
                }
            }
        } else {
            System.out.println("Move could not be executed.");
            return false;
        }

        return true;
    }

    public void resumeFromLoadedState(Round loadedRound) {
        // Set the round's state based on the loaded round
        this.player1 = loadedRound.getPlayer1();
        this.player2 = loadedRound.getPlayer2();
        this.currentPlayer = loadedRound.getCurrentPlayer();
        this.gameBoard = loadedRound.getGameBoard();

        // Output current state for debugging
        System.out.println("Resuming game...");
        System.out.println("Current Player: " + currentPlayer.getName() + " (Piece: " + currentPlayer.getPieceType() + ")");
        System.out.println("Player 1: " + player1.getName() + " (Piece: " + player1.getPieceType() + ")");
        System.out.println("Player 1: " + player1.getName() + " (Piece: " + player1.getPieceType() + ")");
        System.out.println("Player 2: " + player2.getName() + " (Piece: " + player2.getPieceType() + ")");
        System.out.println("Board State:");
        //displayBoardState();

        // If the current player is a human, generate possible moves
        if (currentPlayer instanceof HumanPlayer) {
            HumanPlayer humanPlayer = (HumanPlayer) currentPlayer;
            humanPlayer.generateAllPossibleMoves1(gameBoard);  // Generate possible moves
            System.out.println("Possible Moves for Human Player:");
            for (HumanPlayer.MoveDetails move : humanPlayer.getPossibleMoves1()) {
                String startNotation = properNotation(move.getStart());
                String endNotation = properNotation(move.getEnd());
                System.out.println("Move: " + startNotation + " to " + endNotation);
            }
        }

        // If the current player is a computer, generate and make a move
        if (currentPlayer instanceof ComputerPlayer) {
            ComputerPlayer computerPlayer = (ComputerPlayer) currentPlayer;
            computerPlayer.generateAllPossibleMoves(gameBoard);
            Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move = computerPlayer.getNextMove(gameBoard);

            if (move != null) {
                Pair<Integer, Integer> start = move.getFirst();
                Pair<Integer, Integer> end = move.getSecond();


                // Validate the move
                if (validateMove(currentPlayer, start.getFirst(), start.getSecond(), end.getFirst(), end.getSecond())) {
                    addMove(start.getFirst(), start.getSecond(), end.getFirst(), end.getSecond(), currentPlayer.getPieceType());
                    System.out.println("Computer player is making a move from " + properNotation(start) + " to " + properNotation(end));
                    gameBoard.movePiece(start.getFirst(), start.getSecond(), end.getFirst(), end.getSecond(), currentPlayer.getPieceType());

                    logMove(start.getFirst(), start.getSecond(), end.getFirst(), end.getSecond());
                    //displayBoardState();
                    switchTurn();  // Switch the turn after the computer moves
                } else {
                    System.out.println("Invalid move detected. The move will not be executed.");
                }
            } else {
                System.out.println("No valid moves available for the computer.");
            }
        }
    }

    private boolean validateMove(Player player, int fromRow, int fromCol, int toRow, int toCol) {
        // Convert positions to proper notation
        String fromPosition = properNotation(new Rules.Pair<>(fromRow, fromCol));
        String toPosition = properNotation(new Rules.Pair<>(toRow, toCol));

        // Print the debug statement with proper notation
        System.out.println("Attempting to move piece from " + fromPosition + " to " + toPosition);
        System.out.println("Attempting to move piece from (" + fromRow + "," + fromCol + ") to (" + toRow + "," + toCol + ")");

        char selectedPiece = gameBoard.getPieceAt(fromRow, fromCol);
        char playerPieceType = player.getPieceType();

        // Check if there is a piece to move at the selected position
        if (selectedPiece == '.') {
            System.out.println("No piece to move from the selected position (" + fromRow + "," + fromCol + ").");
            gameBoard.displayBoard();  // Show the board state for debugging
            return false;
        }

        // Check if the piece at the selected position belongs to the current player
        if (selectedPiece != playerPieceType) {
            System.out.println("The piece at (" + fromRow + "," + fromCol + ") does not belong to the current player.");
            gameBoard.displayBoard();  // Show the board state for debugging
            return false;
        }

        // Check if the move is valid according to the game rules
        if (!rules.isValidMove(gameBoard, player, fromRow, fromCol, toRow, toCol)) {
            System.out.println("The move is not valid according to the game rules.");
            return false;
        }

        // If all checks pass, the move is valid
        return true;
    }

    private void logMove(int fromRow, int fromCol, int toRow, int toCol) {
        String fromPosition = "[" + fromRow + "," + fromCol + "]";
        String toPosition = "[" + toRow + "," + toCol + "]";
        System.out.println(currentPlayer.getName() + " moves from " + fromPosition + " to " + toPosition);
    }

    private String properNotation(Pair<Integer, Integer> position) {
        char column = (char) ('A' + position.getSecond());
        int row = 8 - position.getFirst();
        return "" + column + row;
    }

    public void saveGameState(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
            out.write("Board:\n");
            for (int row = 0; row < 8; row++) {  // Start from row 0 to row 7
                for (int col = 0; col < 8; col++) {
                    out.write(gameBoard.getPieceAt(row, col) + " ");
                }
                out.newLine();
            }

            out.write("\nHuman:\n");
            out.write("Rounds won: " + player1.getRoundsWon() + "\n");
            out.write("Score: " + player1.getScore() + "\n");

            out.write("\nComputer:\n");
            out.write("Rounds won: " + player2.getRoundsWon() + "\n");
            out.write("Score: " + player2.getScore() + "\n");

            String playerType = (currentPlayer instanceof HumanPlayer) ? "Human" : "Computer";
            out.write("\nNext player: " + playerType + "\n");
            out.write("Color: " + currentPlayer.getPieceType() + "\n");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving game state.");
        }

        System.out.println("Game state saved successfully to " + fileName + ".");
    }
    public static Round loadGameState(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            // Read the board state
            Board board = new Board();
            board.clearBoard();

            String line = in.readLine(); // "Board:" line

            // Now read from top to bottom
            for (int row = 0; row < 8; row++) {
                line = in.readLine();
                String[] pieces = line.split(" ");
                for (int col = 0; col < 8; col++) {
                    char piece = pieces[col].charAt(0);
                    piece = Character.toUpperCase(piece);
                    if (piece == 'X') {
                        piece = '.';
                    }
                    board.setPieceAt(row, col, piece);
                }
            }

            // Initialize players
            HumanPlayer humanPlayer = new HumanPlayer("Human");
            ComputerPlayer computerPlayer = new ComputerPlayer("AI");

            // Load player details
            String nextPlayerType = null;
            String pieceColor = null;
            while ((line = in.readLine()) != null) {
                if (line.contains("Human:")) {
                    line = in.readLine();
                    humanPlayer.setRoundsWon(Integer.parseInt(line.split(": ")[1]));
                    line = in.readLine();
                    humanPlayer.setScore(Integer.parseInt(line.split(": ")[1]));
                } else if (line.contains("Computer:")) {
                    line = in.readLine();
                    computerPlayer.setRoundsWon(Integer.parseInt(line.split(": ")[1]));
                    line = in.readLine();
                    computerPlayer.setScore(Integer.parseInt(line.split(": ")[1]));
                } else if (line.contains("Next player:")) {
                    nextPlayerType = line.split(": ")[1];
                } else if (line.contains("Color:")) {
                    pieceColor = line.split(": ")[1];
                }
            }

            // Set piece types and determine starting player
            char humanPieceType, computerPieceType;
            if ("B".equals(pieceColor)) {
                if ("Human".equals(nextPlayerType)) {
                    humanPieceType = 'B';
                    computerPieceType = 'W';
                } else {
                    humanPieceType = 'W';
                    computerPieceType = 'B';
                }
            } else {
                if ("Human".equals(nextPlayerType)) {
                    humanPieceType = 'W';
                    computerPieceType = 'B';
                } else {
                    humanPieceType = 'B';
                    computerPieceType = 'W';
                }
            }

            humanPlayer.setPieceType(humanPieceType);
            computerPlayer.setPieceType(computerPieceType);

            Player startingPlayer = "Human".equals(nextPlayerType) ? humanPlayer : computerPlayer;

            Round loadedRound = new Round(humanPlayer, computerPlayer, null, null, humanPlayer.getScore(), computerPlayer.getScore(), humanPlayer.getRoundsWon(), computerPlayer.getRoundsWon());
            loadedRound.setBoardState(board);
            loadedRound.setStartingPlayer(startingPlayer);

            System.out.println("Game state loaded from " + fileName + ".");
            return loadedRound;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading game state.");
            return null;
        }
    }





    public void setBoardState(Board board) {
        this.gameBoard = board;
    }

    public void clearBoard() {
        gameBoard.clearBoard();
    }

    public void updateScores() {
        int[] counts = gameBoard.countPiecesByColor();
        int blackCount = counts[0];
        int whiteCount = counts[1];

        int player1Pieces = player1.getPieceType() == 'B' ? blackCount : whiteCount;
        int player2Pieces = player2.getPieceType() == 'B' ? blackCount : whiteCount;

        if (winner == player1) {
            int scoreDifference = player1Pieces - player2Pieces;
            player1Score += scoreDifference;
            System.out.println("Score difference: " + scoreDifference);
        } else if (winner == player2) {
            int scoreDifference = player2Pieces - player1Pieces;
            player2Score += scoreDifference;
            System.out.println("Score difference: " + scoreDifference);
        }
    }

    public void determineWinner() {
        boolean player1Connected = checkConnectedGroup(player1.getPieceType());
        boolean player2Connected = checkConnectedGroup(player2.getPieceType());

        if (player1Connected && !player2Connected) {
            System.out.println(player1.getName() + " wins this round!");
            winner = player1;
            player1.setRoundsWon(player1.getRoundsWon() + 1);
        } else if (!player1Connected && player2Connected) {
            System.out.println(player2.getName() + " wins this round!");
            winner = player2;
            player2.setRoundsWon(player2.getRoundsWon() + 1);
        } else if (player1Connected) {
            System.out.println("Both players seem to have connected groups. Check for a possible error.");
        } else {
            System.out.println("No player has formed a connected group yet.");
        }

        if (winner != null) {
            System.out.println("Current winner: " + winner.getName());
        } else {
            System.out.println("No winner determined at this point.");
        }
    }

    public boolean checkForRoundCompletion() {
        determineWinner();

        if (winner != null) {
            System.out.println(winner.getName() + " has won the game!");
            if (winner.equals(player1)) {
                winsForPlayer1++;
            } else if (winner.equals(player2)) {
                winsForPlayer2++;
            }
            System.out.println("Updated scores: Player 1 (wins: " + winsForPlayer1 + "), Player 2 (wins: " + winsForPlayer2 + ")");
            return true;
        }
        System.out.println("player 1 rounds won: " + player1.getRoundsWon());
        System.out.println("player 2 rounds won: " + player2.getRoundsWon());

        return false;
    }

    static public int getWinsForPlayer1() {
        return winsForPlayer1;
    }

    static public int getWinsForPlayer2() {
        return winsForPlayer2;
    }

    public boolean checkConnectedGroup(char color) {
        int startRow = -1;
        int startCol = -1;
        boolean[][] visited = new boolean[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (gameBoard.getPieceAt(row, col) == color) {
                    startRow = row;
                    startCol = col;
                    break;
                }
            }
            if (startRow != -1) break;
        }

        if (startRow == -1) return false;

        dfs(startRow, startCol, color, visited);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (gameBoard.getPieceAt(row, col) == color && !visited[row][col]) {
                    return false;
                }
            }
        }

        return true;
    }

    private void dfs(int row, int col, char color, boolean[][] visited) {
        int[] rowNbr = {-1, 1, 0, 0, -1, -1, 1, 1};
        int[] colNbr = {0, 0, -1, 1, -1, 1, -1, 1};

        visited[row][col] = true;

        for (int k = 0; k < 8; k++) {
            int newRow = row + rowNbr[k];
            int newCol = col + colNbr[k];

            if (isSafe(newRow, newCol, color, visited)) {
                dfs(newRow, newCol, color, visited);
            }
        }
    }

    private boolean isSafe(int row, int col, char color, boolean[][] visited) {
        boolean withinBounds = row >= 0 && row < 8 && col >= 0 && col < 8;

        if (withinBounds && !visited[row][col] && gameBoard.getPieceAt(row, col) == color) {
            return true;
        }

        return false;
    }

    public void setStartingPlayer(Player startingPlayer) {
        this.currentPlayer = startingPlayer;
        this.isPlayer1Turn = (this.currentPlayer == this.player1);
    }

    public Player getRoundWinner() {
        return this.winner;
    }

    public void showRoundWinner() {
        if (winner != null) {
            System.out.println("Round completed. Winner: " + winner.getName());
        } else {
            System.out.println("Round completed. No winner determined.");
        }
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Player determineStartingPlayer(boolean tossResult) {
        if (tossResult) {
            currentPlayer = player1;
            System.out.println("Player " + player1.getName() + " wins the coin toss and will go first.");
        } else {
            currentPlayer = player2;
            System.out.println("Player " + player2.getName() + " wins the coin toss and will go first.");
        }
        return currentPlayer;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Board getGameBoard() {
        return gameBoard;
    }
    public void addMove(int fromRow, int fromCol, int toRow, int toCol, char playerPieceType) {
        String moveDescription = playerPieceType + " moved from (" + (fromRow + 1) + ", " + (fromCol + 1) + ") to (" + (toRow + 1) + ", " + (toCol + 1) + ")";
        moveHistory.add(moveDescription);
    }

    // Get the move history
    public List<String> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    public void showMoveHistory() {
        System.out.println("Move History:");
        if (moveHistory.isEmpty()) {
            System.out.println("No moves have been made yet.");
        } else {
            for (String move : moveHistory) {
                System.out.println(move);
            }
        }
    }
}
