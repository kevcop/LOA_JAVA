package com.example.linesofaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog

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

enum class GameOption {
    PlayerVsPlayer,
    PlayerVsComputer,
    ComputerVsComputer,
    LoadGame
}

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

@Composable
fun GameBoard(
    boardState: MutableState<Board>,
    round: Round,
    selectedRow: Int,
    selectedCol: Int,
    onCellClicked: (Int, Int) -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit,
    onContinue: () -> Unit,
    onHelpRequested: () -> Unit,
    onSaveAndExit: () -> Unit  // New callback for Save and Exit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display the game board (assuming an 8x8 grid)
        for (row in 0 until 8) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 8) {
                    val piece = boardState.value.getPieceAt(row, col)
                    val isSelected = (row == selectedRow && col == selectedCol)

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .border(1.dp, Color.Black)
                            .background(if (isSelected) Color.Gray else Color.White)
                            .clickable {
                                onCellClicked(row, col)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = piece.toString(), fontSize = 18.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons for various actions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { onRestart() }) {
                Text("Restart")
            }
            Button(onClick = { onExit() }) {
                Text("Exit")
            }
            Button(onClick = { onContinue() }) {
                Text("Continue")
            }
            Button(onClick = { onHelpRequested() }) {
                Text("Help")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add the Save and Exit button at the bottom
        Button(
            onClick = {
                onSaveAndExit()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Save and Exit")
        }
    }
}


@Composable
fun HelpDialog(moves: List<HumanPlayer.MoveDetails>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Possible Moves", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp))
                Spacer(Modifier.height(20.dp))
                LazyColumn {
                    items(moves) { moveDetails ->
                        println("Here are the move details" + moveDetails)

                        val startNotation = properNotation(moveDetails.getStart())
                        val endNotation = properNotation(moveDetails.getEnd())
                        //val startNotation = (moveDetails.getStart())
                        //val endNotation = (moveDetails.getEnd())
                        println("Start notation "+startNotation)
                        println("End notation"+endNotation)
                        Text(text = "$startNotation to $endNotation")
                    }
                }
                Spacer(Modifier.height(20.dp))
                Button(onClick = { onDismiss() }) {
                    Text("Close")
                }
            }
        }
    }
}

/*fun properNotation(position: Rules.Pair<Int, Int>): String {
    val columnLetter = ('A'.code + position.first).toChar()
    val rowNumber = 8 - position.second
    return "$columnLetter$rowNumber"
}*/

fun properNotation(position: Rules.Pair<Int, Int>): String {
    val columnLetter = ('A'.code + position.second).toChar()
    val rowNumber = 8 - position.first
    return "$columnLetter$rowNumber"
}



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
        Button(
            onClick = { onLoadSelected(1) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Case 1")
        }
        Button(
            onClick = { onLoadSelected(2) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Case 2")
        }
        Button(
            onClick = { onLoadSelected(3) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Case 3")
        }
        Button(
            onClick = { onLoadSelected(4) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Case 4")
        }
        Button(
            onClick = { onLoadSelected(5) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Case 5")
        }
    }
}
