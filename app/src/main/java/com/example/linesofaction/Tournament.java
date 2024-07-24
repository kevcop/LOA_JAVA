package com.example.linesofaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tournament {
    private Player player1;
    private Player player2;
    private Player startingPlayer;
    private int currentRoundNumber;
    private List<Integer> scores;

    private int roundsWonByPlayer1 = 0;
    private int roundsWonByPlayer2 = 0;

    private List<Round> rounds;
    private Player currentStartingPlayer;

    /**
     * Initializes a new tournament with two players.
     * @param p1 Player 1 participating in the tournament.
     * @param p2 Player 2 participating in the tournament.
     */
    public Tournament(Player p1, Player p2) {
        player1 = p1;
        player2 = p2;
        startingPlayer = null;
        currentRoundNumber = 0;
        scores = new ArrayList<>();
        scores.add(0);  // Player1's score
        scores.add(0);
    }

    /**
     * Starts the tournament and manages rounds until the tournament ends.
     */
    public void startTournament() {
        Player previousRoundWinner = null;
        boolean continuePlaying = true;
        while (continuePlaying) {
            Round round = new Round(player1, player2,null,null,null,null,null,null);
            if (previousRoundWinner != null) {
                round.setStartingPlayer(previousRoundWinner);
            } else {
                boolean tossResult = round.coinToss(true); // Example: true might be hardcoded or captured from the user
                round.determineStartingPlayer(tossResult);
            }
            // Start the game with initial settings
            round.startGame(round.getCurrentPlayer(), null);

            // Game loop for a round
            boolean roundComplete = false;
            while (!roundComplete) {
                // Simulate player moves; here you'd need actual move coordinates
                //roundComplete = round.nextMove(/* fromRow, fromCol, toRow, toCol */); // Fill these based on your game input mechanisms
            }

            if (round.getRoundWinner() != null) {
                previousRoundWinner = round.getRoundWinner(); // Set the winner as the starting player for next round
            }

            continuePlaying = askToContinue(); // This method should handle user input to continue or end the tournament
        }
        declareWinner();
    }

    /**
     * Creates and starts a new round based on the previous round's winner.
     * @param previousRoundWinner The winner of the previous round.
     * @return The new round started.
     */
    public Round createAndStartNewRound(Player previousRoundWinner) {
        currentRoundNumber++;
        Player startingPlayer;

        if (previousRoundWinner == null) {
            // If it's the first round, determine the starting player by a coin toss
            startingPlayer = coinToss();  // You might already have a method for this
        } else {
            // For subsequent rounds, the previous round winner starts
            startingPlayer = previousRoundWinner;
        }

        System.out.println("Starting round " + currentRoundNumber + " with " + startingPlayer.getName() + " as the starting player.");
        Round newRound = new Round(player1, player2,null,null,null,null,null,null);
        newRound.startGame(startingPlayer, null); // Start game with the determined starting player
        return newRound;
    }
    /**
     * Conducts a coin toss to decide the starting player.
     * @return The player who wins the coin toss.
     */
    private Player coinToss() {
        // Implement the coin toss logic here, or refer to an existing method that does this
        // Return player1 or player2 based on the coin toss result
        return Math.random() < 0.5 ? player1 : player2;
    }



    /**
     * Updates the scores based on the winner of a round.
     * @param roundWinner The player who won the round.
     */
    private void updateScores(Player roundWinner) {
        if (roundWinner == player1) {
            scores.set(0, scores.get(0) + 1);
            System.out.println(player1.getName() + " wins the round with " + scores.get(0) + " points!");
        } else if (roundWinner == player2) {
            scores.set(1, scores.get(1) + 1);
            System.out.println(player2.getName() + " wins the round with " + scores.get(1) + " points!");
        } else {
            System.out.println("The round ends in a draw.");
        }
        displayScores();
    }
    /**
     * Displays the current scores of both players.
     */
    private void displayScores() {
        System.out.println("Scores after round " + currentRoundNumber + ":");
        System.out.println(player1.getName() + ": " + scores.get(0));
        System.out.println(player2.getName() + ": " + scores.get(1));
    }
    /**
     * Declares the overall winner of the tournament based on rounds won.
     */
    private void declareWinner() {
        System.out.println("Tournament over!");
        if (player1.getRoundsWon() > player2.getRoundsWon()) {
            System.out.println(player1.getName() + " wins the tournament with " + player1.getRoundsWon() + " rounds won!");
        } else if (player2.getRoundsWon() > player1.getRoundsWon()) {
            System.out.println(player2.getName() + " wins the tournament with " + player2.getRoundsWon() + " rounds won!");
        } else {
            System.out.println("The tournament ends in a draw!");
        }
    }
    /**
     * Asks the user if they want to continue playing another round.
     * @return true if the user chooses to continue, otherwise false.
     */
    public boolean askToContinue() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Would you like to play another round? (Y/N): ");
        String response = scanner.nextLine();
        return response.equalsIgnoreCase("Y");
    }

    /**
     * Initiates and plays the next round of the tournament. If there are previous rounds, the winner of the last round starts the next one.
     * If there are no previous rounds, the currentStartingPlayer is used as the starting player.
     */
    public void playNextRound() {
        Player winner = currentStartingPlayer;
        if (!rounds.isEmpty()) {
            winner = rounds.get(rounds.size() - 1).getRoundWinner();
        }
        Round newRound = new Round(player1, player2,null,null,null,null,null,null);
        newRound.startGame(winner, null); // null could represent no coin toss needed
        rounds.add(newRound);
    }
    /**
     * Records the win for a player by incrementing their win count.
     * @param winner The player who won the round.
     */
    public void recordWin(Player winner) {
        if (winner.equals(player1)) {
            roundsWonByPlayer1++;
        } else if (winner.equals(player2)) {
            roundsWonByPlayer2++;
        }
    }
    /**
     * Determines the overall winner of the tournament based on the number of rounds won by each player.
     * @return The player who won the most rounds or null if there is a tie.
     */
    public Player determineOverallWinner() {
        if (roundsWonByPlayer1 > roundsWonByPlayer2) {
            return player1;
        } else if (roundsWonByPlayer2 > roundsWonByPlayer1) {
            return player2;
        } else {
            return null;
        }
    }
}
