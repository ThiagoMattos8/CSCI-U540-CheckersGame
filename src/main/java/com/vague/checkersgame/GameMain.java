package com.vague.checkersgame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class GameMain extends Application {

    private Board board;
    private GameLogic gameLogic;
    private Piece[][] pieces;
    private Label turnLabel;

    @Override
    public void start(Stage primaryStage) {
        gameLogic = new GameLogic();

        board = new Board(8);
        board.displayBoard();

        // Create the starting pieces once when the game begins.
        pieces = gameLogic.createPieces(board);

        // Add a click action to every square on the board.
        // Click a piece first, then click an empty square to move it there.
        for (int row = 0; row < board.size; row++) {
            for (int col = 0; col < board.size; col++) {
                final int clickedRow = row;
                final int clickedCol = col;
                board.setTileClickHandler(clickedRow, clickedCol,
                        event -> gameLogic.handleTileClick(clickedRow, clickedCol, board));
            }
        }

        BorderPane root = new BorderPane();
        root.setCenter(board.gameBoard);
        Scene scene = new Scene(root);

        primaryStage.setTitle("Checkers Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        //Current Player Label
        turnLabel = new Label("Current Player: Red");
        //Reset Button Label
        Button resetButton = new Button("Reset Game");
        //HBox (top bar)
        HBox topBar = new HBox(20);
        topBar.getChildren().addAll(turnLabel, resetButton);
        root.setTop(topBar);
        //Give board access to label if needed
        board.setTurnLabel(turnLabel);
        //Reset logic
        resetButton.setOnAction(e -> {
            board.clearBoard();                      // clear UI
            pieces = gameLogic.resetGame(board);    // reset logic + pieces
            setupTileHandlers();
            turnLabel.setText("Current Player: Red");
        });
    }

    //Reset Logic
    //Reattach Click Logic after Reset
    private void setupTileHandlers() {
        for (int row = 0; row < board.size; row++) {
            for (int col = 0; col < board.size; col++) {
                final int r = row;
                final int c = col;

                board.setTileClickHandler(r, c,
                        event -> gameLogic.handleTileClick(r, c, board));
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
