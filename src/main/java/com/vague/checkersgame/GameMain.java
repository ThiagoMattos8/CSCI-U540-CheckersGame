package com.vague.checkersgame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GameMain extends Application {

    private Board board;
    private GameLogic gameLogic;
    private Piece[][] pieces;

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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
