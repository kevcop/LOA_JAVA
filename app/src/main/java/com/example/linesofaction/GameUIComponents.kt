package com.example.linesofaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.linesofaction.ui.theme.LinesOfActionTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

/**
 * Displays the main menu of the "Lines of Action" game, providing different game mode options.
 * This composable function creates a menu with buttons for each game mode and a load game option.
 * The UI is centered and laid out vertically. Each button, when clicked, will trigger the corresponding game option.
 *
 * @param onOptionSelected A lambda function that takes a [GameOption] and handles the user's selection.
 *                         This function is invoked with the selected game option when a button is clicked.
 */
@Composable
fun MainMenu(onOptionSelected: (GameOption) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Lines of Action", fontSize = 24.sp, modifier = Modifier.padding(bottom = 20.dp))
        Button(
            onClick = { onOptionSelected(GameOption.PlayerVsPlayer) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Player vs Player")
        }
        Button(
            onClick = { onOptionSelected(GameOption.PlayerVsComputer) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Player vs Computer")
        }
        Button(
            onClick = { onOptionSelected(GameOption.ComputerVsComputer) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Computer vs Computer")
        }
        Button(
            onClick = { onOptionSelected(GameOption.LoadGame) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Load Game")
        }
    }
}

// Handles options selected
enum class GameOption {
    PlayerVsPlayer,
    PlayerVsComputer,
    ComputerVsComputer,
    LoadGame
}

/**
 * Displays a dialog for selecting the outcome of a coin toss with two options: "Heads" or "Tails".
 * This composable function renders a dialog window with two buttons for user interaction,
 * allowing the user to choose between heads or tails during a coin toss event.
 *
 * @param onResultSelected A lambda function that takes a Boolean indicating the toss result selected by the user.
 *                         True corresponds to "Heads" and false corresponds to "Tails".
 *                         This function will be invoked with the corresponding Boolean value when a button is clicked.
 */
@Composable
fun CoinTossDialog(onResultSelected: (Boolean) -> Unit) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Coin Toss", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(20.dp))
                Button(onClick = { onResultSelected(true) }) {
                    Text("Heads")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { onResultSelected(false) }) {
                    Text("Tails")
                }
            }
        }
    }
}

/**
 * Renders the game board for the "Lines of Action" game. This composable function displays an 8x8 board
 * with selectable cells for placing or moving pieces. It updates interactively based on user actions and game logic.
 *
 * @param initialBoard The initial state of the game board.
 * @param round The current round of the game being played.
 * @param tournament The tournament context within which the game is being played.
 * @param selectedRow The row index of the currently selected piece on the board.
 * @param selectedCol The column index of the currently selected piece on the board.
 * @param onCellClicked A callback function invoked when a cell on the board is clicked. Takes row and column indices as parameters.
 * @param onRestart A callback function to handle game restart.
 * @param onExit A callback function to exit the current game session.
 * @param onContinue A callback function to continue with the next round or game.
 */
@Composable
fun GameBoard(
    initialBoard: Board,
    round: Round,
    tournament: Tournament,
    selectedRow: Int,
    selectedCol: Int,
    onCellClicked: (Int, Int) -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit,
    onContinue: () -> Unit
) {
    val boardState = remember { mutableStateOf(initialBoard) }
    val moveLog = remember { mutableStateListOf<String>() }

    DisposableEffect(Unit) {
        val observer = object : BoardObserver {
            override fun onBoardChanged(newBoard: Board) {
                boardState.value = newBoard
            }
        }
        boardState.value.addObserver(observer)
        onDispose {
            boardState.value.removeObserver(observer)
        }
    }

    val rows = 8
    val cols = 8
    val gridSize = 32.dp
    val gridPadding = 2.dp
    var selectedRowState by remember { mutableStateOf(selectedRow) }
    var selectedColState by remember { mutableStateOf(selectedCol) }
    var gameEnded by remember { mutableStateOf(false) }
    var winnerName by remember { mutableStateOf("") }
    val numberToLetter = mapOf(
        0 to "A",
        1 to "B",
        2 to "C",
        3 to "D",
        4 to "E",
        5 to "F",
        6 to "G",
        7 to "H"
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color(0xFFCFD8DC))
            .border(BorderStroke(0.5.dp, Color.Gray)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = gridSize.plus(gridPadding.times(10.5f))),
            horizontalArrangement = Arrangement.spacedBy(gridPadding.times(9.99f))
        ) {
        }

        Row {
            Column(modifier = Modifier.padding(end = 2.dp)) {
                for (number in rows downTo 1) {
                    Text(
                        text = number.toString(),
                        modifier = Modifier.size(gridSize),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.End
                    )
                }
            }
            Column {
                for (row in 0 until rows) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(gridPadding),
                        modifier = Modifier.padding(bottom = gridPadding)
                    ) {
                        for (col in 0 until cols) {
                            val piece = boardState.value.getPieceAt(row, col)
                            val backgroundColor = when {
                                selectedRowState == row && selectedColState == col -> Color.Yellow
                                piece == 'W' -> Color.White
                                piece == 'B' -> Color.Black
                                else -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .size(gridSize)
                                    .background(backgroundColor)
                                    .border(BorderStroke(1.dp, Color.Gray))
                            ) {
                                Button(
                                    onClick = {
                                        when {
                                            selectedRowState == -1 && piece != '.' -> {
                                                selectedRowState = row
                                                selectedColState = col
                                            }
                                            selectedRowState != -1 && (selectedRowState != row || selectedColState != col) -> {
                                                moveLog.add("${round.currentPlayer.getName()}'s turn")
                                                if (round.nextMove(selectedRowState, selectedColState, row, col)) {
                                                    if (boardState.value.movePiece(
                                                            selectedRowState,
                                                            selectedColState,
                                                            row,
                                                            col,
                                                            piece
                                                        )
                                                    ) {
                                                        moveLog.add("Moved from (${numberToLetter[selectedColState]}${8 - selectedRowState}) to (${numberToLetter[col]}${8 - row})")
                                                        if (round.checkForRoundCompletion()) {
                                                            gameEnded = true
                                                            winnerName =
                                                                round.getRoundWinner().getName()
                                                        } else if (round.getCurrentPlayer() is ComputerPlayer) {
                                                            val computerMoveStart = (round.getCurrentPlayer() as ComputerPlayer).getMoveStart()
                                                            val computerMoveEnd = (round.getCurrentPlayer() as ComputerPlayer).getMoveEnd()
                                                            if (computerMoveStart != null && computerMoveEnd != null) {
                                                                boardState.value.movePiece(
                                                                    computerMoveStart.getFirst(),
                                                                    computerMoveStart.getSecond(),
                                                                    computerMoveEnd.getFirst(),
                                                                    computerMoveEnd.getSecond(),
                                                                    (round.getCurrentPlayer() as ComputerPlayer).getPieceType()
                                                                )
                                                                moveLog.add("Computer moved from (${numberToLetter[computerMoveStart.getSecond()]}${8 - computerMoveStart.getFirst()}) to (${numberToLetter[computerMoveEnd.getSecond()]}${8 - computerMoveEnd.getFirst()})")
                                                                round.switchTurn()
                                                            }
                                                        }
                                                    }
                                                    selectedRowState = -1
                                                    selectedColState = -1
                                                } else {
                                                    moveLog.add("Invalid move from (${numberToLetter[selectedColState]}${8 - selectedRowState}) to (${numberToLetter[col]}${8 - row})")
                                                    selectedRowState = -1
                                                    selectedColState = -1
                                                }
                                            } else -> {
                                            selectedRowState = -1
                                            selectedColState = -1
                                        }
                                        }
                                    },
                                    modifier = Modifier.matchParentSize(),
                                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                                ) {
                                    Text(
                                        text = if (piece == '.') " " else piece.toString(),
                                        color = if (backgroundColor == Color.Black) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 4.dp, top = gridPadding)
                ) {
                    ('A'..'H').forEach { letter ->
                        Text(
                            text = letter.toString(),
                            modifier = Modifier
                                .width(gridSize)
                                .padding(gridPadding),
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .border(3.dp, Color.Black)
                            .align(Alignment.TopCenter)
                    ) {
                        items(moveLog) { move ->
                            Text(
                                text = move,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(2.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
        if (gameEnded) {
            GameOverDialog(
                winnerName,
                round,
                onContinue = onContinue,
                onRestart = onRestart,
                onExit = onExit
            )
        }
    }
}




/**
 * Displays a dialog at the end of a game round, providing options to continue to the next round, restart the game, or exit to the main menu.
 * This dialog also shows the current game round winner, scores, and rounds won by each player. Additionally, it declares the overall tournament winner if applicable.
 *
 * @param winner The name of the player who won the current round.
 * @param round The current round object, which provides access to player information and scores.
 * @param onContinue A callback function to be invoked when the user chooses to proceed to the next round.
 * @param onRestart A callback function to be invoked when the user chooses to restart the game.
 * @param onExit A callback function to be invoked when the user chooses to exit to the main menu.
 */
@Composable
fun GameOverDialog(
    winner: String,
    round: Round,
    onContinue: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    val player1Wins = Round.getWinsForPlayer1()
    val player2Wins = Round.getWinsForPlayer2()

    Dialog(onDismissRequest = { onExit() }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Game Over",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                )
                Text(
                    "$winner wins the round!",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                )
                Text(
                    "Score - ${round.getPlayer1().getName()}: ${round.getPlayer1Score()}, ${round.getPlayer2().getName()}: ${round.getPlayer2Score()}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                )
                Text(
                    "Rounds won - ${round.getPlayer1().getName()}: $player1Wins, ${round.getPlayer2().getName()}: $player2Wins",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                )
                Spacer(Modifier.height(20.dp))
                Button(onClick = onContinue) {
                    Text("Next Round")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRestart) {
                    Text("Restart Game")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    println("Exiting to menu. Current gameState: Menu")
                    onExit()
                }) {
                    Text("Exit to Main Menu")
                }
                Spacer(Modifier.height(8.dp))
                // Determine the overall tournament winner based on rounds won
                val overallWinner = if (player1Wins > player2Wins) round.getPlayer1().getName()
                else if (player2Wins > player1Wins) round.getPlayer2().getName()
                else "No clear winner"
                Text(
                    "Tournament Winner: $overallWinner",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp)
                )
            }
        }
    }
}

/**
 * Displays a UI with multiple buttons for selecting predefined game configurations or test cases.
 * Each button is labeled with a case number, and clicking a button will trigger the loading of the specified game state.
 *
 * @param onLoadSelected A callback function that takes an integer representing the selected case number.
 *                       This function is invoked with the case number when a button is clicked, allowing the application
 *                       to load the corresponding game state.
 */
@Composable
fun LoadGameOptions(onLoadSelected: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select a case to test",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Button for case 1
        Button(
            onClick = { onLoadSelected(1) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Case 1")
        }

        // Button for case 2
        Button(
            onClick = { onLoadSelected(2) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Case 2")
        }

        // Button for case 3
        Button(
            onClick = { onLoadSelected(3) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Case 3")
        }

        // Button for case 4
        Button(
            onClick = { onLoadSelected(4) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Case 4")
        }

        // Button for case 5
        Button(
            onClick = { onLoadSelected(5) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Case 5")
        }
    }
}
