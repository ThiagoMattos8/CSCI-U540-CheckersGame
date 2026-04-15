package com.vague.checkersgame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class GameMain extends Application {

    private Board board;
    private GameLogic gameLogic;
    private BorderPane root;
    private StackPane boardContainer;

    private Label turnLabel;
    private Label statusLabel;
    private Label moveCounterLabel;

    private boolean gameOver  = false;
    private boolean aiThinking = false;   // blocks human clicks while AI is working
    private CheckersAI ai = null;         // null = 2-player mode

    @Override
    public void start(Stage primaryStage) {
        gameLogic = new GameLogic();
        board = new Board(8);
        board.displayBoard();
        gameLogic.createPieces(board);

        root = new BorderPane();
        boardContainer = new StackPane();
        boardContainer.getChildren().add(new Group(board.gameBoard));
        root.setCenter(boardContainer);

        // Labels
        turnLabel        = new Label("Current Player: Red");
        statusLabel      = new Label("Status: Game Ready");
        moveCounterLabel = new Label("Moves: 0");

        // Mode selector — determines whether AI is active and its strength
        ComboBox<String> modeBox = new ComboBox<>();
        modeBox.getItems().addAll("2 Player", "vs AI (Easy)", "vs AI (Medium)", "vs AI (Hard)");
        modeBox.setValue("2 Player");
        modeBox.setStyle("-fx-font-weight: bold;");

        // Buttons
        String btnStyle = "-fx-background-color: #F4D03F; -fx-font-weight: bold; -fx-padding: 7 14;";
        Button startButton   = new Button("Start Game");
        Button resetButton   = new Button("Reset Game");
        Button forfeitButton = new Button("Forfeit Match");
        startButton.setStyle(btnStyle);
        resetButton.setStyle(btnStyle);
        forfeitButton.setStyle(btnStyle);

        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().addAll(
                turnLabel, moveCounterLabel, statusLabel,
                modeBox, startButton, resetButton, forfeitButton);
        root.setTop(topBar);
        board.setTurnLabel(turnLabel);

        setupTileHandlers();

        startButton.setOnAction(e -> {
            ai = buildAI(modeBox.getValue());
            startNewGame();
            statusLabel.setText("Status: Game Started");
            startButton.setDisable(true);
        });

        resetButton.setOnAction(e -> {
            ai = buildAI(modeBox.getValue());
            startNewGame();
            statusLabel.setText("Status: Game Reset");
            startButton.setDisable(false);
        });

        forfeitButton.setOnAction(e -> {
            if (gameOver) return;
            gameOver = true;
            showEndScreen(gameLogic.getCurrentPlayerName() + " forfeits.");
            statusLabel.setText("Status: Match Forfeited");
        });

        Scene scene = new Scene(root);
        primaryStage.setTitle("Checkers Game");
        primaryStage.setScene(scene);
        primaryStage.show();
        updateHighlights();
    }

    // ── AI ────────────────────────────────────────────────────────────────────

    private CheckersAI buildAI(String mode) {
        switch (mode) {
            case "vs AI (Easy)":   return new CheckersAI(CheckersAI.Difficulty.EASY);
            case "vs AI (Medium)": return new CheckersAI(CheckersAI.Difficulty.MEDIUM);
            case "vs AI (Hard)":   return new CheckersAI(CheckersAI.Difficulty.HARD);
            default:               return null;
        }
    }

    /**
     * Run the AI on a background thread so the UI doesn't freeze.
     * GameLogic drives chain jumps internally: we simulate one hop at a time
     * with a pair of handleTileClick calls, then check if it's still the AI's
     * turn (meaning a chain is continuing) and repeat.
     */
    private void triggerAIMove() {
        if (ai == null || gameOver || aiThinking) return;
        aiThinking = true;
        statusLabel.setText("Status: AI thinking…");

        Thread t = new Thread(() -> {
            try { Thread.sleep(350); } catch (InterruptedException ignored) {}

            // Loop handles chain jumps: after each hop GameLogic keeps currentPlayer
            // as Black if a further jump is available, so we keep going.
            while (!gameOver && gameLogic.getCurrentPlayerName().equals("Black")) {
                CheckersAI.Move move = ai.getBestMove(gameLogic.getPieces());
                if (move == null) break;

                // Apply on the JavaFX thread and block until done.
                Platform.runLater(() -> {
                    gameLogic.handleTileClick(move.fromRow, move.fromCol, board);
                    gameLogic.handleTileClick(move.toRow,   move.toCol,   board);
                    moveCounterLabel.setText("Moves: " + gameLogic.getMoveCount());
                    updateHighlights();

                    if (gameLogic.isGameOver()) {
                        gameOver = true;
                        statusLabel.setText("Status: Match Finished");
                        showEndScreen(gameLogic.getGameOverMessage());
                    }
                });

                // Wait for the UI thread to finish before we check the turn again.
                try { Thread.sleep(400); } catch (InterruptedException ignored) {}
            }

            Platform.runLater(() -> {
                aiThinking = false;
                if (!gameOver) statusLabel.setText("Status: In Progress");
            });
        });

        t.setDaemon(true);
        t.start();
    }

    // ── Game lifecycle ────────────────────────────────────────────────────────

    private void startNewGame() {
        gameOver   = false;
        aiThinking = false;

        board.clearBoard();
        gameLogic.resetGame(board);
        root.setCenter(boardContainer);

        setupTileHandlers();

        turnLabel.setText("Current Player: Red");
        moveCounterLabel.setText("Moves: 0");
        updateHighlights();
    }

    private void setupTileHandlers() {
        for (int row = 0; row < board.size; row++) {
            for (int col = 0; col < board.size; col++) {
                final int r = row, c = col;
                board.setTileClickHandler(r, c, event -> {
                    // Block input while the game is over or the AI is thinking.
                    if (gameOver || aiThinking) return;
                    // In AI mode, block clicks during the AI's turn (Black = color 0).
                    if (ai != null && gameLogic.getCurrentPlayerName().equals("Black")) return;

                    gameLogic.handleTileClick(r, c, board);
                    moveCounterLabel.setText("Moves: " + gameLogic.getMoveCount());
                    statusLabel.setText("Status: In Progress");
                    updateHighlights();

                    if (gameLogic.isGameOver()) {
                        gameOver = true;
                        statusLabel.setText("Status: Match Finished");
                        showEndScreen(gameLogic.getGameOverMessage());
                        return;
                    }

                    // Hand off to the AI if it's now Black's turn.
                    if (ai != null && gameLogic.getCurrentPlayerName().equals("Black")) {
                        triggerAIMove();
                    }
                });
            }
        }
    }

    private void showEndScreen(String message) {
        Label endLabel = new Label(message);
        endLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Button newGameButton = new Button("New Game");
        newGameButton.setStyle("-fx-background-color: #F4D03F; -fx-font-weight: bold; -fx-padding: 7 14;");
        newGameButton.setOnAction(e -> startNewGame());

        VBox endScreen = new VBox(16, endLabel, newGameButton);
        endScreen.setAlignment(Pos.CENTER);
        endScreen.setPadding(new Insets(20));

        turnLabel.setText("Game Over");
        root.setCenter(endScreen);
    }

    private void updateHighlights() {
        board.clearHighlights();
        var destinations = gameLogic.getSelectedPieceDestinations(board);
        if (!destinations.isEmpty()) {
            board.highlightDestinationSquares(destinations);
            return;
        }
        board.highlightSelectablePieces(gameLogic.getSelectablePiecePositions(board));
    }

    public static void main(String[] args) { launch(args); }
}