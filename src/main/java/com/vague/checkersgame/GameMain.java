package com.vague.checkersgame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class GameMain extends Application {

    private Board board;
    private GameLogic gameLogic;
    private Piece[][] pieces;

    private Label turnLabel;
    private Label statusLabel;
    private Label moveCounterLabel;

    private boolean gameOver = false;

    @Override
    public void start(Stage primaryStage) {
        gameLogic = new GameLogic();

        board = new Board(8);
        board.displayBoard();

        pieces = gameLogic.createPieces(board);

        BorderPane root = new BorderPane();

        StackPane boardContainer = new StackPane();
        Group boardGroup = new Group(board.gameBoard);
        boardContainer.getChildren().add(boardGroup);
        root.setCenter(boardContainer);

        // Labels
        turnLabel = new Label("Current Player: Red");
        statusLabel = new Label("Status: Game Ready");
        moveCounterLabel = new Label("Moves: 0");

        // Buttons
        Button startButton = new Button("Start Game");
        Button resetButton = new Button("Reset Game");
        Button forfeitButton = new Button("Forfeit Match");

        String buttonStyle = "-fx-background-color: #F4D03F; -fx-font-weight: bold; -fx-padding: 7 14;";
        startButton.setStyle(buttonStyle);
        resetButton.setStyle(buttonStyle);
        forfeitButton.setStyle(buttonStyle);

        // Top bar layout
        HBox topBar = new HBox(15);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.getChildren().addAll(
                turnLabel,
                moveCounterLabel,
                statusLabel,
                startButton,
                resetButton,
                forfeitButton
        );

        root.setTop(topBar);

        board.setTurnLabel(turnLabel);

        setupTileHandlers();

        // Start Game
        startButton.setOnAction(e -> {
            startNewGame();
            statusLabel.setText("Status: Game Started");
            startButton.setDisable(true);
        });

        // Reset Game
        resetButton.setOnAction(e -> {
            startNewGame();
            statusLabel.setText("Status: Game Reset");
            startButton.setDisable(false);
        });

        // Forfeit
        forfeitButton.setOnAction(e -> {
            gameOver = true;
            statusLabel.setText("Status: Match Forfeited");
            turnLabel.setText("Game Over");
        });

        Scene scene = new Scene(root);

        primaryStage.setTitle("Checkers Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startNewGame() {
        gameOver = false;

        board.clearBoard();
        pieces = gameLogic.resetGame(board);

        setupTileHandlers();

        turnLabel.setText("Current Player: Red");
        moveCounterLabel.setText("Moves: 0");
    }

    private void setupTileHandlers() {
        for (int row = 0; row < board.size; row++) {
            for (int col = 0; col < board.size; col++) {
                final int r = row;
                final int c = col;

                board.setTileClickHandler(r, c, event -> {
                    if (gameOver) {
                        return;
                    }

                    gameLogic.handleTileClick(r, c, board);
                    moveCounterLabel.setText("Moves: " + gameLogic.getMoveCount());
                    statusLabel.setText("Status: In Progress");
                });
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}