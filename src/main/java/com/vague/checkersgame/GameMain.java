package com.vague.checkersgame;

//Imports

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


//Main
public class GameMain extends Application {

    //Import Board and GameLogic
    private Board board;
    private GameLogic gameLogic;
    private Piece[][] pieces;

    @Override
    public void start(Stage primaryStage)
    {
        //Initialize Logic
        gameLogic = new GameLogic();

        //Create Board
        board = new Board(8);
        board.displayBoard();

        //Initialize Pieces on Board
        pieces = gameLogic.createPieces(board);

        //Root
        BorderPane root = new BorderPane();
        root.setCenter(board.gameBoard);

        //Scene
        Scene scene = new Scene(root);

        //Stage
        primaryStage.setTitle("Checkers Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //Future Methods

    //Game Launch
    public static void main(String[] args)
    {
        launch(args);
    }
}
