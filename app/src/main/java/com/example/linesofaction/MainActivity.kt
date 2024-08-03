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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        boardState = mutableStateOf(Board())
        roundState = mutableStateOf(null)

        setContent {
            LinesOfActionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameContent(boardState, roundState)
                }
            }
        }
    }
}

@Composable
fun GameContent(boardState: MutableState<Board>, roundState: MutableState<Round?>) {
    var gameState by remember { mutableStateOf(GameState.Menu) }
    var selectedRow by remember { mutableStateOf(-1) }
    var selectedCol by remember { mutableStateOf(-1) }

    when (gameState) {
        GameState.Menu -> MainMenu { option ->
            when (option) {
                GameOption.PlayerVsPlayer -> {
                    val player1 = HumanPlayer("Player 1")
                    val player2 = HumanPlayer("Player 2")
                    roundState.value = Round(player1, player2, null, null, null, null, null, null)
                    gameState = GameState.CoinToss
                }
                GameOption.PlayerVsComputer -> {
                    val player1 = HumanPlayer("Player 1")
                    val player2 = ComputerPlayer("Computer")
                    roundState.value = Round(player1, player2, null, null, null, null, null, null)
                    gameState = GameState.CoinToss
                }
                GameOption.ComputerVsComputer -> {
                    val player1 = ComputerPlayer("Computer 1")
                    val player2 = ComputerPlayer("Computer 2")
                    roundState.value = Round(player1, player2, null, null, null, null, null, null)
                    gameState = GameState.CoinToss
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
            roundState.value?.startGame(startingPlayer, tossResult)
            gameState = GameState.Playing
        }
        GameState.Playing -> GameBoard(
            initialBoard = boardState.value,
            round = roundState.value!!,
            selectedRow = selectedRow,
            selectedCol = selectedCol,
            onCellClicked = { row, col ->
                handleCellClick(row, col, boardState, roundState, { newState -> gameState = newState }, selectedRow, selectedCol)
                selectedRow = -1
                selectedCol = -1
            },
            onRestart = { gameState = GameState.ContinuePlaying },
            onExit = { gameState = GameState.Menu },
            onContinue = { gameState = GameState.ContinuePlaying }
        )
        GameState.GameOver -> GameOverDialog(
            winner = roundState.value?.getRoundWinner()?.getName() ?: "",
            round = roundState.value!!,
            onContinue = { gameState = GameState.ContinuePlaying },
            onRestart = { gameState = GameState.Playing },
            onExit = {
                gameState = GameState.Menu
            }
        )
        GameState.ContinuePlaying -> {
            val newRound = roundState.value?.let {
                Round(it.getPlayer1(), it.getPlayer2(), it.getRoundWinner(), null, it.getPlayer1Score(), it.getPlayer2Score(), Round.getWinsForPlayer1(), Round.getWinsForPlayer2())
            }
            val newBoard = Board().apply { resetBoard() }
            boardState.value = newBoard
            roundState.value = newRound
            selectedRow = -1
            selectedCol = -1
            gameState = GameState.Playing
        }
        GameState.Load -> LoadGameOptions { selectedCase ->
            when (selectedCase) {
                1 -> {
                    val newRound = Round(roundState.value!!.getPlayer1(), roundState.value!!.getPlayer2(), null, 1, null, -2, 0, 1)
                    val newBoard = Board().apply { case1Board() }
                    boardState.value = newBoard
                    roundState.value = newRound
                    selectedRow = -1
                    selectedCol = -1
                    gameState = GameState.Playing
                }
                // Add other cases similarly
            }
            gameState = GameState.Playing
        }
    }
}

fun handleCellClick(row: Int, col: Int, boardState: MutableState<Board>, roundState: MutableState<Round?>, updateGameState: (GameState) -> Unit, selectedRow: Int, selectedCol: Int) {
    var updatedSelectedRow = selectedRow
    var updatedSelectedCol = selectedCol
    if (updatedSelectedRow >= 0 && updatedSelectedCol >= 0) {
        if (roundState.value?.nextMove(updatedSelectedRow, updatedSelectedCol, row, col) == true) {
            updatedSelectedRow = -1
            updatedSelectedCol = -1
            boardState.value = roundState.value!!.gameBoard // Update board state
            if (roundState.value?.checkForRoundCompletion() == true) {
                roundState.value?.showRoundWinner()
                updateGameState(GameState.GameOver)
            } else {
                // Handle the computer's turn
                if (roundState.value?.getCurrentPlayer() is ComputerPlayer) {
                    executeComputerMove(roundState.value!!, boardState)
                }
            }
        }
    } else {
        updatedSelectedRow = row
        updatedSelectedCol = col
    }
}

fun executeComputerMove(round: Round, boardState: MutableState<Board>) {
    val move = (round.getCurrentPlayer() as ComputerPlayer).getNextMove(boardState.value)
    if (move != null) {
        val start = move.getFirst()
        val end = move.getSecond()
        boardState.value.movePiece(start.getFirst(), start.getSecond(), end.getFirst(), end.getSecond(), round.getCurrentPlayer().getPieceType()!!)
        round.switchTurn()
        boardState.value = round.gameBoard // Ensure board state is updated
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
