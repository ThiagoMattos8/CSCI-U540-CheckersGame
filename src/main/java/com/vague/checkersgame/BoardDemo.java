package com.vague.checkersgame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BoardDemo extends Application {
    public void start(Stage stage) {
        Board board=new Board(8);//18x18 board
        board.displayBoard();
        final boolean[] piecesPlaced = {false};

        /*
        add red pieces at bottom
        for coords<size, add piece to every other space
        consume one on next row, do it again

        skip 2 lines
        do the same for black pieces

         */
        Button placePieceBtn=new Button("Place pieces to starting position");
        placePieceBtn.setStyle("-fx-background-color: #F4D03F; -fx-font-weight: bold; -fx-padding: 7 14;");
        placePieceBtn.setOnAction(e -> {
            if(!piecesPlaced[0]) {
                for (int x = 0; x < 3; x++) { //add black pieces at top
                    for (int y = 0; y < board.size; y++)
                        if (y % 2 == 0 && x % 2 == 1)
                            board.addPiece(x, y, new Piece(0, x, y, 0, 0));
                        else if (y % 2 == 1 && x % 2 == 0)
                            board.addPiece(x, y, new Piece(0, x, y, 0, 0));
                }
                for (int x = board.size - 3; x < board.size; x++) { //add red pieces at bottom
                    for (int y = 0; y < board.size; y++)
                        if (y % 2 == 0 && x % 2 == 1)
                            board.addPiece(x, y, new Piece(1, x, y, 0, 0));
                        else if (y % 2 == 1 && x % 2 == 0)
                            board.addPiece(x, y, new Piece(1, x, y, 0, 0));
                }
                placePieceBtn.setText("Close Demo");    //place pieces, change button to close button
                piecesPlaced[0] = true;
            }else {javafx.application.Platform.exit();} //if button clicked after pieces place, close
        });
        VBox root = new VBox(20,
                new Label("Board Demo") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: bold;"); }},
                board.gameBoard,
                placePieceBtn

        );


        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #FAFAFA;");

        stage.setScene(new Scene(root));
        stage.setTitle("Board Demo");
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}

