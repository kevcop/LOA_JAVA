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
    private ComputerPlayer computerPlayer;

    private static int winsForPlayer1 = 0;
    private static int winsForPlayer2 = 0;
    private List<String> moveLog;
    private Map<Character, Integer> colToIndex;
    private Random random;
    private Scanner scanner;
    private Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> lastComputerMove;

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

    public boolean nextMove(int fromRow, int fromCol, int toRow, int toCol) {
        System.out.println("Attempting to move piece from (" + fromRow + "," + fromCol + ") to (" + toRow + "," + toCol + ")");

        char selectedPiece = gameBoard.getPieceAt(fromRow, fromCol);
        char playerPieceType = currentPlayer.getPieceType();

        System.out.println("Current player piece type: " + playerPieceType);

        if (selectedPiece == '.' || selectedPiece != playerPieceType) {
            System.out.println("No piece to move from the selected position (" + fromRow + "," + fromCol + ") or piece does not belong to player.");
            gameBoard.displayBoard();
            return false;
        }

        if (!rules.isValidMove(gameBoard, currentPlayer, fromRow, fromCol, toRow, toCol)) {
            System.out.println("The move is not valid according to the game rules.");
            return false;
        }

        if (gameBoard.movePiece(fromRow, fromCol, toRow, toCol, playerPieceType)) {
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
                        logMove(start.getFirst(), start.getSecond(), end.getFirst(), end.getSecond());
                        gameBoard.displayBoard();
                        //currentPlayer = player1;
                        // Switch turn to human player after computer move
                        //switchTurn();
                    }
                }
                //System.out.println("It is now " + currentPlayer.getName() + "'s turn.");
            }
        } else {
            System.out.println("Move could not be executed.");
            return false;
        }

        return true;
    }

    private void logMove(int fromRow, int fromCol, int toRow, int toCol) {
        String fromPosition = "[" + fromRow + "," + fromCol + "]";
        String toPosition = "[" + toRow + "," + toCol + "]";
        System.out.println(currentPlayer.getName() + " moves from " + fromPosition + " to " + toPosition);
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
}


