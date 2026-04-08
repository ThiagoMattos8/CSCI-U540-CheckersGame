package com.vague.checkersgame;

import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;

public class Board {
    int size;
    Pane gameBoard = new Pane();
    private static final double TILE = 70;
    private static final Color LIGHT = Color.web("#F0D9B5");
    private static final Color DARK = Color.web("#B58863");
    int[][] coords;
    Rectangle[][] tiles;

    //Player Turn label
    //Updater and Setter
    private Label turnLabel;
    public void setTurnLabel(Label label) {
        this.turnLabel = label;
    }
    public void updateTurnDisplay(String text) {
        if (turnLabel != null) {
            turnLabel.setText(text);
        }
    }

    public Board(int size) {
        this.size = size;
        this.coords = new int[size][size];
        this.tiles = new Rectangle[size][size];
    }

    public PieceView addPiece(int row, int col, Piece model) {
        PieceView view = new PieceView(model, TILE);
        view.setTranslateX(col * TILE + TILE / 2.0);
        view.setTranslateY(row * TILE + TILE / 2.0);
        gameBoard.getChildren().add(view);
        return view;
    }

    public void displayBoard() {
        gameBoard.setPrefSize(size * TILE, size * TILE);

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Rectangle tile = new Rectangle(c * TILE, r * TILE, TILE, TILE);
                tile.setFill((r + c) % 2 == 0 ? LIGHT : DARK);
                tiles[r][c] = tile;
                gameBoard.getChildren().add(tile);
            }
        }
    }

    // This redraws the pieces using the current board data.
    // We leave the squares alone and only remove old piece views.
    public void redrawPieces(Piece[][] pieces) {
        gameBoard.getChildren().removeIf(node -> node instanceof PieceView);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (pieces[row][col] != null && pieces[row][col].getIsTaken() == 0) {
                    addPiece(row, col, pieces[row][col]);
                }
            }
        }
    }

    // This lets the main game attach a click action to each square.
    public void setTileClickHandler(int row, int col, EventHandler<javafx.scene.input.MouseEvent> handler) {
        tiles[row][col].setOnMouseClicked(handler);
    }

    //For Resetting Board
    public void clearBoard() {
        gameBoard.getChildren().clear();
        displayBoard(); // redraw empty grid
    }
    public void removePiece(Piece piece){
        gameBoard.getChildren().remove(piece);
    }
}
