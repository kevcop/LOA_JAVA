package com.example.linesofaction

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.example.linesofaction.ui.theme.LinesOfActionTheme

class MainActivity : ComponentActivity() {
    private lateinit var boardState: MutableState<Board>
    private lateinit var roundState: MutableState<Round?>
    private lateinit var possibleMovesState: MutableState<List<HumanPlayer.MoveDetails>>
    private lateinit var currentPlayer: MutableState<Player?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boardState = mutableStateOf(Board())
        roundState = mutableStateOf(null)
        possibleMovesState = mutableStateOf(emptyList())
        currentPlayer = mutableStateOf(null)  // Initialize currentPlayer

        setContent {
            LinesOfActionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameContent(boardState, roundState, possibleMovesState, currentPlayer, this)
                }
            }
        }
    }
}

@Composable
fun GameContent(
    boardState: MutableState<Board>,
    roundState: MutableState<Round?>,
    possibleMovesState: MutableState<List<HumanPlayer.MoveDetails>>,
    currentPlayer: MutableState<Player?>,
    activity: ComponentActivity
) {
    var gameState by remember { mutableStateOf(GameState.Menu) }
    var selectedRow by remember { mutableStateOf(-1) }
    var selectedCol by remember { mutableStateOf(-1) }
    var showHelpDialog by remember { mutableStateOf(false) }

    when (gameState) {
        GameState.Menu -> MainMenu { option ->
            when (option) {
                GameOption.PlayerVsPlayer -> {
                    val player1 = HumanPlayer("Player 1")
                    val player2 = HumanPlayer("Player 2")
                    roundState.value = Round(player1, player2, null, null, null, null, null, null)
                    gameState = GameState.CoinToss
                    currentPlayer.value = player1  // Set initial currentPlayer
                }
                GameOption.PlayerVsComputer -> {
                    val player1 = HumanPlayer("Player 1")
                    val player2 = ComputerPlayer("Computer")
                    roundState.value = Round(player1, player2, null, null, null, null, null, null)
                    gameState = GameState.CoinToss
                    currentPlayer.value = player1  // Set initial currentPlayer
                }
                GameOption.ComputerVsComputer -> {
                    val player1 = ComputerPlayer("Computer 1")
                    val player2 = ComputerPlayer("Computer 2")
                    roundState.value = Round(player1, player2, null, null, null, null, null, null)
                    gameState = GameState.CoinToss
                    currentPlayer.value = player1  // Set initial currentPlayer
                }
                GameOption.LoadGame -> {
                    gameState = GameState.Load
                }
                else -> throw IllegalStateException("Unhandled game option")
            }
        }
        GameState.CoinToss -> CoinTossDialog { tossResult ->
            val startingPlayer = roundState.value?.determineStartingPlayer(tossResult)
            roundState.value?.setCurrentPlayer(startingPlayer)
            currentPlayer.value = startingPlayer  // Update currentPlayer after toss
            roundState.value?.startGame(startingPlayer, tossResult)
            gameState = GameState.Playing
        }
        GameState.Playing -> {
            if (showHelpDialog) {
                HelpDialog(
                    moves = possibleMovesState.value,
                    onDismiss = { showHelpDialog = false }
                )
            }
            GameBoard(
                boardState = boardState,
                round = roundState.value!!,
                selectedRow = selectedRow,
                selectedCol = selectedCol,
                onCellClicked = { row, col ->
                    handleCellClick(row, col, boardState, roundState, currentPlayer, { newState -> gameState = newState }, selectedRow, selectedCol) { newRow, newCol ->
                        selectedRow = newRow
                        selectedCol = newCol
                    }
                },
                onRestart = { gameState = GameState.Menu },
                onExit = { gameState = GameState.Menu },
                onContinue = { gameState = GameState.ContinuePlaying },
                onHelpRequested = {
                    val player = currentPlayer.value

                    println("Current Player: ${player?.getName()} (Type: ${player?.javaClass?.name})")

                    if (player != null) {
                        println("Current Board State:")
                        for (row in 0 until 8) {
                            var rowRepresentation = ""
                            for (col in 0 until 8) {
                                rowRepresentation += "${boardState.value.getPieceAt(row, col)} "
                            }
                            println(rowRepresentation.trim())
                        }

                        // Generate moves for white pieces and store in round
                        if (player is HumanPlayer) {
                            player.generateAllPossibleMoves1(boardState.value) // Generate all possible moves for the human player
                            possibleMovesState.value = player.getPossibleMoves1() // Set the possible moves to the state
                        } else {
                            println("Current player is not a HumanPlayer, cannot generate moves.")
                        }

                        // Log the generated moves
                        println("Possible Moves for ${player.getName()}:")
                        for (move in possibleMovesState.value) {
                            val startNotation = properNotation(move.getStart())
                            val endNotation = properNotation(move.getEnd())
                            println("Move: $startNotation to $endNotation")
                            if (move.getCaptures().isNotEmpty()) {
                                print("Captures: ")
                                for (capture in move.getCaptures()) {
                                    print("${properNotation(capture)} ")
                                }
                                println() // Newline after printing captures
                            }
                        }

                        // Show the Help dialog
                        showHelpDialog = true
                    } else {
                        println("Help requested but the current player is not set.")
                    }
                },
                onSaveAndExit = {
                    roundState.value?.saveGameState(activity, "gameState.txt") // Pass the context and file name
                    activity.finish() // Exit the app after saving the game state
                }
            )
        }
        GameState.GameOver -> GameOverDialog(
            winner = roundState.value?.getRoundWinner()?.getName() ?: "",
            round = roundState.value!!,
            onContinue = { gameState = GameState.ContinuePlaying },
            onRestart = { gameState = GameState.Menu },
            onExit = {
                gameState = GameState.Menu
            }
        )
        GameState.ContinuePlaying -> {
            val newRound = roundState.value?.let {
                val player1 = it.getPlayer1()
                val player2 = it.getPlayer2()
                val roundWinner = it.getRoundWinner()
                val player1Score = it.getPlayer1Score()
                val player2Score = it.getPlayer2Score()
                val winsForPlayer1 = Round.getWinsForPlayer1()
                val winsForPlayer2 = Round.getWinsForPlayer2()

                // Add debug statements to log the attributes before setting them
                println("Before setting values in new Round object:")
                println("Player 1: ${player1.getName()}, Score: ${player1.getScore()}, Rounds Won: ${player1.getRoundsWon()}")
                println("Player 2: ${player2.getName()}, Score: ${player2.getScore()}, Rounds Won: ${player2.getRoundsWon()}")

                // Set the rounds won for each player
                player1.setRoundsWon(winsForPlayer1)
                player2.setRoundsWon(winsForPlayer2)

                // Set the scores for each player
                player1.setScore(player1Score)
                player2.setScore(player2Score)

                // Debug statements after setting values
                println("After setting values in new Round object:")
                println("Player 1: ${player1.getName()}, Score: ${player1.getScore()}, Rounds Won: ${player1.getRoundsWon()}")
                println("Player 2: ${player2.getName()}, Score: ${player2.getScore()}, Rounds Won: ${player2.getRoundsWon()}")

                // Initialize the new Round object
                Round(player1, player2, roundWinner, null, player1Score, player2Score, winsForPlayer1, winsForPlayer2)
            }

            val newBoard = Board().apply { resetBoard() }
            boardState.value = newBoard
            roundState.value = newRound
            selectedRow = -1
            selectedCol = -1
            currentPlayer.value = newRound?.getCurrentPlayer()  // Update currentPlayer for the new round
            gameState = GameState.Playing
        }
        GameState.Load -> LoadGameOptions { selectedCase ->
            when (selectedCase) {

                1 -> {
                    val loadedRound = Round.loadGameState(activity, "serialization_case1.txt") // Use the Round class to call the loadGameState function
                    if (loadedRound != null) {
                        boardState.value = loadedRound.getGameBoard() // Update the board state with the loaded board
                        roundState.value = loadedRound // Set the roundState to the loaded round
                        selectedRow = -1
                        selectedCol = -1
                        println("Loaded Round Details:")
                        println("Player 1: ${loadedRound.getPlayer1().getName()} (Piece Type: ${loadedRound.getPlayer1().getPieceType()})")
                        println("Player 2: ${loadedRound.getPlayer2().getName()} (Piece Type: ${loadedRound.getPlayer2().getPieceType()})")
                        println("Current Player: ${loadedRound.getCurrentPlayer().getName()} (Piece Type: ${loadedRound.getCurrentPlayer().getPieceType()})")
                        println("Board State After Loading:")

                        for (row in 0 until 8) {
                            var rowRepresentation = ""
                            for (col in 0 until 8) {
                                rowRepresentation += "${loadedRound.getGameBoard().getPieceAt(row, col)} "
                            }
                            println(rowRepresentation.trim())
                        }
                        currentPlayer.value = loadedRound.getCurrentPlayer()  // Update currentPlayer with the loaded current player
                        gameState = GameState.Playing
                    } else {
                        // Handle the case where loading the game state failed
                        println("Failed to load the game state.")
                        // Optionally, reset the game state to the main menu or show an error dialog
                        gameState = GameState.Menu
                    }
                }
            }
        }
    }
}

fun handleCellClick(
    row: Int,
    col: Int,
    boardState: MutableState<Board>,
    roundState: MutableState<Round?>,
    currentPlayer: MutableState<Player?>,
    updateGameState: (GameState) -> Unit,
    selectedRow: Int,
    selectedCol: Int,
    updateSelected: (Int, Int) -> Unit
) {
    var updatedSelectedRow = selectedRow
    var updatedSelectedCol = selectedCol

    if (updatedSelectedRow >= 0 && updatedSelectedCol >= 0) {
        if (roundState.value?.nextMove(updatedSelectedRow, updatedSelectedCol, row, col) == true) {
            boardState.value = roundState.value!!.gameBoard // Update board state
            if (roundState.value?.checkForRoundCompletion() == true) {
                roundState.value?.showRoundWinner()
                updateGameState(GameState.GameOver)
            } else {
                updateSelected(-1, -1)
                roundState.value?.switchTurn()
                currentPlayer.value = roundState.value?.getCurrentPlayer()  // Update currentPlayer after the move
            }
        } else {
            updateSelected(row, col)
        }
    } else {
        updateSelected(row, col)
    }
}

public enum class GameState {
    Menu,
    CoinToss,
    Playing,
    GameOver,
    ContinuePlaying,
    Load,
}