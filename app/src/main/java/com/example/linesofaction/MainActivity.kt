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
import android.util.Log;
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

/**
 * MainActivity is the primary activity for the Lines of Action game application.
 * It initializes the game environment, including setting up the board, players, and the tournament.
 * This activity manages the lifecycle of the game state and orchestrates the rendering of the UI.
 */
class MainActivity : ComponentActivity() {
    //declaring variables to keep track of changes, mutable state is chosen for the board to signify that this value can be altered
    private lateinit var boardState: MutableState<Board>
    //manage rounds within a tournament
    private lateinit var tournament: Tournament

    //private var computerPlayer: ComputerPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //create a new board instance
        val board = Board()
        //assign board to a mutable state for recomposition
        boardState = mutableStateOf(board)
        //create players for the game
        val player1 = HumanPlayer("alice")
        val player2 = HumanPlayer("Bob")
        //initialize tournament with players
        tournament = Tournament(player1, player2)

        setContent {
            //apply app theme
            LinesOfActionTheme {
                //set up UI that fills entire screen
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //pass in parameters to initialize game playing
                    GameContent(boardState,tournament)
                }
            }
        }
    }
}


/**
 * Main content function for the game, handling different states like the main menu, game play, and game over scenarios.
 * It manages transitions between game states based on user interactions and game logic outcomes.
 *
 * @param board A mutable state holder for the current game board to facilitate updates and UI recomposition.
 * @param tournament An instance of Tournament managing the rounds and overall game state.
 */
@Composable
fun GameContent(board: MutableState<Board>, tournament: Tournament) {
    //track current state of the game
    var gameState by remember { mutableStateOf(GameState.Menu) }
    //track clicks, set to -1 to indicate there are no selections
    var selectedRow by remember { mutableStateOf(-1) }
    var selectedCol by remember { mutableStateOf(-1) }
    //initialize a round with human players
    val round = remember { mutableStateOf(Round(HumanPlayer("Human"), HumanPlayer("Computer"), null,null,null,null,null,null)) }
    //create player objects
    val player1 = HumanPlayer()
    val player2 = HumanPlayer()
    //used to trigger a recomposition of the board
    var recomposeTrigger by remember { mutableStateOf(false) }
    //val tournament = remember { mutableStateOf(Tournament(HumanPlayer("Alice"),HumanPlayer("Bob"))) }

    //handling different game states
    when (gameState) {
        //display game menu when state is set to menu
        GameState.Menu -> MainMenu { option ->
            when (option) {
                //if player vs player is chosen switch state to coin toss
                GameOption.PlayerVsPlayer -> {
                    gameState = GameState.CoinToss
                }
                //not yet implemented
                GameOption.PlayerVsComputer -> {
                    //computerPlayer = ComputerPlayer("Computer")
                    gameState = GameState.CoinToss
                }
                //load in games
                GameOption.LoadGame -> {
                    gameState = GameState.Load
                }

                else -> throw IllegalStateException("Unhandled game option")
            }
        }
        // determine starting player based on coin toss result
        GameState.CoinToss -> CoinTossDialog { tossResult ->
            val startingPlayer = round.value.determineStartingPlayer(tossResult)
            round.value.setCurrentPlayer(startingPlayer)
            round.value.startGame(startingPlayer, tossResult)
            //switch to playing game state to bring up board and commence run of game
            gameState = GameState.Playing
        }
        //pass in game board to playing state
        GameState.Playing -> GameBoard(
            //initialize board with necessary values
            initialBoard = board.value,
            round = round.value,
            tournament = tournament,
            selectedRow = selectedRow,
            selectedCol = selectedCol,
            //function that handles clicking of cells
            onCellClicked = { row, col ->
                //check if there is a selected piece
                if (selectedRow >= 0 && selectedCol >= 0) {
                    // attempt to move piece
                    if (round.value.nextMove(selectedRow, selectedCol, row, col)) {
                        //trigger a recomposition for the ui
                        recomposeTrigger = !recomposeTrigger
                        //reset selection
                        selectedRow = -1
                        selectedCol = -1
                        //check if move triggered game end scenario
                        if (round.value.checkForRoundCompletion()) {
                            //display winner, used for debugging purposes
                            round.value.showRoundWinner()
                            //set winner
                            val winner = round.value.getRoundWinner()
                            //record winner
                            tournament.recordWin(winner)
                            //switch game state
                            gameState = GameState.GameOver
                        }
                    }
                } else {
                    //no piece is selected
                    selectedRow = row
                    selectedCol = col
                }
            },
            onRestart = {
                gameState = GameState.ContinuePlaying
            },
            onExit = {
                gameState = GameState.Menu
            },
            onContinue = {
                gameState = GameState.ContinuePlaying
            }
        )
        //display round results
        GameState.GameOver -> GameOverDialog(
            //set variables
            winner = round.value.getRoundWinner().getName(),

            round = round.value,
            //handle states depending on user choice
            onContinue = {
                gameState = GameState.ContinuePlaying
            },
            onRestart = {
                gameState = GameState.Playing // Reset to the playing state or handle as needed
            },
            onExit = {
                println("Exiting to menu. Current gameState: $gameState")
                println(
                    "Tournament Results: ${
                        tournament.determineOverallWinner()?.getName() ?: "No clear winner"
                    }"
                )
                gameState = GameState.Menu
                println("New gameState: $gameState")
                recomposeTrigger = !recomposeTrigger
            }
        )

        GameState.ContinuePlaying -> {
            // Reset or reinitialize game board for new round with previous round's winner
            //val newRound = Round(round.getRoundWinner(), round.getRoundLoser())
            //newRound.startGame(newRound.getRoundWinner(), null)  // Assuming we start directly without a coin toss
            //board.value = newRound.getGameBoard()
            //gameState = GameState.Playing
            val newRound = Round(round.value.getPlayer1(), round.value.getPlayer2(), round.value.getRoundWinner(), null,round.value.getPlayer1Score(),round.value.getPlayer2Score(),Round.getWinsForPlayer1(),Round.getWinsForPlayer2())
            val newBoard = Board()
            newBoard.resetBoard()
            //newRound.startGame(
                //newRound.getRoundWinner(),
                //null
            //)  // Assuming we start directly without a coin toss

            // Reset the board for the new round
            board.value = newBoard
            round.value = newRound
            // Reset selection states
            selectedRow = -1
            selectedCol = -1

            // Reset any other states if necessary
            recomposeTrigger = !recomposeTrigger  // Toggle to force recomposition if necessary

            // Update the game state to start playing the new round
            gameState = GameState.Playing

        }

        GameState.Load -> {
            //load in attributes of a loaded in board
            LoadGameOptions { selectedCase ->
                when (selectedCase) {
                    1 -> {
                        val newRound = Round(round.value.getPlayer1(), round.value.getPlayer2(), null,1,null,-2,0,1)
                        val newBoard = Board()
                        newBoard.case1Board()

                        //reset the board for the new round
                        board.value = newBoard
                        round.value = newRound
                        //reset clicks
                        selectedRow = -1
                        selectedCol = -1

                        //reset all states
                        recomposeTrigger = !recomposeTrigger

                        //update the game state to start playing the new round
                        gameState = GameState.Playing
                    }
                    2->{
                        val newRound = Round(round.value.getPlayer1(), round.value.getPlayer2(),null ,2,0,-2,0,1)
                        val newBoard = Board()
                        newBoard.case2Board()

                        //reset the board for the new round
                        board.value = newBoard
                        round.value = newRound
                        //reset click
                        selectedRow = -1
                        selectedCol = -1

                        //reset all states
                        recomposeTrigger = !recomposeTrigger

                        //update the game state to start playing the new round
                        gameState = GameState.Playing
                    }
                    3->{
                        val newRound = Round(round.value.getPlayer1(), round.value.getPlayer2(), null,3,2,-1,1,1)
                        val newBoard = Board()
                        newBoard.case3Board()

                        //reset the board for the new round
                        board.value = newBoard
                        round.value = newRound
                        //reset clicks
                        selectedRow = -1
                        selectedCol = -1

                        //reset all states
                        recomposeTrigger = !recomposeTrigger  // Toggle to force recomposition if necessary

                        //update the game state to start playing the new round
                        gameState = GameState.Playing
                    }
                    4->{
                        val newRound = Round(round.value.getPlayer1(), round.value.getPlayer2(), null,4,-1,2,0,1)
                        val newBoard = Board()
                        newBoard.case4Board()

                        //reset the board for new round
                        board.value = newBoard
                        round.value = newRound
                        //reset clicks
                        selectedRow = -1
                        selectedCol = -1

                        // reset all states
                        recomposeTrigger = !recomposeTrigger

                        // update game state
                        gameState = GameState.Playing
                    }
                    5->{
                        val newRound = Round(round.value.getPlayer1(), round.value.getPlayer2(),null,5,0,-2,0,1)
                        val newBoard = Board()
                        newBoard.case5Board()

                        //reset board
                        board.value = newBoard
                        round.value = newRound
                        //reset clicks
                        selectedRow = -1
                        selectedCol = -1

                        //reset all states
                        recomposeTrigger = !recomposeTrigger

                        //update game state
                        gameState = GameState.Playing
                    }

                }
                gameState = GameState.Playing
            }
        }
    }
}

//Handles game states
private enum class GameState {
    Menu,
    CoinToss,
    Playing,
    GameOver,
    ContinuePlaying,
    Load,
}




