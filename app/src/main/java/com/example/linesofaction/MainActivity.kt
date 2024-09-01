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
    private var isNewGame: Boolean by mutableStateOf(true)  // Flag to differentiate between new game and loaded game
    private val moveLog = mutableListOf<String>()  // Initialize moveLog

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
                    GameContent(
                        boardState = boardState,
                        roundState = roundState,
                        possibleMovesState = possibleMovesState,
                        currentPlayer = currentPlayer,
                        moveLog = moveLog,  // Pass moveLog to GameContent
                        activity = this,
                        isNewGame = isNewGame,
                        onGameStart = { isNewGame = it } // Update flag based on game start
                    )
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
    moveLog: MutableList<String>,  // Receive moveLog here
    activity: ComponentActivity,
    isNewGame: Boolean,
    onGameStart: (Boolean) -> Unit // Pass a callback to update the flag
) {
    var gameState by remember { mutableStateOf(GameState.Menu) }
    var selectedRow by remember { mutableStateOf(-1) }
    var selectedCol by remember { mutableStateOf(-1) }
    var showHelpDialog by remember { mutableStateOf(false) }

    when (gameState) {
        GameState.Menu -> MainMenu { option ->
            onGameStart(true) // Set the flag when a new game starts
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
                    currentPlayer.value = player1// Set initial currentPlayer
                }

                GameOption.ComputerVsComputer -> {
                    val player1 = ComputerPlayer("Computer 1")
                    val player2 = ComputerPlayer("Computer 2")
                    roundState.value = Round(player1, player2, null, null, null, null, null, null)
                    gameState = GameState.CoinToss
                    currentPlayer.value = player1  // Set initial currentPlayer
                }

                GameOption.LoadGame -> {
                    onGameStart(false) // Set the flag when a game is loaded
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
            println("Current player is"+currentPlayer.value?.getName())
            gameState = if (isNewGame) GameState.Playing else GameState.ContinuePlaying
        }

        GameState.Playing -> {
            handleGamePlay(
                boardState,
                roundState,
                possibleMovesState,
                currentPlayer,
                { newState -> gameState = newState },
                selectedRow,
                selectedCol,
                { newRow, newCol -> selectedRow = newRow; selectedCol = newCol },
                showHelpDialog,
                { showHelpDialog = it },
                moveLog,  // Pass moveLog to handleGamePlay
                activity
            )
        }

        GameState.ResumePlaying -> {
            handleGamePlay(
                boardState,
                roundState,
                possibleMovesState,
                currentPlayer,
                { newState -> gameState = newState },
                selectedRow,
                selectedCol,
                { newRow, newCol -> selectedRow = newRow; selectedCol = newCol },
                showHelpDialog,
                { showHelpDialog = it },
                moveLog,  // Pass moveLog to handleGamePlay
                activity
            )
        }

        GameState.GameOver -> GameOverDialog(
            winner = roundState.value?.getRoundWinner()?.getName() ?: "",
            round = roundState.value!!,
            onContinue = {
                val winsForPlayer1 = Round.getWinsForPlayer1()
                val winsForPlayer2 = Round.getWinsForPlayer2()

                if (winsForPlayer1 == winsForPlayer2) {
                    println("Rounds are tied. Initiating a coin toss for the next round.")
                    gameState = GameState.CoinToss
                } else {
                    gameState =
                        if (isNewGame) GameState.ContinuePlaying else GameState.ContinuePlaying
                }
            },
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

                println("Before setting values in new Round object:")
                println("Player 1: ${player1.getName()}, Score: ${player1.getScore()}, Rounds Won: ${player1.getRoundsWon()}")
                println("Player 2: ${player2.getName()}, Score: ${player2.getScore()}, Rounds Won: ${player2.getRoundsWon()}")

                player1.setRoundsWon(winsForPlayer1)
                player2.setRoundsWon(winsForPlayer2)
                player1.setScore(player1Score)
                player2.setScore(player2Score)

                println("After setting values in new Round object:")
                println("Player 1: ${player1.getName()}, Score: ${player1.getScore()}, Rounds Won: ${player1.getRoundsWon()}")
                println("Player 2: ${player2.getName()}, Score: ${player2.getScore()}, Rounds Won: ${player2.getRoundsWon()}")

                Round(
                    player1,
                    player2,
                    roundWinner,
                    null,
                    player1Score,
                    player2Score,
                    winsForPlayer1,
                    winsForPlayer2
                )
            }

            val newBoard = Board().apply { resetBoard() }
            boardState.value = newBoard
            roundState.value = newRound
            selectedRow = -1
            selectedCol = -1
            currentPlayer.value =
                newRound?.getCurrentPlayer()  // Update currentPlayer for the new round
            gameState = GameState.Playing
        }

        GameState.Load -> LoadGameOptions { selectedCase ->
            when (selectedCase) {
                1 -> {
                    val loadedRound = Round.loadGameState(activity, "serialization_case1.txt")
                    if (loadedRound != null) {
                        loadedRound.resumeFromLoadedState(loadedRound)
                        boardState.value = loadedRound.getGameBoard()
                        roundState.value = loadedRound
                        selectedRow = -1
                        selectedCol = -1
                        println("Loaded Round Details:")
                        println(
                            "Player 1: ${
                                loadedRound.getPlayer1().getName()
                            } (Piece Type: ${loadedRound.getPlayer1().getPieceType()})"
                        )
                        println(
                            "Player 2: ${
                                loadedRound.getPlayer2().getName()
                            } (Piece Type: ${loadedRound.getPlayer2().getPieceType()})"
                        )
                        println(
                            "Current Player: ${
                                loadedRound.getCurrentPlayer().getName()
                            } (Piece Type: ${loadedRound.getCurrentPlayer().getPieceType()})"
                        )
                        println("Board State After Loading:")

                        for (row in 0 until 8) {
                            var rowRepresentation = ""
                            for (col in 0 until 8) {
                                rowRepresentation += "${
                                    loadedRound.getGameBoard().getPieceAt(row, col)
                                } "
                            }
                            println(rowRepresentation.trim())
                        }
                        currentPlayer.value = loadedRound.getCurrentPlayer()
                        gameState = GameState.ResumePlaying
                    } else {
                        println("Failed to load the game state.")
                        gameState = GameState.Menu
                    }
                }
            }
        }
    }
}


@Composable
fun handleGamePlay(
    boardState: MutableState<Board>,
    roundState: MutableState<Round?>,
    possibleMovesState: MutableState<List<HumanPlayer.MoveDetails>>,
    currentPlayer: MutableState<Player?>,
    updateGameState: (GameState) -> Unit,
    selectedRow: Int,
    selectedCol: Int,
    updateSelected: (Int, Int) -> Unit,
    showHelpDialog: Boolean,
    setShowHelpDialog: (Boolean) -> Unit,
    moveLog: MutableList<String>,  // Receive moveLog here
    activity: ComponentActivity
) {
    if (showHelpDialog) {
        HelpDialog(
            moves = possibleMovesState.value,
            onDismiss = { setShowHelpDialog(false) }
        )
    }
    GameBoard(
        boardState = boardState,
        round = roundState.value!!,
        selectedRow = selectedRow,
        selectedCol = selectedCol,
        moveLog = moveLog,  // Pass moveLog to GameBoard
        onCellClicked = { row, col ->
            handleCellClick(
                row,
                col,
                boardState,
                roundState,
                currentPlayer,
                updateGameState,
                selectedRow,
                selectedCol,
                moveLog  // Pass moveLog to handleCellClick
            ) { newRow, newCol ->
                updateSelected(newRow, newCol)
            }
        },
        onRestart = { updateGameState(GameState.Menu) },
        onExit = { updateGameState(GameState.Menu) },
        onContinue = { updateGameState(GameState.ContinuePlaying) },
        onHelpRequested = {
            val player = currentPlayer.value
            if (player != null && player is HumanPlayer) {
                player.generateAllPossibleMoves1(boardState.value)
                possibleMovesState.value = player.getPossibleMoves1()
            }
            setShowHelpDialog(true)
        },
        onSaveAndExit = {
            roundState.value?.saveGameState(activity, "gameState.txt")
            activity.finish()
        }
    )

    currentPlayer.value?.let { player ->
        if (player is ComputerPlayer) {
            player.generateAllPossibleMoves(boardState.value)
            player.displayPossibleMoves()
            val move = player.getNextMove(boardState.value)
            if (move != null) {
                val start = move.first
                val end = move.second
                val startNotation = properNotation(start)
                val endNotation = properNotation(end)
                println("Computer player is making a move from $startNotation to $endNotation")
                moveLog.add("Computer moved from $startNotation to $endNotation")
                handleCellClick(
                    end.first,
                    end.second,
                    boardState,
                    roundState,
                    currentPlayer,
                    updateGameState,
                    start.first,
                    start.second,
                    moveLog,  // Pass moveLog to handleCellClick
                    { newRow, newCol -> updateSelected(newRow, newCol) }
                )
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
    moveLog: MutableList<String>,  // Receive moveLog here
    updateSelected: (Int, Int) -> Unit
) {
    var updatedSelectedRow = selectedRow
    var updatedSelectedCol = selectedCol

    if (updatedSelectedRow >= 0 && updatedSelectedCol >= 0) {
        if (roundState.value?.nextMove(updatedSelectedRow, updatedSelectedCol, row, col) == true) {
            boardState.value = roundState.value!!.gameBoard
            // Log the move after execution
            moveLog.clear()
            moveLog.addAll(roundState.value?.getMoveHistory() ?: emptyList())
            if (roundState.value?.checkForRoundCompletion() == true) {
                roundState.value?.showRoundWinner()
                updateGameState(GameState.GameOver)
            } else {
                updateSelected(-1, -1)
                roundState.value?.switchTurn()
                currentPlayer.value = roundState.value?.getCurrentPlayer()
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
    ResumePlaying,
    GameOver,
    ContinuePlaying,
    Load,
}
