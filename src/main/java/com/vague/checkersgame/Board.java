package com.vague.checkersgame;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class Board {
    int size;
    Pane gameBoard=new Pane();
    private static final double TILE  = 70;
    private static final Color  LIGHT = Color.web("#F0D9B5");
    private static final Color  DARK  = Color.web("#B58863");
    int[][] coords=new int[size][size]; //[row][column]


    public Board(int size){
        this.size=size;
    }
    public PieceView addPiece(int row, int col,Piece model){
        PieceView view = new PieceView(model, TILE);
        view.setTranslateX(col * TILE + TILE / 2.0);
        view.setTranslateY(row * TILE + TILE / 2.0);
        gameBoard.getChildren().add(view);
        return view;
    }
    public void displayBoard(){
        gameBoard.setPrefSize(size * TILE, size * TILE);
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Rectangle tile = new Rectangle(c * TILE, r * TILE, TILE, TILE);
                tile.setFill((r + c) % 2 == 0 ? LIGHT : DARK);
                gameBoard.getChildren().add(tile);
            }
        }
    }
}
