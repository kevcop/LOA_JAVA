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
    private lateinit var tournament: Tournament

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val board = Board()
        boardState = mutableStateOf(board)

        setContent {
            LinesOfActionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameContent(boardState)
                }
            }
        }
    }
}

@Composable
fun GameContent(board: MutableState<Board>) {
    var gameState by remember { mutableStateOf(GameState.Menu) }
    var selectedRow by remember { mutableStateOf(-1) }
    var selectedCol by remember { mutableStateOf(-1) }
    var recomposeTrigger by remember { mutableStateOf(false) }
    val round = remember { mutableStateOf<Round?>(null) }
    val tournament = remember { mutableStateOf<Tournament?>(null) }

    when (gameState) {
        GameState.Menu -> MainMenu { option ->
            when (option) {
                GameOption.PlayerVsPlayer -> {
                    val player1 = HumanPlayer("Player 1")
                    val player2 = HumanPlayer("Player 2")
                    round.value = Round(player1, player2, null, null, null, null, null, null)
                    tournament.value = Tournament(player1, player2)
                    gameState = GameState.CoinToss
                }
                GameOption.PlayerVsComputer -> {
                    val player1 = HumanPlayer("Player 1")
                    val player2 = ComputerPlayer("Computer")
                    round.value = Round(player1, player2, null, null, null, null, null, null)
                    tournament.value = Tournament(player1, player2)
                    gameState = GameState.CoinToss
                }
                GameOption.ComputerVsComputer -> {
                    val player1 = ComputerPlayer("Computer 1")
                    val player2 = ComputerPlayer("Computer 2")
                    round.value = Round(player1, player2, null, null, null, null, null, null)
                    tournament.value = Tournament(player1, player2)
                    gameState = GameState.CoinToss
                }
                GameOption.LoadGame -> {
                    gameState = GameState.Load
                }
                else -> throw IllegalStateException("Unhandled game option")
            }
        }
        GameState.CoinToss -> CoinTossDialog { tossResult ->
            val startingPlayer = round.value?.determineStartingPlayer(tossResult)
            round.value?.setCurrentPlayer(startingPlayer)
            round.value?.startGame(startingPlayer, tossResult)
            gameState = GameState.Playing
        }
        GameState.Playing -> GameBoard(
            initialBoard = board.value,
            round = round.value!!,
            tournament = tournament.value!!,
            selectedRow = selectedRow,
            selectedCol = selectedCol,
            onCellClicked = { row, col ->
                if (selectedRow >= 0 && selectedCol >= 0) {
                    if (round.value?.nextMove(selectedRow, selectedCol, row, col) == true) {
                        recomposeTrigger = !recomposeTrigger
                        selectedRow = -1
                        selectedCol = -1
                        if (round.value?.checkForRoundCompletion() == true) {
                            round.value?.showRoundWinner()
                            val winner = round.value?.getRoundWinner()
                            if (winner != null) {
                                tournament.value?.recordWin(winner)
                            }
                            gameState = GameState.GameOver
                        }
                    }
                } else {
                    selectedRow = row
                    selectedCol = col
                }
            },
            onRestart = { gameState = GameState.ContinuePlaying },
            onExit = { gameState = GameState.Menu },
            onContinue = { gameState = GameState.ContinuePlaying }
        )
        GameState.GameOver -> GameOverDialog(
            winner = round.value?.getRoundWinner()?.getName() ?: "",
            round = round.value!!,
            onContinue = { gameState = GameState.ContinuePlaying },
            onRestart = { gameState = GameState.Playing },
            onExit = {
                println("Exiting to menu. Current gameState: $gameState")
                println(
                    "Tournament Results: ${
                        tournament.value?.determineOverallWinner()?.getName() ?: "No clear winner"
                    }"
                )
                gameState = GameState.Menu
                recomposeTrigger = !recomposeTrigger
            }
        )
        GameState.ContinuePlaying -> {
            val newRound = round.value?.let {
                Round(it.getPlayer1(), it.getPlayer2(), it.getRoundWinner(), null, it.getPlayer1Score(), it.getPlayer2Score(), Round.getWinsForPlayer1(), Round.getWinsForPlayer2())
            }
            val newBoard = Board().apply { resetBoard() }
            board.value = newBoard
            round.value = newRound
            selectedRow = -1
            selectedCol = -1
            recomposeTrigger = !recomposeTrigger
            gameState = GameState.Playing
        }
        GameState.Load -> LoadGameOptions { selectedCase ->
            when (selectedCase) {
                1 -> {
                    val newRound = Round(round.value!!.getPlayer1(), round.value!!.getPlayer2(), null, 1, null, -2, 0, 1)
                    val newBoard = Board().apply { case1Board() }
                    board.value = newBoard
                    round.value = newRound
                    selectedRow = -1
                    selectedCol = -1
                    recomposeTrigger = !recomposeTrigger
                    gameState = GameState.Playing
                }
                // Add other cases similarly
            }
            gameState = GameState.Playing
        }
    }
}

private enum class GameState {
    Menu,
    CoinToss,
    Playing,
    GameOver,
    ContinuePlaying,
    Load,
}

